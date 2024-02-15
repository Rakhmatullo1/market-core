package com.rahmatullo.comfortmarket.service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR ,componentModel = "spring")
public abstract class PremiseMapper {
}
