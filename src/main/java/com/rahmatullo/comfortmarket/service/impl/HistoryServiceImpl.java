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
import com.rahmatullo.comfortmarket.service.mapper.HistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final HistoryMapper historyMapper;

    @Override
    public <T> T createHistory(T resolver, Map<Object, Object> details) {
        History history = new History();
        User user = authService.getUser();

        history.setAction(((Action4Product) details.get("action")));
        history.setDescription(((String) details.get("description")));
        history.setByUser(user.getUsername());
        history.setCreatedAt(new Date(System.currentTimeMillis()));
        history.setProduct(toProduct(((Long) details.get("id"))));
        history.setId(null);

        historyRepository.save(history);
        return resolver;
    }

    @Override
    public List<HistoryDto> getHistory(Long productId) {
        return getHistory((long) productId)
                .stream().map(historyMapper::toHistoryDto).toList();
    }

    @Override
    public List<History> getHistory(long productId) {
        return historyRepository
                .getAllByProduct(toProduct(productId));
    }

    private Product toProduct(Long id) {
        return productRepository
                .findByIdAndOwner(id, authService.getOwner())
                .orElse(null);
    }
}