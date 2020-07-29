package com.demo.calculator.exceptions;

public class DuplicatedTradeException extends RuntimeException {
    public DuplicatedTradeException(String message) {
        super(message);
    }
}
