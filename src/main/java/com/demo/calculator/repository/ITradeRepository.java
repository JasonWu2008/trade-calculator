package com.demo.calculator.repository;

import com.demo.calculator.entity.Trade;

import java.util.Map;

public interface ITradeRepository {
    Map<Long, Trade> findTrades(String securityCode);

    Trade load(long tradeId);

    void addToQueue(Trade trade);

    void removeFromQueue(Trade trade);

    Trade save(Trade trade);

    Trade override(Trade trade);

    void clearAll();
}
