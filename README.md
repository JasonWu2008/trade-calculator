# trade-calculator
### Design and Solution
* Using two Hashmaps as storage to store trade data(TRADE_CACHE, TRADE_QUEUE in TradeRepositoryCacheImpl).
* One is used for ID searching and another one is used for searching and updating by SecurityCode.
* The insert/update/cancel operations are defined at ITradeRepository.
* Using a ReentrantLock to lock up data for the modifications.
### APIs (Please run: com.demo.calculator.Application)
* [Upload data](http://localhost:9090/calculator/upload?contents=1,1,REL,50,INSERT,BUY;2,1,ITC,40,INSERT,SELL;3,1,INF,70,INSERT,BUY;1,2,REL,60,UPDATE,BUY;2,2,ITC,30,CANCEL,BUY;4,1,INF,20,INSERT,SELL) 
* [List calculation results](http://localhost:9090/calculator/listCalcResults)
* [ListTrades](http://localhost:9090/calculator/listTrades)
* [ClearCache](http://localhost:9090/calculator/clearCache)
### Environments
* Dev env: JDK1.8/Maven 3.6.2/Spring boot 2.2.4
* Unit test framework: Junit
### Unit Test Coverage 
* The coverage of core classes is 70%.
1. package     Class(%)    Method(%)   Line(%) 
1. controller	0% (0/1)	0% (0/9)	0% (0/44)
1. entity	    50% (2/4)	37% (9/24)	28% (10/35)
1. exceptions	66% (2/3)	100% (2/2)	80% (4/5)
1. model	    50% (1/2)	19% (4/21)	36% (4/11)
1. repository	100% (1/1)	77% (7/9)	70% (28/40)
1. service	    100% (1/1)	69% (9/13)	71% (47/66)