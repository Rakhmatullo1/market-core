package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Invoice;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.ProductDetails;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.dto.InvoiceDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDetailsDto;
import com.rahmatullo.comfortmarket.service.dto.request.InvoiceRequestDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProductDetailsRequestDto;
import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.InvoiceStatus;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class InvoiceMapper {

    @Autowired
    private PremiseRepository premiseRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;

    @Mapping(target = "status", expression = "java(getStatus())")
    @Mapping(target = "toUser", expression = "java(getOwner())")
    @Mapping(target = "createdBy", expression = "java(getCreator())")
    @Mapping(target = "productDetailsSet",ignore = true)
    @Mapping(target = "premise", source = "requestDto", qualifiedByName = "getPremise")
    @Mapping(target = "overallInitialPrice", source = "products", qualifiedByName = "getOverallInitialPrice")
    @Mapping(target = "overallFinalPrice", source = "products", qualifiedByName = "getOverallFinalPrice")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date",expression = "java(getDate())")
    @Mapping(target = "previousId",expression = "java(getPreviousPremiseId(requestDto))")
    public abstract  Invoice toInvoice(InvoiceRequestDto requestDto);

    @Mapping(target = "previousPremiseName", expression = "java(getPremiseName(invoice.getPreviousId()))")
    @Mapping(target = "products", source = "productDetailsSet", qualifiedByName = "getProducts")
    @Mapping(target = "toUser", expression = "java(invoice.getToUser().getUsername())")
    @Mapping(target = "premise", expression = "java(getPremiseName(invoice.getPremise()))")
    @Mapping(target = "date", expression = "java(invoice.getDate().toString())")
    public abstract InvoiceDto toInvoiceDto(Invoice invoice);

    Date getDate(){
        return new Date(System.currentTimeMillis());
    }

    @Named("getOverallInitialPrice")
    Double getOverallInitialPrice(List<ProductDetailsRequestDto> products) {
        Double result=0d;
        for (ProductDetailsRequestDto p : products) {
            result += p.getInitialPrice();
        }
        return result;
    }

    @Named("getOverallFinalPrice")
    Double getOverallFinalPrice(List<ProductDetailsRequestDto> products) {
        Double result=0d;
        for (ProductDetailsRequestDto p : products) {
            result += p.getFinalPrice();
        }
        return result;
    }

    @Named("getPremise")
    Premise getPremise(InvoiceRequestDto invoiceRequestDto) {
        if(Objects.equals(invoiceRequestDto.getAction(), Action.EXPORT)) {
            return null;
        }
        return getPremise(invoiceRequestDto.getPremiseId()).orElseThrow(()->new NotFoundException("Premise is not found"));
    }

    User getOwner() {
        return authService.getOwner();
    }

    String  getCreator() {
        return authService.getUser().getUsername();
    }

    @Named("getProducts")
    List<ProductDetailsDto> getProducts(Set<ProductDetails> productDetails) {
        return productDetails.stream().map(productDetailsMapper::toProductDetailsDto).toList();
    }

    InvoiceStatus getStatus() {
        return InvoiceStatus.PENDING;
    }

    String getPremiseName(Premise premise) {
        if(Objects.isNull(premise)) {
            return null;
        }
        return premise.getName();
    }

    Long getPreviousPremiseId(InvoiceRequestDto invoiceRequestDto) {
        if(Objects.equals(invoiceRequestDto.getAction(), Action.IMPORT)) {
            return null;
        }
        return getPremise(invoiceRequestDto.getPreviousId())
                .orElseThrow(()->new NotFoundException("Premise is not found"))
                .getId();
    }

    String getPremiseName(Long id) {
        Optional<Premise> premise = getPremise(id);
        if(premise.isEmpty()) {
            return null;
        }
        return  premise.get().getName();
    }

    Optional<Premise> getPremise(Long id){
        return premiseRepository.findByOwnerAndId(authService.getOwner(), id);
    }
}
