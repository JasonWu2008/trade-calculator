package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.entity.TradeOperation;
import com.demo.calculator.exceptions.DuplicatedTradeException;
import com.demo.calculator.exceptions.TradeInputInvalidException;
import com.demo.calculator.exceptions.TradeNotFoundException;
import com.demo.calculator.model.CalculationResult;
import com.demo.calculator.repository.ITradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TradeCalcServiceImpl implements ITradeCalcService {
    private ITradeRepository tradeRepository;

    @Autowired
    TradeCalcServiceImpl(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public void clearCache() {
        tradeRepository.clearAll();
    }

    @Override
    public List<Trade> findAll() {
        return tradeRepository.findAll();
    }

    @Override
    public List<Trade> findTrades(String securityCode) {
        return new ArrayList<>(tradeRepository.findTrades(securityCode).values());
    }

    @Override
    public CalculationResult calculate(String securityCode) {
        Map<Long, Trade> tradeMap = tradeRepository.findTrades(securityCode);
        CalculationResult calculationResult = new CalculationResult();
        calculationResult.setSecurityCode(securityCode);
        for (Trade trade : tradeMap.values()) {
            processBuy(calculationResult, trade);
            processSell(calculationResult, trade);
        }
        return calculationResult;
    }

    @Override
    public List<CalculationResult> listCalcResults() {
        List<CalculationResult> calculationResults = new ArrayList<>();
        List<String> securityCodes = tradeRepository.listSecurityCodes();
        for (String securityCode : securityCodes) {
            Map<Long, Trade> tradeMap = tradeRepository.findTrades(securityCode);
            CalculationResult calculationResult = new CalculationResult();
            calculationResult.setSecurityCode(securityCode);
            for (Trade trade : tradeMap.values()) {
                processBuy(calculationResult, trade);
                processSell(calculationResult, trade);
            }
            calculationResults.add(calculationResult);
        }
        return calculationResults;
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
        tradeRepository.removeFromQueue(existed);
        existed.setVersion(trade.getVersion() + 1);
        existed.setQuantity(trade.getQuantity());
        existed.setTradeOperation(trade.getTradeOperation());
        existed.setSecurityCode(trade.getSecurityCode());
        tradeRepository.addToQueue(existed);
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

    void clearAll() {
        tradeRepository.clearAll();
    }

    @Override
    public Trade load(long tradeId) {
        return tradeRepository.load(tradeId);
    }

    private void processSell(CalculationResult calculationResult, Trade newTrade) {
        if (newTrade.getTradeOperation() != TradeOperation.SELL) {
            return;
        }
        calculationResult.setTradeOperation(TradeOperation.SELL);
        calculationResult.setQuantity(calculationResult.getQuantity() - newTrade.getQuantity());
    }

    private void processBuy(CalculationResult calculationResult, Trade newTrade) {
        if (newTrade.getTradeOperation() != TradeOperation.BUY) {
            return;
        }
        calculationResult.setTradeOperation(TradeOperation.BUY);
        calculationResult.setQuantity(calculationResult.getQuantity() + newTrade.getQuantity());
    }
}
