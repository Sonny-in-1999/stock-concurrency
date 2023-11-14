package com.neurotoxin.stockexample.facade;

import com.neurotoxin.stockexample.repository.LockRepository;
import com.neurotoxin.stockexample.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;


    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decreaseForNamedLock(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
