package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.model.CalculationResult;

public interface ITradeCalcService {
    Trade insert(Trade trade);

    Trade update(Trade trade);

    Trade cancel(Trade trade);

    Trade load(long tradeId);

    CalculationResult calculate(String securityCode);
}
