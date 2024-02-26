package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.UserService;
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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PremiseMapper premiseMapper;
    private final AuthService authService;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final UserService userService;

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

        if(productRepository.existsByBarcode(productRequestDto.getBarcode())){
            log.warn("the product exists");
            throw new ExistsException("The product exists "+ productRequestDto.getBarcode());
        }

        User owner = checkAndGetOwner(premise);

        product.setOwner(owner);
        product = productRepository.save(product);

        productService.addProduct2Category( product);

        List<Product> productList = premise.getProducts();
        productList.add(product);
        premise.setProducts(productList);

        return premiseMapper.toPremiseDto(premiseRepository.save(premise));
    }

    @Override
    public List<PremiseDto> findAll() {
        log.info("Requested to get all premises");
        User user = authService.getUser();
        return user.getPremise()
                .stream()
                .map(premiseMapper::toPremiseDto).toList();
    }

    @Override
    public PremiseDto addWorkers2Premise(Long id, Long userId) {
        log.info("Requested to add worker {} to premise {}", userId, id);
        Premise premise = toPremise(id);

        User worker = userService.toUser(userId);

        userService.checkUser(worker);
        userService.addUsers2Premise(worker, premise);

        userRepository.save(worker);
        log.info("Successfully saved user to premise ");
        return premiseMapper.toPremiseDto(premise);
    }

    @Override
    public Premise toPremise(Long id) {
        return premiseRepository.findById(id).orElseThrow(()->{
            log.warn("premise is not found");
            throw new NotFoundException("Premise is not found");
        });
    }

    @Override
    public PremiseDto updatePremise(Long id, PremiseRequestDto premiseRequestDto) {
        log.info("Requested to update premise");
        Premise premise = toPremise(id);
        premise = premiseMapper.toPremise(premiseRequestDto, premise);
        log.info("Successfully updated");
        return premiseMapper.toPremiseDto(premiseRepository.save(premise));
    }

    private User checkAndGetOwner(Premise premise) {
        User owner = authService.getUser();

        if(!Objects.equals(owner.getRole(), UserRole.OWNER)) {
            owner = owner.getOwner();
        }

        if(!Objects.equals(premise.getOwner(), owner)) {
            log.warn("This premise does not match with owner's premise");
            throw new DoesNotMatchException("Premise does not match with owner's premise");
        }
        return owner;
    }
}
