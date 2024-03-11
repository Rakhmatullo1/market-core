package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.ProposalService;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping
    public ResponseEntity<ProposalDto> create(@Valid  @RequestBody ProposalRequestDto requestDto) {
        return ResponseEntity.ok(proposalService.create(requestDto));
    }

    @PostMapping("/decision/{id}")
    public ResponseEntity<ProposalDto> approveOrReject(@PathVariable Long id, @RequestParam boolean isApproved, @RequestParam Long premiseId){
        return ResponseEntity.ok(proposalService.approveOrReject(id, isApproved, premiseId));
    }

    @GetMapping
    public ResponseEntity<List<ProposalDto>> findAll(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(proposalService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(proposalService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalDto> update(@PathVariable Long id, @RequestBody ProposalRequestDto requestDto) {
        return ResponseEntity.ok(proposalService.updateProposal(id, requestDto));
    }
}
