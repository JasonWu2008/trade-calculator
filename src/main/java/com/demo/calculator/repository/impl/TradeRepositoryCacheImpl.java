package com.demo.calculator.repository.impl;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.exceptions.TradeInputInvalidException;
import com.demo.calculator.repository.ITradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class TradeRepositoryCacheImpl implements ITradeRepository {
    private static final Logger logger = LoggerFactory.getLogger(TradeRepositoryCacheImpl.class);

    private static final Lock TRADE_LOCKER = new ReentrantLock();
    private static final Map<Long, Trade> TRADE_CACHE = new HashMap<>();
    private static final Map<String, Map<Long, Trade>> TRADE_QUEUE = new HashMap<>();

    @Override
    public Trade load(long tradeId) {
        return TRADE_CACHE.get(tradeId);
    }

    @Override
    public void clearAll() {
        TRADE_CACHE.clear();
        TRADE_QUEUE.clear();
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
        try {
            TRADE_LOCKER.lock();
            addToQueue(trade);
            return TRADE_CACHE.put(trade.getTradeId(), trade);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            TRADE_LOCKER.unlock();
        }
        return null;
    }

    @Override
    public void removeFromQueue(Trade trade) {
        try {
            TRADE_LOCKER.lock();
            Map<Long, Trade> tradeMap = TRADE_QUEUE.get(trade.getSecurityCode());
            if (tradeMap == null || tradeMap.isEmpty()) {
                return;
            }
            tradeMap.remove(trade.getTradeId());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            TRADE_LOCKER.unlock();
        }
    }

    @Override
    public void addToQueue(Trade trade) {
        try {
            TRADE_LOCKER.lock();
            Map<Long, Trade> tradeMap = TRADE_QUEUE.computeIfAbsent(trade.getSecurityCode(), k -> new LinkedHashMap<>());
            tradeMap.put(trade.getTradeId(), trade);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            TRADE_LOCKER.unlock();
        }
    }
}
