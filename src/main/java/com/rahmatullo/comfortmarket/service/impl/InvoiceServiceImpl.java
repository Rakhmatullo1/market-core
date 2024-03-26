package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.*;
import com.rahmatullo.comfortmarket.repository.*;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.InvoiceService;
import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.InvoiceStatus;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.InvoiceMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductDetailsMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rahmatullo.comfortmarket.service.mapper.ProductMapper.getFormattedString;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductDetailsMapper productDetailsMapper;
    private final ProductDetailsRepository productDetailsRepository;
    private final ProductInfoRepository productInfoRepository;
    private final ProductRepository productRepository;
    private final PremiseRepository premiseRepository;
    private final InvoiceMapper invoiceMapper;
    private final ProductMapper productMapper;
    private final AuthService authService;

    @Override
    public InvoiceDto create(InvoiceRequestDto requestDto) {
        log.info("Requested to create new Invoice");

        if(!Objects.equals(requestDto.getAction(), Action.IMPORT)){
            checkProducts(requestDto);
        }

        Invoice invoice = invoiceMapper.toInvoice(requestDto);
        invoice.setProductDetailsSet(
                requestDto.getProducts()
                        .stream()
                        .map(p-> productDetailsRepository.save(productDetailsMapper.toProductDetails(p)))
                        .collect(Collectors.toSet()));
        invoice = invoiceRepository.save(invoice);
        log.info("Successfully created new invoice ");
        return invoiceMapper.toInvoiceDto(invoice);
    }

    @Override
    public List<InvoiceDto> getAll(Pageable pageable) {
        log.info("Requested to get all invoices");
        List<Invoice> invoices = invoiceRepository.findAllByToUser(authService.getOwner() ,pageable).getContent();
        return invoices.stream().map(invoiceMapper::toInvoiceDto).toList();
    }

    @Override
    public MessageDto makeDecision(boolean isApproved, Long invoiceId) {
        log.info("Requested to approve or Reject invoice");
        Invoice invoice = toInvoice(invoiceId);

        if(!Objects.equals(InvoiceStatus.PENDING, invoice.getStatus())) {
            log.warn("invoice already approved or rejected");
            throw new DoesNotMatchException("invoice already approved or rejected");
        }

        invoice.setStatus(isApproved ? InvoiceStatus.ACCEPTED : InvoiceStatus.REJECTED);

        if(isApproved) {
            if(!Objects.equals(invoice.getAction(), Action.IMPORT)){
                subtractCountOnProduct(invoice);
            }

            if(!Objects.equals(invoice.getAction(), Action.EXPORT)){
                addingProductsOnPremise(invoice);
            }
        }

        invoiceRepository.save(invoice);
        return new MessageDto("Successfully " + (isApproved ? "approved":"rejected"));
    }

    private Invoice toInvoice(Long id) {
        return invoiceRepository.findByIdAndToUser(id, authService.getOwner()).orElseThrow(()->new NotFoundException("Invoice is not found"));
    }

    private void addingProductsOnPremise(Invoice invoice) {
        log.info("Requested to add product on premise with invoice");
        Premise premise = invoice.getPremise();

        invoice.getProductDetailsSet().forEach(p->{
            Optional<Product> product =productRepository.findByOwnerAndBarcode(authService.getOwner(), p.getProductInfo().getBarcode());
            if(product.isEmpty()) {
                createProductsOnPremise(p, premise);
            }

            if(product.isPresent()) {
                Product theProduct = product.get();
                if(theProduct.getPremise().stream().anyMatch(premise1 -> Objects.equals(premise1,premise))){
                    updateProductsOnPremise(theProduct, p, premise);
                }

                if(theProduct.getPremise().stream().noneMatch(premise1 -> Objects.equals(premise1, premise))){
                    createProductsOnPremise(p, premise);
                }
            }
        });
    }

    private void createProductsOnPremise(ProductDetails productDetails, Premise premise) {
        log.info("Requested to create product on premise {}", premise.getId());
        Product theProduct =productMapper.toProduct(productDetails);

        Optional<Product> product = productRepository.findByOwnerAndBarcode(authService.getOwner(), theProduct.getBarcode());

        if(product.isPresent()){
            theProduct.setId(product.get().getId());
            theProduct.setPremise(product.get().getPremise());
            theProduct.setCount(product.get().getCount());
        }

        theProduct.getCount().add(getFormattedString(premise, productDetails.getCount()));
        theProduct.getPremise().add(premise);

        productRepository.save(theProduct);
        log.info("Successfully created");
    }

    private void updateProductsOnPremise(Product theProduct, ProductDetails productDetails, Premise premise){
        log.info("Requested to update product {} on premise {}", theProduct.getId(), premise.getId());

        Optional<String> countOnPremise = findCount(premise.getId(), theProduct);

        if(countOnPremise.isEmpty()) {
            log.warn("count is not found");
            throw new DoesNotMatchException("Something went wrong");
        }

        String count = countOnPremise.get();

        updatingCountOnProduct(count, productDetails.getCount(), premise, theProduct);

        productRepository.save(theProduct);
        log.info("Successfully updated");
    }

    private void updatingCountOnProduct(String count, Long newCount, Premise premise, Product theProduct){
        long resultOne =getCountFromString(count)  + newCount;
        theProduct.getCount().remove(count);
        theProduct.getCount().add(getFormattedString(premise, resultOne));
    }

    private Long getCountFromString(String count){
        return Long.parseLong(count.split(":")[1]);
    }

    private Optional<String> findCount(Long premiseId, Product product) {
        return  product.getCount().stream()
                .filter(c->Objects.equals(Long.parseLong(c.split(":")[0]), premiseId))
                .findFirst();
    }

    private void checkProducts(InvoiceRequestDto invoice) {
        log.info("Checking products which are belonging to premise");
        Premise premise = premiseRepository.findByOwnerAndId(authService.getOwner(), invoice.getPreviousId()).orElseThrow(()->new NotFoundException("Previous Premise is not found"));

        invoice.getProducts().forEach(p->{
            ProductInfo productInfo = productInfoRepository.findById(p.getProductId()).orElseThrow(()->new NotFoundException("Product is not found "));

            Product product = productRepository
                    .findByOwnerAndBarcode(authService.getOwner(), productInfo.getBarcode())
                    .orElseThrow(()->new NotFoundException("Product is not found on premise "+ premise.getName()));

            String oldCount = findCount(premise.getId(), product).orElseThrow(()-> {
                log.warn("count is not found on product {}", product.getId());
                throw new DoesNotMatchException("Something went wrong");
            });

            Long count = getCountFromString(oldCount);

            if(count<p.getCount()) {
                log.warn("Count on product {} is not enough for {}", product.getId(), invoice.getAction().name());
                throw new DoesNotMatchException("Not enough products "+ invoice.getAction().name());
            }
        });
    }

    private void subtractCountOnProduct(Invoice invoice) {
        log.info("Requested subtract products from previous premise");
        Premise premise = premiseRepository
                .findByOwnerAndId(authService.getOwner(), invoice.getPreviousId())
                .orElseThrow(()->new NotFoundException("Previous premise is not found"));

        invoice.getProductDetailsSet().forEach(p-> changeOnCount(p, premise));
    }

    private void changeOnCount(ProductDetails productDetails, Premise premise) {
        log.info("Requested change count of product on premise");
        Product product = productRepository.findByOwnerAndBarcode(authService.getOwner(), productDetails.getProductInfo().getBarcode()).orElseThrow(()->new NotFoundException("Product is not found on the premise"));

        String count = findCount(premise.getId(), product).orElseThrow(()->new DoesNotMatchException("Something went wrong"));
        product.getCount().remove(count);
        Long oldCount = getCountFromString(count);
        product.getCount().add(getFormattedString(premise, oldCount-productDetails.getCount()));

        productRepository.save(product);
        log.info("Successfully updated");
    }
}
