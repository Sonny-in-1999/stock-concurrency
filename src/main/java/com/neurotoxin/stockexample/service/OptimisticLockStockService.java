package com.neurotoxin.stockexample.service;

import com.neurotoxin.stockexample.domain.Stock;
import com.neurotoxin.stockexample.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optimsitic Lock 방식을 적용한 재고 서비스 로직
 * Pessimistic Lock과 다르게 별도의 Data Lock을 할당하지 않으므로 성능상 이점이 존재
 */
@RequiredArgsConstructor
@Service
public class OptimisticLockStockService {

    private final StockRepository stockRepository;


    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);
        stock.decrease(quantity);
        stockRepository.save(stock);
    }
}
