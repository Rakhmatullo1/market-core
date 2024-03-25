package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Invoice;
import com.rahmatullo.comfortmarket.repository.InvoiceRepository;
import com.rahmatullo.comfortmarket.repository.ProductDetailsRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.InvoiceService;
import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
import com.rahmatullo.comfortmarket.service.mapper.InvoiceMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductDetailsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
