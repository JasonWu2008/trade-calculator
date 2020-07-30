package com.demo.calculator.controller;

import com.alibaba.fastjson.JSONArray;
import com.demo.calculator.entity.Operation;
import com.demo.calculator.entity.TradeOperation;
import com.demo.calculator.model.InputTrade;
import com.demo.calculator.service.ITradeCalcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.demo.calculator.entity.Trade.copyFromInputTrade;

@Controller
@RequestMapping
public class ListTradeTransactionController {
    private ITradeCalcService iTradeCalcService;

    @Autowired
    public ListTradeTransactionController(ITradeCalcService iTradeCalcService) {
        this.iTradeCalcService = iTradeCalcService;
    }

    @ResponseBody
    @GetMapping(path = "/upload")
    public String upload(String contents) {
        if (StringUtils.isBlank(contents)) {
            return "contents is blank";
        }
        String[] tradeStrs = StringUtils.split(contents, ";\n");
        List<InputTrade> inputTrades = new ArrayList<>();
        for (String tradeStr : tradeStrs) {
            String[] fields = StringUtils.split(tradeStr, ",");
            if (fields.length != 6) {
                continue;
            }
            inputTrades.add(createTrade(fields));
        }
        for (InputTrade inputTrade : inputTrades) {
            processInsert(inputTrade);
            processUpdate(inputTrade);
            processCancel(inputTrade);
        }
        return "upload done, impacted:" + inputTrades.size();
    }

    private void processCancel(InputTrade inputTrade) {
        if (inputTrade.getOperation() != Operation.CANCEL) {
            return;
        }
        iTradeCalcService.cancel(copyFromInputTrade(inputTrade));
    }

    private void processUpdate(InputTrade inputTrade) {
        if (inputTrade.getOperation() != Operation.UPDATE) {
            return;
        }
        iTradeCalcService.update(copyFromInputTrade(inputTrade));
    }

    private void processInsert(InputTrade inputTrade) {
        if (inputTrade.getOperation() != Operation.INSERT) {
            return;
        }
        iTradeCalcService.insert(copyFromInputTrade(inputTrade));
    }

    private InputTrade createTrade(String[] fields) {
        InputTrade inputTrade = new InputTrade();
        inputTrade.setTradeId(Long.parseLong(fields[0]));
        inputTrade.setVersion(Integer.parseInt(fields[1]));
        inputTrade.setSecurityCode(fields[2]);
        inputTrade.setQuantity(Integer.parseInt(fields[3]));
        inputTrade.setOperation(Operation.of(fields[4]));
        inputTrade.setTradeOperation(TradeOperation.of(fields[5]));
        return inputTrade;
    }

    @ResponseBody
    @GetMapping(path = "/listCalcResults")
    public String listCalcResults() {
        return JSONArray.toJSONString(iTradeCalcService.listCalcResults());
    }

    @ResponseBody
    @GetMapping(path = "/listTrades")
    public String listTrades(String securityCode) {
        if (StringUtils.isBlank(securityCode)) {
            return JSONArray.toJSONString(iTradeCalcService.findAll());
        }
        return JSONArray.toJSONString(iTradeCalcService.findTrades(securityCode));
    }

    @ResponseBody
    @GetMapping(path = "/clearCache")
    public String clearCache() {
        iTradeCalcService.clearCache();
        return "Cache clear done.";
    }
}
