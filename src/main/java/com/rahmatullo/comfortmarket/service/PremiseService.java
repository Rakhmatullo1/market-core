package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface PremiseService {
    PremiseDto createPremise(PremiseRequestDto premiseRequestDto);

    List<PremiseDto> findAll(PageRequest pageRequest);

    PremiseDto addWorkers2Premise(Long id, Long userId);

    PremiseDto findById(Long id);

    Premise toPremise(Long id,  User user);

    PremiseDto updatePremise(Long id, PremiseRequestDto premiseRequestDto);

    MessageDto deletePremise(Long id, Long destinationPremiseId);
}
