package com.neurotoxin.stockexample.dto;

import com.neurotoxin.stockexample.domain.Stock;
import lombok.Data;

@Data
public class StockRequest {

    private Long productId;

    private Long quantity;


    public Stock toEntity() {
        return Stock.builder()
                .productId(this.productId)
                .quantity(this.quantity)
                .build();
    }
}
