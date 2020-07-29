package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.entity.TradeOperation;
import com.demo.calculator.exceptions.DuplicatedTradeException;
import com.demo.calculator.exceptions.TradeNotFoundException;
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
        tradeCalcService.insert(createTrade(2, "S01", 50, TradeOperation.BUY));
        tradeCalcService.insert(createTrade(3, "S01", 200, TradeOperation.SELL));
        Trade existed = tradeCalcService.findLastTrade("S01");
        Assert.assertEquals(existed.getSecurityCode(), "S01");
        Assert.assertEquals(existed.getQuantity(), -250);
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
        tradeCalcService.update(createTrade(1, "S02", 80, TradeOperation.SELL));

        tradeCalcService.insert(createTrade(2, "S03", 200, TradeOperation.SELL));
        tradeCalcService.update(createTrade(2, "S04", 180, TradeOperation.BUY));
        Trade existed_1 = tradeCalcService.load(1);
        Assert.assertEquals(existed_1.getSecurityCode(), "S02");
        Assert.assertEquals(existed_1.getQuantity(), 80);
        Assert.assertEquals(existed_1.getTradeOperation(), TradeOperation.SELL);

        Trade existed_2 = tradeCalcService.load(2);
        Assert.assertEquals(existed_2.getSecurityCode(), "S04");
        Assert.assertEquals(existed_2.getQuantity(), 180);
        Assert.assertEquals(existed_2.getTradeOperation(), TradeOperation.BUY);
    }

    @Test(expected = TradeNotFoundException.class)
    public void updateNotFoundTrade() throws Exception {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "S01", 100, TradeOperation.SELL));
        tradeCalcService.update(createTrade(2, "S02", 80, TradeOperation.SELL));
    }

    @Test
    public void cancelTest() throws Exception {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "S01", 100, TradeOperation.SELL));
        tradeCalcService.update(createTrade(1, "S02", 80, TradeOperation.SELL));
        tradeCalcService.cancel(createTrade(1, "S03", 20, TradeOperation.BUY));

        Trade existed_1 = tradeCalcService.load(1);
        Assert.assertEquals(existed_1.getSecurityCode(), "S03");
        Assert.assertEquals(existed_1.getQuantity(), 0);
        Assert.assertEquals(existed_1.getTradeOperation(), TradeOperation.BUY);
    }

    @Test(expected = TradeNotFoundException.class)
    public void cancelNotFoundTest() throws Exception {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "S01", 100, TradeOperation.SELL));
        tradeCalcService.update(createTrade(1, "S02", 80, TradeOperation.SELL));
        tradeCalcService.cancel(createTrade(2, "S03", 20, TradeOperation.BUY));
    }

    @Test
    public void testFind() {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "RTL", 50, TradeOperation.BUY));
        tradeCalcService.insert(createTrade(2, "ITC", 40, TradeOperation.SELL));
        tradeCalcService.insert(createTrade(3, "INF", 70, TradeOperation.BUY));
        tradeCalcService.update(createTrade(1, "REL", 60, TradeOperation.BUY));
        tradeCalcService.cancel(createTrade(2, "ITC", 30, TradeOperation.BUY));
        tradeCalcService.insert(createTrade(4, "INF", 20, TradeOperation.SELL));

        Trade existed_1 = tradeCalcService.findLastTrade("REL");
        Assert.assertEquals(existed_1.getSecurityCode(), "REL");
        Assert.assertEquals(existed_1.getQuantity(), 60);
        Assert.assertEquals(existed_1.getTradeOperation(), TradeOperation.BUY);

        Trade existed_2 = tradeCalcService.findLastTrade("ITC");
        Assert.assertEquals(existed_2.getSecurityCode(), "ITC");
        Assert.assertEquals(existed_2.getQuantity(), 0);
        Assert.assertEquals(existed_2.getTradeOperation(), TradeOperation.BUY);

        Trade existed_3 = tradeCalcService.findLastTrade("INF");
        Assert.assertEquals(existed_3.getSecurityCode(), "INF");
        Assert.assertEquals(existed_3.getQuantity(), 50);
        Assert.assertEquals(existed_3.getTradeOperation(), TradeOperation.SELL);
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
