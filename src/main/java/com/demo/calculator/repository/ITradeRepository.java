package com.demo.calculator.repository;

import com.demo.calculator.entity.Trade;

public interface ITradeRepository {
    Trade findLastTrade(String securityCode);

    Trade load(long tradeId);

    Trade save(Trade trade);

    Trade override(Trade trade);

    void clearAll();

    void update(Trade existed);
}
