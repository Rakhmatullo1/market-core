package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.HistoryService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.ProposalService;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import com.rahmatullo.comfortmarket.service.enums.Action4Product;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;
    private final HistoryService historyService;
    private final AuthService authService;
    private final ProductMapper productMapper;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProposalDto> create(@Valid  @RequestBody ProposalRequestDto requestDto) {
        return ResponseEntity.ok(proposalService.create(requestDto));
    }

    @PostMapping("/decision/{id}")
    public ResponseEntity<ProposalDto> approveOrReject(@PathVariable Long id, @RequestParam boolean isApproved, @RequestParam Long premiseId){

        ProposalDto proposalDto = proposalService.findById(id);

        ProductDto productDto = toProductDto(proposalDto.getProduct().getId());
        Map<Object, Object> details = new HashMap<>();

        Action4Product action = Arrays.stream(Action4Product.values()).filter(a->a.name().contains(proposalDto.getAction().name())).findFirst().get();

        details.put("action", isApproved ? action : null );
        details.put("description", String.format("%s, %s product %s on premise %s",productDto.getName() ,productDto.getBarcode() ,isApproved ? action.name() : "rejected " +action.name(),  premiseId));
        details.put("id", proposalDto.getProduct().getId());

        return ResponseEntity.ok(historyService.createHistory(proposalService.approveOrReject(id, isApproved, premiseId), details));
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

    public ProductDto toProductDto(Long id) {
        return productMapper.toProductDto(productService.toProduct(id, authService.getOwner()));
    }
}
