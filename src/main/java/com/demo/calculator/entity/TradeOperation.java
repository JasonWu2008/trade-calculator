package com.demo.calculator.entity;

import org.apache.commons.lang3.StringUtils;

public enum TradeOperation {
    SELL,
    BUY;

    public static TradeOperation of(String tradeOperation) {
        for (TradeOperation operation : TradeOperation.values()) {
            if (StringUtils.equalsIgnoreCase(operation.name(), tradeOperation)) {
                return operation;
            }
        }
        return null;
    }
}
