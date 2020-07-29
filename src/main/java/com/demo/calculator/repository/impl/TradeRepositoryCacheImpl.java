package com.demo.calculator.repository.impl;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.exceptions.TradeInputInvalidException;
import com.demo.calculator.repository.ITradeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TradeRepositoryCacheImpl implements ITradeRepository {
    private static final ConcurrentHashMap<Long, Trade> TRADE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Map<Long, Trade>> TRADE_QUEUE = new ConcurrentHashMap<>();

    @Override
    public Trade load(long tradeId) {
        return TRADE_CACHE.get(tradeId);
    }

    @Override
    public void clearAll() {
        TRADE_CACHE.clear();
    }

    @Override
    public List<Trade> findAll() {
        return new ArrayList<>(TRADE_CACHE.values());
    }

    public List<String> listSecurityCodes() {
        return new ArrayList<>(TRADE_QUEUE.keySet());
    }

    public Map<Long, Trade> findTrades(String securityCode) {
        return TRADE_QUEUE.get(securityCode);
    }

    @Override
    public Trade save(Trade trade) {
        if (trade == null || trade.getTradeId() == null) {
            throw new TradeInputInvalidException();
        }
        addToQueue(trade);
        return TRADE_CACHE.put(trade.getTradeId(), trade);
    }

    @Override
    public void removeFromQueue(Trade trade) {
        Map<Long, Trade> tradeMap = TRADE_QUEUE.get(trade.getSecurityCode());
        if (tradeMap == null || tradeMap.isEmpty()) {
            return;
        }
        tradeMap.remove(trade.getTradeId());
    }

    @Override
    public void addToQueue(Trade trade) {
        Map<Long, Trade> tradeMap = TRADE_QUEUE.computeIfAbsent(trade.getSecurityCode(), k -> new LinkedHashMap<>());
        tradeMap.put(trade.getTradeId(), trade);
    }

    @Override
    public Trade override(Trade trade) {
        return TRADE_CACHE.put(trade.getTradeId(), trade);
    }
}
