package com.demo.calculator.entity;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class TradeTransaction {
    private static final AtomicLong ID_SEQUENCE = new AtomicLong(0);
    private long transactionId;
    private long tradeId;

    public void increaseId() {
        this.transactionId = ID_SEQUENCE.decrementAndGet();
    }
}
