package com.neurotoxin.stockexample.service;

import com.neurotoxin.stockexample.domain.Stock;
import com.neurotoxin.stockexample.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Pessimistic Lock 방식을 적용한 재고 서비스 로직
 * Data에 직접적으로 Lock을 걸어 정합성을 맞춤
 * Data에 접근할 때마다 Lock을 할당하므로 해당방식 적용 시 성능 저하가 발생하는 것을 감안해야함
 */
@RequiredArgsConstructor
@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;


    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        stock.decrease(quantity);
    }
}
