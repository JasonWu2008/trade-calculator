package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.exceptions.DuplicatedTradeException;
import com.demo.calculator.exceptions.TradeInputInvalidException;
import com.demo.calculator.exceptions.TradeNotFoundException;
import com.demo.calculator.repository.ITradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeCalcServiceImpl implements ITradeCalcService {
    private ITradeRepository tradeRepository;

    @Autowired
    TradeCalcServiceImpl(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    //check insert duplicate
    //insert
    public Trade insert(Trade trade) {
        if (trade.getTradeId() != null && tradeRepository.load(trade.getTradeId()) != null) {
            throw new DuplicatedTradeException("Trade is duplicated : " + trade.getTradeId());
        }
        return tradeRepository.save(trade);
    }

    //check existed trade
    //override
    public Trade update(Trade trade) {
        if (trade == null || trade.getTradeId() == null) {
            throw new TradeInputInvalidException();
        }
        Trade existed = tradeRepository.load(trade.getTradeId());
        if (existed == null) {
            throw new TradeNotFoundException("Trade Id " + trade.getTradeId());
        }
        existed.setVersion(trade.getVersion() + 1);
        existed.setQuantity(trade.getQuantity());
        existed.setTradeOperation(trade.getTradeOperation());
        existed.setSecurityCode(trade.getSecurityCode());
        tradeRepository.update(existed);
        return existed;
    }

    //only cancel with specified tradeId
    public Trade cancel(Trade trade) {
        if (trade == null || trade.getTradeId() == null) {
            throw new TradeInputInvalidException();
        }
        Trade existed = tradeRepository.load(trade.getTradeId());
        if (existed == null) {
            throw new TradeNotFoundException("Trade Id " + trade.getTradeId());
        }
        existed.setVersion(trade.getVersion() + 1);
        existed.setQuantity(0);
        existed.setTradeOperation(trade.getTradeOperation());
        existed.setSecurityCode(trade.getSecurityCode());
        return existed;
    }

    public Trade findLastTrade(String securityCode) {
        return tradeRepository.findLastTrade(securityCode);
    }

    void clearAll() {
        tradeRepository.clearAll();
    }

    @Override
    public Trade load(long tradeId) {
        return tradeRepository.load(tradeId);
    }
}
