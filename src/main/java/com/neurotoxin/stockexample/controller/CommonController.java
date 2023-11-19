package com.neurotoxin.stockexample.controller;

import com.neurotoxin.stockexample.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommonController {

    @GetMapping("/health")
    public ResponseEntity getHealth() {
        return ResponseEntity.ok(new ErrorResponse(true, HttpStatus.OK, "헬스 체크 정상"));
    }
}
