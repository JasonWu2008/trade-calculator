package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;

public interface ITradeCalcService {
    Trade insert(Trade trade);

    Trade update(Trade trade);

    Trade reset(Trade trade);

    Trade load(long tradeId);
}
