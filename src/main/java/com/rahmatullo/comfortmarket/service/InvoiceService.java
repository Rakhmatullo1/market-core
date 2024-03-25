package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {
    InvoiceDto create(InvoiceRequestDto requestDto);

    List<InvoiceDto> getAll(Pageable pageable);

    MessageDto makeDecision(boolean isApproved, Long invoiceId);
}
