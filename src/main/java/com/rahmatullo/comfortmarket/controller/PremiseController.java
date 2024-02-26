package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/premise")
@RequiredArgsConstructor
public class PremiseController {

    private final PremiseService premiseService;

    @PostMapping()
    public ResponseEntity<PremiseDto> createPremise(@Valid @RequestBody PremiseRequestDto premiseRequestDto) {
        return ResponseEntity.ok(premiseService.createPremise(premiseRequestDto));
    }

    @PostMapping("/{id}/add-product")
    public ResponseEntity<PremiseDto> addProducts(@PathVariable Long id,@Valid @RequestBody ProductRequestDto productRequestDto){
        return ResponseEntity.ok(premiseService.addProductsToPremise(id, productRequestDto));
    }

    @PostMapping("/{id}/add_user/{userId}")
    public ResponseEntity<PremiseDto> addUsers(@PathVariable Long id, @PathVariable Long userId){
        return ResponseEntity.ok(premiseService.addWorkers2Premise(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<PremiseDto>> findAll(@RequestParam(required = false, defaultValue = "0") int page , @RequestParam(required = false, defaultValue = "10") int size){
        return ResponseEntity.ok(premiseService.findAll(PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PremiseDto> updatePremise(@PathVariable Long id,@Valid @RequestBody PremiseRequestDto premiseRequestDto) {
        return ResponseEntity.ok(premiseService.updatePremise(id, premiseRequestDto));
    }

}
