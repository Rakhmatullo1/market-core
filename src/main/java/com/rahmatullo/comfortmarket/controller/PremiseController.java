package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<PremiseDto>> findAll(){
        return ResponseEntity.ok(premiseService.findAll());
    }
}
