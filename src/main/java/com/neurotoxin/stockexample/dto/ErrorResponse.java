package com.neurotoxin.stockexample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
public class ErrorResponse {

    private Boolean ok;
    private HttpStatus httpStatus;
    private String message;
}
