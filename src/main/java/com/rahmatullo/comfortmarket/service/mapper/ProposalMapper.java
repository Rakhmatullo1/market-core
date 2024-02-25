package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.ProductProposal;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import com.rahmatullo.comfortmarket.service.enums.ProposalStatus;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class ProposalMapper {

    @Autowired
    private AuthService authService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductRepository productRepository;

    @Mapping(target = "toUser", expression = "java(getUser())")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "product",source = "proposalRequestDto.productId", qualifiedByName = "getProduct")
    @Mapping(target = "createdBy", expression = "java(getCreatedBy())")
    @Mapping(target = "status", constant = "PENDING", resultType = ProposalStatus.class)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(getCreatedTime())")
    public abstract ProductProposal toProposal(ProposalRequestDto proposalRequestDto);

    @Mapping(target = "toUser", source = "proposal.toUser")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "product", source = "proposalRequestDto.productId", qualifiedByName = "getProduct")
    @Mapping(target = "createdBy", source = "proposal.createdBy")
    @Mapping(target = "status", source = "proposal.status")
    @Mapping(target = "id", source = "proposal.id")
    @Mapping(target = "createdAt", source = "proposal.createdAt")
    @Mapping(target = "name", source = "proposalRequestDto.name")
    @Mapping(target = "description", source = "proposalRequestDto.description")
    @Mapping(target = "action", source = "proposalRequestDto.action")
    @Mapping(target = "count", source = "proposalRequestDto.count")
    @Mapping(target = "sellAmount", source = "proposalRequestDto.sellAmount")
    public abstract ProductProposal toProposal(ProposalRequestDto proposalRequestDto, ProductProposal proposal);

    @Mapping(target = "product", source = "proposal.product",  qualifiedByName = "getProduct")
    @Mapping(target = "toUser", source = "proposal.toUser", qualifiedByName = "getUserName")
    @Mapping(target = "createdAt", expression = "java(proposal.getCreatedAt().toString())")
    public abstract ProposalDto toProposalDto(ProductProposal proposal);

    @Named("getProduct")
    ProductDto getProduct(Product product) {
        return productMapper.toProductDto(product);
    }

    @Named("getUserName")
    String getUsername(User user) {
        return user.getUsername();
    }

    Date getCreatedTime() {
       return new Date(System.currentTimeMillis());
    }

    String getCreatedBy() {
        return authService.getUser().getUsername();
    }

    @Named("getProduct")
    Product getProduct(Long productId){
        User owner = getUser();
        return productRepository.findByIdAndOwner(productId, owner).orElseThrow(()->new NotFoundException("Product is not found"));
    }

    User getUser() {
        User user = authService.getUser();

        if(!Objects.equals(user.getRole(), UserRole.OWNER)){
            user = user.getOwner();
        }

        return user;
    }
}
