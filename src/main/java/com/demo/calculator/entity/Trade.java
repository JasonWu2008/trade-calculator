package com.demo.calculator.entity;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class Trade {
    private static final AtomicLong ID_SEQUENCE = new AtomicLong(0);
    private Long tradeId;
    private int version;
    private String securityCode;
    private int quantity;
    private TradeOperation tradeOperation;

    public void increaseId() {
        this.tradeId = ID_SEQUENCE.decrementAndGet();
    }
}
