package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Invoice;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.repository.InvoiceRepository;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductDetailsRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.InvoiceService;
import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductDetailsMapper productDetailsMapper;
    private final ProductDetailsRepository productDetailsRepository;
    private final InvoiceMapper invoiceMapper;
    private final AuthService authService;
    private final ProductMapper productMapper;

    private final PremiseRepository premiseRepository;

    @Override
    public InvoiceDto create(InvoiceRequestDto requestDto) {
        log.info("Requested to create new Invoice");
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
            Premise premise = invoice.getPremise();

            premise.getProducts().addAll(
                    invoice.getProductDetailsSet()
                            .stream()
                            .map(p->productMapper.toProduct(p, premise))
                            .collect(Collectors.toSet()));

            premiseRepository.save(premise);
        }

        invoiceRepository.save(invoice);
        return new MessageDto("Successfully " + (isApproved ? "approved":"rejected"));
    }

    private Invoice toInvoice(Long id) {
        return invoiceRepository.findByIdAndToUser(id, authService.getOwner()).orElseThrow(()->new NotFoundException("Invoice is not found"));
    }
}
