package com.neurotoxin.stockexample.service;

import com.neurotoxin.stockexample.domain.Stock;
import com.neurotoxin.stockexample.facade.NamedLockStockFacade;
import com.neurotoxin.stockexample.facade.OptimisticLockStockFacade;
import com.neurotoxin.stockexample.facade.RedisLockStockFacade;
import com.neurotoxin.stockexample.facade.RedissonLockStockFacade;
import com.neurotoxin.stockexample.repository.StockRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private PessimisticLockStockService pessimisticLockStockService;

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @Autowired
    private RedisLockStockFacade redisLockStockFacade;

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void beforeEach() {
        stockRepository.saveAndFlush(new Stock(1L, 1000L));
    }

    @AfterEach
    public void afterEach() {
        stockRepository.deleteAll();
    }


    @DisplayName("java synchronized를 활용해 1000개의 동시 요청(재고감소)을 처리합니다")
    @Test
    public void concurrent_request_by_java_synchronized() throws InterruptedException {
        int threadCount = 1000;
        // 비동기 실행을 위한 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 모두 끝날떄까지 대기하기 위해 CountDownLatch 활용
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 둘 이상의 쓰레드가 공유 데이터에 엑세스하여 변경을 시도하는 경우 -> Race Condition 발생!
        // 1차 시도: 서비스 로직에 synchronized 선언을 통해 메서드에 한 번에 하나의 쓰레드만 접근이 가능하도록 제한한다.
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 1000 - (1 * 1000) = 0
        assertEquals(0, stock.getQuantity());
    }

    @DisplayName("Pessimistic Lock을 활용해 1000개의 동시 요청(재고감소)을 처리합니다")
    @Test
    public void concurrent_request_by_pessimistic_lock() throws InterruptedException {
        int threadCount = 1000;
        // 비동기 실행을 위한 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 모두 끝날떄까지 대기하기 위해 CountDownLatch 활용
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 둘 이상의 쓰레드가 공유 데이터에 엑세스하여 변경을 시도하는 경우 -> Race Condition 발생!
        // 2차 시도: Pessimistic Lock을 활용하여 Data Lock을 걸어 데이터 정합성을 맞춘다
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockStockService.decrease(1L, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1 * 100) = 0
        assertEquals(0, stock.getQuantity());
    }

    @DisplayName("Optimistic Lock을 활용해 1000개의 동시 요청(재고감소)을 처리합니다")
    @Test
    public void concurrent_request_by_optimistic_lock() throws InterruptedException {
        int threadCount = 1000;
        // 비동기 실행을 위한 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 모두 끝날떄까지 대기하기 위해 CountDownLatch 활용
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 둘 이상의 쓰레드가 공유 데이터에 엑세스하여 변경을 시도하는 경우 -> Race Condition 발생!
        // 3차 시도: Optimistic Lock을 활용하여 엔티티의 version을 활용해 데이터 정합성을 맞춘다
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1 * 100) = 0
        assertEquals(0, stock.getQuantity());
    }

    @DisplayName("Named Lock을 활용해 1000개의 동시 요청(재고감소)을 처리합니다")
    @Test
    public void concurrent_request_by_named_lock() throws InterruptedException {
        int threadCount = 1000;
        // 비동기 실행을 위한 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 모두 끝날떄까지 대기하기 위해 CountDownLatch 활용
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 둘 이상의 쓰레드가 공유 데이터에 엑세스하여 변경을 시도하는 경우 -> Race Condition 발생!
        // 4차 시도: Named Lock을 활용하여 이름을 가진 Lock을 별도로 할당하여 데이터 정합성을 맞춘다
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1 * 100) = 0
        assertEquals(0, stock.getQuantity());
    }

    @DisplayName("Redis의 Lettuce Lock 방식을 활용해 1000개의 동시 요청(재고감소)을 처리합니다")
    @Test
    public void concurrent_request_by_redis_lettuce_lock() throws InterruptedException {
        int threadCount = 1000;
        // 비동기 실행을 위한 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 모두 끝날떄까지 대기하기 위해 CountDownLatch 활용
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 둘 이상의 쓰레드가 공유 데이터에 엑세스하여 변경을 시도하는 경우 -> Race Condition 발생!
        // 4차 시도: Named Lock을 활용하여 이름을 가진 Lock을 별도로 할당하여 데이터 정합성을 맞춘다
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redisLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1 * 100) = 0
        assertEquals(0, stock.getQuantity());
    }

    @DisplayName("Redisson 라이브러리를 활용해 1000개의 동시 요청(재고감소)을 처리합니다")
    @Test
    public void concurrent_request_by_redisson() throws InterruptedException {
        int threadCount = 1000;
        // 비동기 실행을 위한 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 모두 끝날떄까지 대기하기 위해 CountDownLatch 활용
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 둘 이상의 쓰레드가 공유 데이터에 엑세스하여 변경을 시도하는 경우 -> Race Condition 발생!
        // 4차 시도: Named Lock을 활용하여 이름을 가진 Lock을 별도로 할당하여 데이터 정합성을 맞춘다
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockStockFacade.decrease(1L, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1 * 100) = 0
        assertEquals(0, stock.getQuantity());
    }
}

