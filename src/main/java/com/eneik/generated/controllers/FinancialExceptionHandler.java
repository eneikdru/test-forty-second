package com.eneik.generated.controllers;

import com.eneik.generated.dtos.ApiErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;

@ControllerAdvice
public class FinancialExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAllExceptions(Exception ex) {
        ApiErrorDto error = new ApiErrorDto(
                "INTERNAL_SERVER_ERROR",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.",
                LocalDateTime.now(),
                Collections.singletonList(ex.toString())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
