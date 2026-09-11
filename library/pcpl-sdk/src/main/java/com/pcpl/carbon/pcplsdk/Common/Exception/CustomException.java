package com.pcpl.carbon.pcplsdk.Common.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;
}
