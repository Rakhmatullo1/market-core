package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.History;
import com.rahmatullo.comfortmarket.service.dto.HistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface HistoryService {
    <T> T createHistory(T resolver, Map<Object, Object> details);

    List<HistoryDto> getHistory(Long productId, Pageable pageable);

    List<History> getHistory(long productId, Pageable pageable);
}
