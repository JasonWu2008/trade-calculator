package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.entity.TradeOperation;
import com.demo.calculator.exceptions.DuplicatedTradeException;
import com.demo.calculator.repository.ITradeRepository;
import com.demo.calculator.repository.impl.TradeRepositoryCacheImpl;
import org.junit.Assert;
import org.junit.Test;

public class TradeCalcServiceImplTest {
    @Test
    public void insertTest() throws Exception {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "S01", 100, TradeOperation.SELL));
        tradeCalcService.insert(createTrade(2, "S01", 100, TradeOperation.SELL));
        Trade existed = tradeCalcService.load(1);
        Assert.assertEquals(existed.getSecurityCode(), "S01");
        Assert.assertEquals(existed.getQuantity(), 100);
        Assert.assertEquals(existed.getTradeOperation(), TradeOperation.SELL);
    }

    //duplicated test
    @Test(expected = DuplicatedTradeException.class)
    public void insertDuplicatedTest() throws Exception {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();

        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "S01", 100, TradeOperation.SELL));
        tradeCalcService.insert(createTrade(1, "S02", 100, TradeOperation.SELL));
    }

    @Test
    public void updateTest() throws Exception {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "S01", 100, TradeOperation.SELL));
        Trade existed = tradeCalcService.load(1);
        Assert.assertEquals(existed.getSecurityCode(), "S01");
        Assert.assertEquals(existed.getQuantity(), 100);
        Assert.assertEquals(existed.getTradeOperation(), TradeOperation.SELL);
    }

    private Trade createTrade(long id, String sc, int quantity, TradeOperation tradeOperation) {
        Trade trade = new Trade();
        trade.setTradeId(id);
        trade.setSecurityCode(sc);
        trade.setTradeOperation(tradeOperation);
        trade.setVersion(trade.getVersion() + 1);
        trade.setQuantity(quantity);
        return trade;
    }
}
