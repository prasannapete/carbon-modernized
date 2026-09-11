package com.pcpl.carbon.pcplsdk.Common.Handler;

import com.pcpl.carbon.pcplsdk.Common.Exception.CustomException;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity handleCustomException(CustomException exception) {
        log.error("Error {}", exception);
        return ResponseEntity.status(exception.getHttpStatus().value())
                .body(ApplicationResponse.builder()
                        .message(exception.getMessage())
                        .build());
    }
}
