package com.demo.calculator.model;

import com.demo.calculator.entity.TradeOperation;
import lombok.Data;

@Data
public class CalculationResult {
    private String securityCode;
    private int quantity;
    private TradeOperation tradeOperation;
}
