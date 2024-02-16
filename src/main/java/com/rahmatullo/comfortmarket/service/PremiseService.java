package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;

public interface PremiseService {
    PremiseDto createPremise(PremiseRequestDto premiseRequestDto);

    PremiseDto addProductsToPremise(Long id, ProductRequestDto productRequestDto);
}
