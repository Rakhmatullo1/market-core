package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.History;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.HistoryRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.HistoryService;
import com.rahmatullo.comfortmarket.service.dto.HistoryDto;
import com.rahmatullo.comfortmarket.service.enums.Action4Product;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.HistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final HistoryMapper historyMapper;

    @Override
    public <T> T createHistory(T resolver, Action4Product action, Long id) {
        History history = new History();
        User user = authService.getUser();

        history.setAction(action);
        history.setDescription(action.getDescription());
        history.setByUser(user.getUsername());
        history.setCreatedAt(new Date(System.currentTimeMillis()));
        history.setProduct(toProduct(id));

        historyRepository.save(history);
        return resolver;
    }

    @Override
    public List<HistoryDto> getHistory(Long productId) {
        return historyRepository
                .getAllByProduct(toProduct(productId))
                .stream().map(historyMapper::toHistoryDto).toList();
    }

    private Product toProduct(Long id) {
        return productRepository
                .findByIdAndOwner(id, authService.getOwner())
                .orElseThrow(()->new NotFoundException("Product is not found"));
    }
}