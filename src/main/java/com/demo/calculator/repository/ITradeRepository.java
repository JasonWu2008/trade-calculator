package com.demo.calculator.repository;

import com.demo.calculator.entity.Trade;

import java.util.List;
import java.util.Map;

public interface ITradeRepository {
    List<Trade> findAll();

    List<String> listSecurityCodes();

    Map<Long, Trade> findTrades(String securityCode);

    Trade load(long tradeId);

    void addToQueue(Trade trade);

    void removeFromQueue(Trade trade);

    Trade save(Trade trade);

    void clearAll();
}
