package com.demo.calculator.repository.impl;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.exceptions.TradeInputInvalidException;
import com.demo.calculator.repository.ITradeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TradeRepositoryCacheImpl implements ITradeRepository {
    private static final ConcurrentHashMap<Long, Trade> TRADE_CACHE = new ConcurrentHashMap<>();

    @Override
    public Trade load(long tradeId) {
        return TRADE_CACHE.get(tradeId);
    }

    public List<Trade> findBySrCode(String securityCode) {
        return new ArrayList<>();
    }

    @Override
    public void clearAll() {
        TRADE_CACHE.clear();
    }

    @Override
    public Trade save(Trade trade) {
        if (trade == null || trade.getTradeId() == null) {
            throw new TradeInputInvalidException();
        }
        return TRADE_CACHE.put(trade.getTradeId(), trade);
    }

    @Override
    public Trade override(Trade trade) {
        return TRADE_CACHE.put(trade.getTradeId(), trade);
    }
}
