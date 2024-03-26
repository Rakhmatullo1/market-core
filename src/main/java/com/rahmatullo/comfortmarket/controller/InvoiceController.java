package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.InvoiceService;
import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> create(@RequestBody InvoiceRequestDto requestDto) {
        return ResponseEntity.ok(invoiceService.create(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAll(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size){
        return ResponseEntity.ok(invoiceService.getAll(PageRequest.of(page, size)));
    }

    @PostMapping("/make-decision/{id}")
    public ResponseEntity<MessageDto> makeDecision(@PathVariable Long id, @RequestParam boolean isApproved ){
        return ResponseEntity.ok(invoiceService.makeDecision(isApproved, id));
    }
}
