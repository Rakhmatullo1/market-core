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
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.PremiseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final UserService userService;
    private final ProductService productService;

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
    public List<PremiseDto> findAll(PageRequest pageRequest) {
        log.info("Requested to get all premises");
        User user = authService.getOwner();
        return premiseRepository.findAllByOwner(user, pageRequest)
                .map(premiseMapper::toPremiseDto).toList();
    }

    @Override
    public PremiseDto addWorkers2Premise(Long id, Long userId) {
        log.info("Requested to add worker {} to premise {}", userId, id);
        Premise premise = toPremise(id, authService.getOwner());

        User worker = userService.toUser(userId);

        userService.checkUser(worker);
        userService.addUsers2Premise(worker, premise);

        userRepository.save(worker);
        log.info("Successfully saved user to premise ");
        return premiseMapper.toPremiseDto(premise);
    }

    @Override
    public PremiseDto findById(Long id) {
        return premiseMapper.toPremiseDto(toPremise(id, authService.getOwner()));
    }

    @Override
    public Premise toPremise(Long id, User user) {
        return premiseRepository.findByOwnerAndId(user, id).orElseThrow(()->{
            log.warn("premise is not found");
            throw new NotFoundException("Premise is not found " + id);
        });
    }

    @Override
    public PremiseDto updatePremise(Long id, PremiseRequestDto premiseRequestDto) {
        log.info("Requested to update premise");
        Premise premise = toPremise(id, authService.getOwner());
        premise = premiseMapper.toPremise(premiseRequestDto, premise);
        log.info("Successfully updated");
        return premiseMapper.toPremiseDto(premiseRepository.save(premise));
    }

    @Override
    public MessageDto deletePremise(Long id, Long destinationPremiseId) {
        log.info("Requested to delete premise id {}", id);
        User owner = authService.getOwner();

        if(Objects.equals(id, destinationPremiseId)) {
            log.warn("destination premise and the premise cannot be same");
            throw new DoesNotMatchException("destination premise and the premise cannot be same");
        }

        Premise premise = toPremise(id, owner);
        Premise destinationPremise = toPremise(destinationPremiseId, authService.getOwner());

        for (User worker : premise.getWorkers()) {
            List<Premise> premises = worker.getPremise();
            premises.remove(premise);
            worker.setPremise(premises);
            userRepository.save(worker);
        }

        List<Product> temp = new ArrayList<>(premise.getProducts());

        premise.setProducts(new ArrayList<>());
        premiseRepository.save(premise);

        temp.forEach(product -> {
            product.setPremise(destinationPremise);
            productRepository.save(product);
        });

        List<Product> products = destinationPremise.getProducts();
        products.addAll(temp);
        destinationPremise.setProducts(products);

        premiseRepository.save(destinationPremise);
        premiseRepository.delete(premise);
        return new MessageDto("Successfully deleted and transferred to another premise");
    }
}
