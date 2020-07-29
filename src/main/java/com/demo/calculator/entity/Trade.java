package com.demo.calculator.entity;

import com.demo.calculator.model.InputTrade;
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

    public static Trade copyFromInputTrade(InputTrade inputTrade) {
        Trade trade = new Trade();
        trade.setTradeId(inputTrade.getTradeId());
        trade.setVersion(inputTrade.getVersion());
        trade.setSecurityCode(inputTrade.getSecurityCode());
        trade.setQuantity(inputTrade.getQuantity());
        trade.setTradeOperation(inputTrade.getTradeOperation());
        return trade;
    }
}
