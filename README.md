# Matching Engine
This project implements a simple matchig engine with price/time priority of orders. It currently supports only limit and market orders but can easily be extended to support more order types. 

The engine supports order books for multiple instruments and allows for the following actions with the following asymptotic runtimes
<br>
    <table align="center">
        <tr>
            <td>Operation</td>
            <td align="center">Time Complexity</td>
        </tr>
        <tr>
            <td>insert</td>
            <td align="center">log(N) (first insertion of price level), O(1) else</td>
        </tr>
        <tr>
            <td>delete</td>
            <td align="center">O(1)</td>
        </tr>
        <tr>
            <td>execute</td>
            <td align="center">O(1)</td>
        </tr>
        <tr>
            <td>modify</td>
            <td align="center">O(1)</td>
        </tr>
    </table>
<br>
To guarantee constant execution time for all operations, the engine uses the following data structures: 

<p align="justify">
  - The matching engine manages a hash map which maps the name of each instrument to a specifc order book instance
- each order book manages two red black binary tree (one for bids, one for asks) which store ticks (price levels) in ascending order.
To quickly retreive a price level that has already been inserted into the tree in O(1), the orderbook also manages a hashtable that matches each price level to a node in the tree.
This allows to access each element in the tree in O(1) once inserted.
Further, the custom implementation of the RB-Tree stores references to the minimum and maximum tick values which allows for retreival of the best bid / ask in O(1)
- Each tick stores orders in a doubly linked list which allows to insert, delete and remove elements in O(1)
- To also be able to delete orders based on their orderID in O(1), each tick also administers a hash table which maps each orderID to a specific node/Order in the queue
</p>

## Setup
First, create an instance of MatchingEngine and TradeLogger. 
TradeLogger is responsible for logging the trading results in a separate thread. The current implementation is rather primitive. If a trade is flagged for logging, the trade logger will simply 
write the trade result to a file specified in the constructor of the tradeLogger. 
Once created, supply the matching engine with the trade logger instance.
```java
MatchingEngine matcher = new MatchingEngine();
TradeLogger tl = new TradeLogger("logFile.txt");
matcher.addTradeLoger(tl);
```

## add trades 
The engine currently allows for the addition of limit and market orders.
To add an order, create an instance of an order and pass it to the matching engine for execution. 

Make sure that each trade in the same instrument has a unique orderID 
```java
Order o1 = new LimitOrder("Amazon", 1, 100, 100, OrderType. SELL, true);
Order o2 = new LimitOrder("Amazon", 2, 100, OrderType.BUY, true);
matcher.add(o1);
matcher.add(o2);
```

## modify trades 
To modify a trade, pass an instance of the original trade as well as an instance of the modified trade to the matching engine. 
You can either supply the instance of the original order passed to the engine or in case this is not available anymore, you can create 
a new instance with the same parameters as the original order. The original order will then be retrieved from the matching book through the orderID
```java
```

## delete trades 
```java
Order orderOriginal = new LimitOrder("Amazon", 1, 100, 100, OrderType. SELL, true);
matcher.addOrder(orderOriginal);

 // original order instance may not be available anymore --> create a new 
Order orderReplicate = new LimitOrder("Amazon", 1, 100, 100, OrderType. SELL, true); 

// this deletes the original order inside the book matcher. deleteOrder(replicate) 
matcher.deleteOrder(orderReplicate);
```

## Performance statistics 
