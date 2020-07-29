package com.demo.calculator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class ListTradeTransactionController {

    @ResponseBody
    @PostMapping(path = "/list")
    public void list() {

    }
}
