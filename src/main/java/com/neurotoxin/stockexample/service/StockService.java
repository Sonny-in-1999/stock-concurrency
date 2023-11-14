package com.neurotoxin.stockexample.service;

import com.neurotoxin.stockexample.domain.Stock;
import com.neurotoxin.stockexample.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class StockService {

    private final StockRepository stockRepository;

    /**
     * !! @Transactinal 어노테이션의 구조적 특성으로 인해 동시성 문제가 발생 !!
     * 1. @Transactional 어노테이션을 사용할 경우 우리가 만든 클래스를 매핑한 클래스를 새로 만들어 실행한다.(transaction directory 참고)
     * 2. decrease 메서드의 작업이 모두 끝났음에도 transaction이 커밋되기 직전에 다른 쓰레드의 호출로 실행되어 예상치 못한 결과가 발생할 수 있게 된다.
     * 3. 따라서 java synchronized를 통해 동기 작업을 수행하는 경우에는 @Transactional 어노테이션을 사용하지 않아야 동시성 문제가 해결된다.
     * synchronized 사용 시 문제점:
     *  1. java synchronized는 하나의 프로세스 내부에서만 보장됨.(서버 scale-out이 불가능 -> 사실상 사용하지 않는 방식)
     */
//    @Transactional
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock id가 올바르지 않습니다!"));
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)  // 부모의 트랜잭션과 별개로 실행되어야 하기에 propagation 설정 변경
    public void decreaseForNamedLock(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock id가 올바르지 않습니다!"));
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
