package com.neurotoxin.stockexample.domain;

import com.neurotoxin.stockexample.dto.StockResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    @Version
    private Long version;

    @Builder
    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new RuntimeException("재고는 0개 미만일 수 없습니다!");
        }
        this.quantity -= quantity;
    }

    public StockResponse toResponse() {
        return StockResponse.builder()
                .id(this.id)
                .productId(this.productId)
                .quantity(this.quantity)
                .build();
    }
}
