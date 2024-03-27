package com.rahmatullo.comfortmarket.service.utils;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PremiseUtils {

    private final PremiseRepository premiseRepository;
    private final AuthUtils authUtils;

    public Optional<Premise> getPremiseOptional(Long id){
        return premiseRepository
                .findByOwnerAndId(authUtils.getOwner(), id);
    }

    public Premise getPremise(Long id) {
        return getPremiseOptional(id)
                .orElseThrow(()->new NotFoundException("Previous Premise is not found"));
    }
}
