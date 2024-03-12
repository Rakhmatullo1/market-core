package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.HistoryDto;
import com.rahmatullo.comfortmarket.service.enums.Action4Product;

import java.util.List;

public interface HistoryService {
    <T> T createHistory(T resolver, Action4Product action, Long id);

    List<HistoryDto> getHistory(Long productId);
}
