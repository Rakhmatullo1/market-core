package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.ProposalService;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<ProposalDto>> findAll() {
        return ResponseEntity.ok(proposalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(proposalService.findById(id));
    }

    @PostMapping("/decision/{id}")
    public ResponseEntity<ProposalDto> approveOrReject(@PathVariable Long id, @RequestParam boolean isApproved){
        return ResponseEntity.ok(proposalService.approveOrReject(id, isApproved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalDto> update(@PathVariable Long id, @RequestBody ProposalRequestDto requestDto) {
        return ResponseEntity.ok(proposalService.updateProposal(id, requestDto));
    }
}
