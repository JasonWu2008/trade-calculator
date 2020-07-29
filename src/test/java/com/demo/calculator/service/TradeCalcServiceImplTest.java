package com.demo.calculator.service;

import com.demo.calculator.entity.Trade;
import com.demo.calculator.entity.TradeOperation;
import com.demo.calculator.exceptions.DuplicatedTradeException;
import com.demo.calculator.exceptions.TradeNotFoundException;
import com.demo.calculator.model.CalculationResult;
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
        CalculationResult result = tradeCalcService.calculate("S01");
        Assert.assertEquals(result.getSecurityCode(), "S01");
        Assert.assertEquals(result.getQuantity(), -250);
        Assert.assertEquals(result.getTradeOperation(), TradeOperation.SELL);
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
    public void calcTest() {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "RTL", 50, TradeOperation.BUY));
        tradeCalcService.insert(createTrade(2, "ITC", 40, TradeOperation.SELL));
        tradeCalcService.insert(createTrade(3, "INF", 70, TradeOperation.BUY));
        tradeCalcService.update(createTrade(1, "REL", 60, TradeOperation.BUY));
        tradeCalcService.cancel(createTrade(2, "ITC", 30, TradeOperation.BUY));
        tradeCalcService.insert(createTrade(4, "INF", 20, TradeOperation.SELL));

        CalculationResult result_1 = tradeCalcService.calculate("REL");
        Assert.assertEquals(result_1.getSecurityCode(), "REL");
        Assert.assertEquals(result_1.getQuantity(), 60);
        Assert.assertEquals(result_1.getTradeOperation(), TradeOperation.BUY);

        CalculationResult result_2 = tradeCalcService.calculate("ITC");
        Assert.assertEquals(result_2.getSecurityCode(), "ITC");
        Assert.assertEquals(result_2.getQuantity(), 0);
        Assert.assertEquals(result_2.getTradeOperation(), TradeOperation.BUY);

        CalculationResult result_3 = tradeCalcService.calculate("INF");
        Assert.assertEquals(result_3.getSecurityCode(), "INF");
        Assert.assertEquals(result_3.getQuantity(), 50);
        Assert.assertEquals(result_3.getTradeOperation(), TradeOperation.SELL);
    }

    //insert -> update -> cancel -> update
    @Test
    public void complexCalcTest() {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "RELA", 20, TradeOperation.BUY));
        tradeCalcService.update(createTrade(1, "REL", 60, TradeOperation.BUY));
        tradeCalcService.cancel(createTrade(1, "REL", 20, TradeOperation.SELL));
        tradeCalcService.update(createTrade(1, "REL", 99, TradeOperation.BUY));
        CalculationResult result_1 = tradeCalcService.calculate("REL");
        Assert.assertEquals(result_1.getSecurityCode(), "REL");
        Assert.assertEquals(result_1.getQuantity(), 99);
        Assert.assertEquals(result_1.getTradeOperation(), TradeOperation.BUY);
    }

    //insert ->cancel -> insert
    //but different tradeId
    @Test
    public void complexCalcTest2() {
        ITradeRepository tradeRepository = new TradeRepositoryCacheImpl();
        TradeCalcServiceImpl tradeCalcService = new TradeCalcServiceImpl(tradeRepository);
        tradeCalcService.clearAll();
        tradeCalcService.insert(createTrade(1, "REL", 20, TradeOperation.BUY));
        tradeCalcService.cancel(createTrade(1, "REL", 20, TradeOperation.SELL));
        tradeCalcService.insert(createTrade(2, "REL", 99, TradeOperation.SELL));
        tradeCalcService.insert(createTrade(3, "REL", 50, TradeOperation.BUY));
        CalculationResult result_1 = tradeCalcService.calculate("REL");
        Assert.assertEquals(result_1.getSecurityCode(), "REL");
        Assert.assertEquals(result_1.getQuantity(), -49);
        Assert.assertEquals(result_1.getTradeOperation(), TradeOperation.BUY);
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
