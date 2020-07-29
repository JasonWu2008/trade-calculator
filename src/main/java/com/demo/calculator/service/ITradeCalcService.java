package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;

public interface ITradeCalcService {
    Trade insert(Trade trade);

    Trade update(Trade trade);

    Trade cancel(Trade trade);

    Trade findLastTrade(String securityCode);

    Trade load(long tradeId);
}
