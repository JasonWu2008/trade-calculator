package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.model.CalculationResult;

import java.util.List;

public interface ITradeCalcService {
    Trade insert(Trade trade);

    Trade update(Trade trade);

    Trade cancel(Trade trade);

    Trade load(long tradeId);

    void clearCache();

    CalculationResult calculate(String securityCode);

    List<CalculationResult> listCalcResults();

    List<Trade> findTrades(String securityCode);

    List<Trade> findAll();
}
