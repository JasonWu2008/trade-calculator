package com.demo.calculator.repository;

import com.demo.calculator.entity.Trade;

public interface ITradeRepository {
    Trade load(long tradeId);

    Trade save(Trade trade);

    Trade override(Trade trade);

    void clearAll();
}
