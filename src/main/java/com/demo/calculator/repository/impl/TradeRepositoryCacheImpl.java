package com.demo.calculator.repository.impl;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.entity.TradeOperation;
import com.demo.calculator.exceptions.TradeInputInvalidException;
import com.demo.calculator.repository.ITradeRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TradeRepositoryCacheImpl implements ITradeRepository {
    private static final ConcurrentHashMap<Long, Trade> TRADE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Trade> LAST_QUEUE = new ConcurrentHashMap<>();

    @Override
    public Trade load(long tradeId) {
        return TRADE_CACHE.get(tradeId);
    }

    @Override
    public void clearAll() {
        TRADE_CACHE.clear();
    }

    public Trade findLastTrade(String securityCode) {
        return LAST_QUEUE.get(securityCode);
    }

    @Override
    public void update(Trade existed) {
        LAST_QUEUE.put(existed.getSecurityCode(), existed);
    }

    @Override
    public Trade save(Trade trade) {
        if (trade == null || trade.getTradeId() == null) {
            throw new TradeInputInvalidException();
        }
        Trade existed = LAST_QUEUE.get(trade.getSecurityCode());
        if (existed == null) {
            LAST_QUEUE.put(trade.getSecurityCode(), trade);
            processSell(trade);
            processBuy(trade);
        } else {
            processSell(existed, trade);
            processBuy(existed, trade);
        }
        return TRADE_CACHE.put(trade.getTradeId(), trade);
    }

    @Override
    public Trade override(Trade trade) {
        return TRADE_CACHE.put(trade.getTradeId(), trade);
    }

    private void processSell(Trade newTrade) {
        if (newTrade.getTradeOperation() != TradeOperation.SELL) {
            return;
        }
        newTrade.setVersion(1);
        newTrade.setQuantity(-newTrade.getQuantity());
    }

    private void processSell(Trade existed, Trade newTrade) {
        if (newTrade.getTradeOperation() != TradeOperation.SELL) {
            return;
        }
        existed.setTradeOperation(TradeOperation.SELL);
        existed.setVersion(existed.getVersion() + 1);
        existed.setQuantity(existed.getQuantity() - newTrade.getQuantity());
    }

    private void processBuy(Trade newTrade) {
        if (newTrade.getTradeOperation() != TradeOperation.BUY) {
            return;
        }
        newTrade.setVersion(1);
    }

    private void processBuy(Trade existed, Trade newTrade) {
        if (newTrade.getTradeOperation() != TradeOperation.BUY) {
            return;
        }
        existed.setTradeOperation(TradeOperation.BUY);
        existed.setVersion(existed.getVersion() + 1);
        existed.setQuantity(existed.getQuantity() + newTrade.getQuantity());
    }
}
