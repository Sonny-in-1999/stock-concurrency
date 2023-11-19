package com.neurotoxin.stockexample.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StockResponse {

    private Long id;

    private Long productId;

    private Long quantity;
}
