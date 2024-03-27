package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.request.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.mapper.PremiseMapper;
import com.rahmatullo.comfortmarket.service.utils.AuthUtils;
import com.rahmatullo.comfortmarket.service.utils.PremiseUtils;
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
    private final AuthUtils authUtils;
    private final PremiseUtils premiseUtils;
    private final UserService userService;

    @Override
    public List<PremiseDto> findAll(PageRequest pageRequest) {
        log.info("Requested to get all premises");
        User user = authUtils.getOwner();
        return premiseRepository.findAllByOwner(user, pageRequest)
                .map(premiseMapper::toPremiseDto).toList();
    }

    @Override
    public PremiseDto findById(Long id) {
        return premiseMapper.toPremiseDto(premiseUtils.getPremise(id));
    }

    @Override
    public PremiseDto createPremise(PremiseRequestDto premiseRequestDto) {
        log.info("Requested to create new premise");
        User ownerUser = authUtils.getUser();
        return createPremise(premiseRequestDto, ownerUser);
    }

    @Override
    public PremiseDto createPremise(PremiseRequestDto premiseRequestDto, User owner) {
        Premise premise = premiseMapper.toPremise(premiseRequestDto);
        if(!Objects.equals(owner.getRole(), UserRole.OWNER)){
            log.warn("user role does not match {}", owner.getRole().name());
            throw new DoesNotMatchException("Your role does not match");
        }

        premise.setOwner(owner);
        premise = premiseRepository.save(premise);

        owner.getPremise().add(premise);
        userRepository.save(owner);

        log.info("Successfully added new premise [{}]", premiseRequestDto);
        return premiseMapper.toPremiseDto(premise);
    }

    @Override
    public PremiseDto addWorkers2Premise(Long id, Long userId) {
        log.info("Requested to add worker {} to premise {}", userId, id);
        Premise premise = premiseUtils.getPremise(id);

        User worker = userService.toUser(userId);

        userService.checkUser(worker);
        worker.getPremise().add(premise);

        userRepository.save(worker);
        log.info("Successfully saved user to premise ");
        return premiseMapper.toPremiseDto(premise);
    }

    @Override
    public PremiseDto updatePremise(Long id, PremiseRequestDto premiseRequestDto) {
        log.info("Requested to update premise");
        Premise premise = premiseUtils.getPremise(id);
        premise = premiseMapper.toPremise(premiseRequestDto, premise);
        log.info("Successfully updated");
        return premiseMapper.toPremiseDto(premiseRepository.save(premise));
    }

    @Override
    public MessageDto deletePremise(Long id, Long destinationPremiseId) {
        log.info("Requested to delete premise id {}", id);
        User user = authUtils.getUser();

        if(!Objects.equals(user.getRole(), UserRole.OWNER)) {
            log.warn("user role {}", user.getRole().name());
            throw new DoesNotMatchException("Your role does not match with any authorities");
        }

        if(Objects.equals(id, destinationPremiseId)) {
            log.warn("destination premise and the premise cannot be same");
            throw new DoesNotMatchException("destination premise and the premise cannot be same");
        }

        Premise premise = premiseUtils.getPremise(id);
        Premise destinationPremise = premiseUtils.getPremise(destinationPremiseId);

        for (User worker : premise.getWorkers()) {
            worker.getPremise().remove(premise);
            userRepository.save(worker);
        }

        List<Product> temp = new ArrayList<>(premise.getProducts());

        premise.setProducts(new ArrayList<>());
        premiseRepository.save(premise);

        temp.forEach(product -> {
            product.getPremise().remove(premise);
            product.getPremise().add(destinationPremise);
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
