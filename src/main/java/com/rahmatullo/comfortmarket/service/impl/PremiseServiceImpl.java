package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.entity.Worker;
import com.rahmatullo.comfortmarket.repository.*;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.PremiseMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiseServiceImpl implements PremiseService {

    private final PremiseRepository premiseRepository;
    private final PremiseMapper premiseMapper;
    private final AuthService authService;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final CategoryService categoryService;

    @Override
    public PremiseDto createPremise(PremiseRequestDto premiseRequestDto) {
        log.info("Requested to create new premise");
        Premise premise = premiseMapper.toPremise(premiseRequestDto);

        User ownerUser = authService.getUser();
        if(!Objects.equals(ownerUser.getRole(), UserRole.OWNER)){
            log.warn("user role does not match {}", ownerUser.getRole().name());
            throw new DoesNotMatchException("Your role does not match");
        }
        premise.setOwner(ownerUser);
        premise = premiseRepository.save(premise);

        List<Premise> premises = ownerUser.getPremise();
        premises.add(premise);
        ownerUser.setPremise(premises);
        userRepository.save(ownerUser);

        log.info("Successfully added new premise [{}]", premiseRequestDto);
        return premiseMapper.toPremiseDto(premise);
    }

    @Override
    public PremiseDto addProductsToPremise(Long id, ProductRequestDto productRequestDto) {
        log.info("Requested to add new products to premise {}", id);
        Premise premise = toPremise(id);

        Product product = productMapper.toProduct(productRequestDto,premise, authService.getUser());
        List<Product> productList = premise.getProducts();

        if(productRepository.existsByBarcode(productRequestDto.getBarcode())){
            log.warn("the product exists");
            throw new ExistsException("The product exists "+ productRequestDto.getBarcode());
        }
        product = productRepository.save(product);

        categoryService.addProduct2Category( product);

        productList.add(product);
        premise.setProducts(productList);

        return premiseMapper.toPremiseDto(premiseRepository.save(premise));
    }

    @Override
    public List<PremiseDto> findAll() {

        User user = authService.getUser();

        if(Objects.equals(user.getRole(), UserRole.OWNER)){
            return user.getPremise().stream()
                    .map(premiseMapper::toPremiseDto).toList();
        }

        Worker worker = workerRepository.getWorkerByUser(user).orElseThrow(()->{
            log.warn("user is not found");
            throw new NotFoundException("User is not found");
        });

        if(Objects.isNull(worker.getOwner())){
            log.warn("User is not enabled yet");
            throw new DoesNotMatchException("User has not been enabled yet");
        }

        return worker.getOwner().getPremise()
                .stream()
                .map(premiseMapper::toPremiseDto).toList();
    }

    @Override
    public void removeProductsFromPremise(Product product) {
        log.info("Requested to remove product  {}  from {}", product, product.getPremise().getId());
        Premise premise = toPremise(product.getPremise().getId());

        List<Product> products = premise.getProducts();
        products.remove(product);
        premise.setProducts(products);

        premiseRepository.save(premise);
        log.info("successfully deleted");
    }

    @Override
    public Premise toPremise(Long id) {
        return premiseRepository.findById(id).orElseThrow(()->{
            log.warn("premise is not found");
            throw new NotFoundException("Premise is not found");
        });
    }
}
