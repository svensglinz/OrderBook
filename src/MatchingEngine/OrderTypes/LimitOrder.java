package MatchingEngine.OrderTypes;

import MatchingEngine.OrderType;

/**
 * Implementation of a limit order that can be submitted to the order book.
 */
public class LimitOrder extends Order {

    public LimitOrder(String product, int orderID, double limit, double volume, OrderType side, boolean logTrade) {
        super(product, orderID, volume, side, logTrade);
        this.limit = limit;
    }
}
