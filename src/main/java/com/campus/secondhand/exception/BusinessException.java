package com.campus.secondhand.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }
}
