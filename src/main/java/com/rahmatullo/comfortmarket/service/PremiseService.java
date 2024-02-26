package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;

import java.util.List;

public interface PremiseService {
    PremiseDto createPremise(PremiseRequestDto premiseRequestDto);

    PremiseDto addProductsToPremise(Long id, ProductRequestDto productRequestDto);

    List<PremiseDto> findAll();

    PremiseDto addWorkers2Premise(Long id, Long userId);

    Premise toPremise(Long id);

    PremiseDto updatePremise(Long id, PremiseRequestDto premiseRequestDto);
}
