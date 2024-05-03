package MatchingEngine.OrderTypes;

import MatchingEngine.OrderType;

/**
 * Order subclass for a market order. This order is executed immediately in the order book if enough volume is
 * available. For simplification of the order book, the <code>Market Order</code> is submitted as a limit order with <code>limit = Integer.MIN_VALUE</code> (for SELL)
 * and <code>limit = Integer.MAX_VALUE</code> (for BUY). This implementation breaks down if prices are allowed to be outside of this range which is unlikely
 */
public class MarketOrder extends Order {
    public MarketOrder(String product, int orderID, double volume, OrderType side, boolean logTrade) {
        super(product, orderID, volume, side, logTrade);

        if (side.equals(OrderType.BUY))
            limit = Integer.MAX_VALUE;
        else
            limit = Integer.MIN_VALUE;
    }
}
