package com.neurotoxin.stockexample.facade;

import com.neurotoxin.stockexample.repository.RedisLockRepository;
import com.neurotoxin.stockexample.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Redis Lettuce Lock
 * MySQL의 Named Lock과 거의 동일한 방식
 * Redis를 사용하기에 세션관리에 신경을 쓰지 않아도 되는 편리함 존재
 */
@RequiredArgsConstructor
@Component
public class RedisLockStockFacade {

    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;


    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(id)) {
            Thread.sleep(100);  // 100ms 간격으로 lock 획득 재시도 -> Redis 부하 감소
        }
        try {
            stockService.decreaseForNamedLock(id, quantity);
        } finally {
            redisLockRepository.unlock(id);
        }
    }
}
