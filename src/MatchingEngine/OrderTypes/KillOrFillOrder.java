package MatchingEngine.OrderTypes;
import MatchingEngine.*;
import MatchingEngine.OrderType;

public class KillOrFillOrder extends Order {
    public KillOrFillOrder(String product, double price, int orderID, double volume, OrderType side, boolean logTrade) {
        super(product, orderID, volume, side, logTrade);
    }
}
