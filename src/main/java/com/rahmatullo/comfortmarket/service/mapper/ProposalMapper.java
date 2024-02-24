package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.ProductProposal;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class ProposalMapper {

    @Autowired
    private AuthService authService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;

    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "product",source = "proposalRequestDto.productId", qualifiedByName = "getProduct")
    @Mapping(target = "createdBy", expression = "java(getCreatedBy())")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(getCreatedTime())")
    public abstract ProductProposal toProposal(ProposalRequestDto proposalRequestDto);

    @Mapping(target = "productDto", ignore = true)
    public abstract ProposalDto toProposalDto(ProductProposal proposal);

    Date getCreatedTime() {
       return new Date(System.currentTimeMillis());
    }

    String getCreatedBy() {
        return authService.getUser().getUsername();
    }

    @Named("getProduct")
    Product getProduct(Long productId){
        return productService.toProduct(productId);
    }
}
