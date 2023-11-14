package com.neurotoxin.stockexample.transaction;

import com.neurotoxin.stockexample.service.StockService;
import lombok.RequiredArgsConstructor;

// @Transactional 어노테이션을 사용할 경우 이런식으로 새로운 클래스를 만들어 트랜잭션을 수행 한다.
@RequiredArgsConstructor
public class TransactionStockService {

    private final StockService stockService;

    public void decrease(Long id, Long quantity) {
        startTransaction();


        stockService.decrease(id, quantity);
        // decrease 로직이 완료된 이후에 다른 쓰레드가 decrease 메서드를 호출하면서 문제 발생 가능
        endTransaction();
    }

    private void endTransaction() {
        System.out.println("Transaction Start!");
    }

    private void startTransaction() {
        System.out.println("Transaction End and Commit!");
    }
}
