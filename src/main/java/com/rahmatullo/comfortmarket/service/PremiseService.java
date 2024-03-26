package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.request.PremiseRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface PremiseService {
    List<PremiseDto> findAll(PageRequest pageRequest);

    PremiseDto findById(Long id);

    PremiseDto createPremise(PremiseRequestDto premiseRequestDto);

    PremiseDto updatePremise(Long id, PremiseRequestDto premiseRequestDto);

    PremiseDto addWorkers2Premise(Long id, Long userId);

    MessageDto deletePremise(Long id, Long destinationPremiseId);
}
