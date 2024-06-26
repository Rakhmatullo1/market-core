package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.*;
import com.rahmatullo.comfortmarket.repository.*;
import com.rahmatullo.comfortmarket.service.InvoiceService;
import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.InvoiceStatus;
import com.rahmatullo.comfortmarket.service.enums.PremiseType;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.InvoiceMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductDetailsMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import com.rahmatullo.comfortmarket.service.utils.AuthUtils;
import com.rahmatullo.comfortmarket.service.utils.PremiseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;
    private final ProductMapper productMapper;
    private final AuthUtils authUtils;
    private final PremiseUtils premiseUtils;

    @Override
    public InvoiceDto create(InvoiceRequestDto requestDto) {
        log.info("Requested to create new Invoice");

        if(!Objects.equals(requestDto.getAction(), Action.IMPORT)){
            checkProducts(requestDto);
        }

        Invoice invoice = invoiceMapper.toInvoice(requestDto);
        invoice.setProductDetailsSet(getProductsSet(requestDto));
        invoice = invoiceRepository.save(invoice);
        log.info("Successfully created new invoice ");
        return invoiceMapper.toInvoiceDto(invoice);
    }

    @Override
    public List<InvoiceDto> getAll(Pageable pageable) {
        log.info("Requested to get all invoices");
        List<Invoice> invoices = invoiceRepository.findAllByToUser(authUtils.getOwner() ,pageable).stream().collect(Collectors.toList());
        invoices.sort((o1, o2) -> {
            if (o2.getDate().before(o1.getDate())) {
                return -1;
            } else if (o1.getDate().before(o2.getDate())) {
                return 1;
            } else {
                return 0;
            }
        });
        return invoices.stream().map(invoiceMapper::toInvoiceDto).toList();
    }

    @Override
    public InvoiceDto getById(Long id) {
        log.info("Requested to get by id {}", id);
        return invoiceMapper.toInvoiceDto(toInvoice(id));
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
            checkPremiseType(invoice);

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
        return invoiceRepository.findByIdAndToUser(id, authUtils.getOwner()).orElseThrow(()->new NotFoundException("Invoice is not found"));
    }

    private void addingProductsOnPremise(Invoice invoice) {
        log.info("Requested to add product on premise with invoice");
        Premise premise = invoice.getPremise();

        invoice.getProductDetailsSet().forEach(p->{
            Optional<Product> product = getProduct(p.getProductInfo().getBarcode());
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
        theProduct.setPrice(productDetails.getFinalPrice());
        Optional<Product> product = getProduct(theProduct.getBarcode());

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

        String count  = findCount(premise.getId(), theProduct).orElseThrow(()->new DoesNotMatchException("Something went wrong"));
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
        Premise premise = premiseUtils.getPremise(invoice.getPreviousId());

        invoice.getProducts().forEach(p->{
            ProductInfo productInfo = productInfoRepository.findByBarcode(p.getBarcode()).orElseThrow(()->new NotFoundException("Product is not found "));

            Product product = getProduct(productInfo.getBarcode())
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
        Premise premise = premiseUtils.getPremise(invoice.getPreviousId());

        setIncomeForOwner(invoice);

        invoice.getProductDetailsSet().forEach(p-> changeOnCount(p, premise));
    }

    private void setIncomeForOwner(Invoice invoice) {
        if(Objects.equals(invoice.getAction(), Action.EXPORT)) {
            User user = authUtils.getOwner();
            Double oldIncome = user.getIncome();

            if(Objects.isNull(user.getIncome())) {
                oldIncome = 0d;
            }

            Double result = oldIncome+ invoice.getOverallFinalPrice()-invoice.getOverallInitialPrice();
            user.setIncome(result);
            userRepository.save(user);
        }
    }

    private void changeOnCount(ProductDetails productDetails, Premise premise) {
        log.info("Requested change count of product on premise");
        Product product = getProduct(productDetails.getProductInfo().getBarcode())
                .orElseThrow(()->new NotFoundException("Product is not found on the premise"));

        String count = findCount(premise.getId(), product).orElseThrow(()->new DoesNotMatchException("Something went wrong"));
        product.getCount().remove(count);
        Long oldCount = getCountFromString(count);
        product.getCount().add(getFormattedString(premise, oldCount-productDetails.getCount()));

        productRepository.save(product);
        log.info("Successfully updated");
    }

    private Set<ProductDetails> getProductsSet(InvoiceRequestDto requestDto) {
        return requestDto.getProducts()
                .stream()
                .map(p-> productDetailsRepository.save(productDetailsMapper.toProductDetails(p)))
                .collect(Collectors.toSet());
    }

    private Optional<Product> getProduct(String barcode) {
        return  productRepository.findByOwnerAndBarcode(authUtils.getOwner(), barcode);
    }

    private void checkPremiseType(Invoice invoice) {
        if(Objects.equals(invoice.getAction(), Action.IMPORT)) {
            checkingPremiseType(invoice.getPremise().getId());
        }

        if(Objects.equals(invoice.getAction(), Action.EXPORT)) {
            checkingPremiseType(invoice.getPreviousId());
        }
    }

    private void checkingPremiseType(Long id){
        if(Objects.equals(premiseUtils.getPremise(id).getType(), PremiseType.BIN)){
            log.warn("You cannot export or import product to bin type premise");
            throw new DoesNotMatchException("Premise type cannot be equal to Bin");
        }
    }
}
