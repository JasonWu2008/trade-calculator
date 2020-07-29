package com.demo.calculator.model;

import com.demo.calculator.entity.Operation;
import com.demo.calculator.entity.TradeOperation;
import lombok.Data;

@Data
public class InputTrade {
    private Long tradeId;
    private int version;
    private String securityCode;
    private int quantity;
    private Operation operation;
    private TradeOperation tradeOperation;
}
