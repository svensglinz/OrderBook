package MatchingEngine.OrderTypes;

import MatchingEngine.*;
/**
 * Order contains all relevant information attached to an order submitted to an instance of <code>OrderBook</code>
 */

public class Order extends DoublyLinkedQueueNode<Order> {
    private final int orderID;
    private double volume;
    private final OrderType side;
    private final boolean logTrade;
    private final String product;
    private TradeLogger tradeLogger;
    double limit = 0;

    public Order(String product, int orderID, double volume, OrderType side, boolean logTrade){
        this.product = product;
        this.side = side;
        this.orderID = orderID;
        this.volume = volume;
        this.logTrade = logTrade;
    }

    public void tradeVolume(double volume){
        if (volume > this.volume)
            throw new IllegalArgumentException("volume too high for order to be executed");
        // invoke trade logger if it is a flagged order
        if (logTrade)
            tradeLogger.logTrade(this + ", " + volume);

        this.volume -= volume;
    }

    public void tradeTotalVolume(){
        tradeVolume(this.volume);
    }

    @Override
    public String toString() {
        return ("" + this.orderID);
    }

    // getter & setter methods
    public double getLimit() {return limit;}
    public double getVolume() {return volume;}
    public OrderType getSide() {return side;}
    public String getProduct() {return product;}
    public int getOrderID() {return orderID;}
    public void setTradeLogger(TradeLogger tradeLogger){this.tradeLogger = tradeLogger;}
}

