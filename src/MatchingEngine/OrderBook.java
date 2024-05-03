package MatchingEngine;

import MatchingEngine.*;
import MatchingEngine.OrderTypes.*;

/**
 * This class represents an order book for a single instrument
 * it contains a red black binary tree for the bid and ask side
 * and methods to add orders as well as execute orders
 */
class OrderBook {
    private final OrderTree bidSide = new BidTree();
    private final OrderTree askSide = new AskTree();
    private TradeLogger tradeLogger;

    void setTradeLogger(TradeLogger tradeLogger){
        this.tradeLogger = tradeLogger;
    }

    /**
     * Before an order is added to the order book, it is checked if it can be fully or partially executed
     * given the best bid/ask. The order is only added in case not the full volume can be executed immediately
     * @param order order to be added to the order book
     */
    void addOrder(Order order){
        // set trade logger for each order
        order.setTradeLogger(tradeLogger);

        // check if order can be executed before adding it to the book
        if (order instanceof MarketOrder)
            marketOrder(order);
        else if (isExecutable(order)){
            if (order instanceof LimitOrder) {
                    limitOrder(order);
            }
        }

        // if not all volume was executed, add to order book
        if (order.getVolume() > 0) {
            if (order.getSide() == OrderType.BUY){
                bidSide.addOrder(order);
            } else {
                askSide.addOrder(order);
            }
        }
    }

    private void limitOrder(Order order){
        double traded;
        if (order.getSide().equals(OrderType.BUY)){
            // executes trade on the ask side
            traded = tradeVolume(order.getVolume(), order.getLimit(), askSide);
        } else {
            // executes trades on the bid side
            traded = tradeVolume(order.getVolume(), order.getLimit(), bidSide);
        }
        // execute trade on the opposite side
        order.tradeVolume(traded);
    }

    private void marketOrder(Order order){
        double traded;
        if (order.getSide().equals(OrderType.BUY)){
            traded = tradeVolume(order.getVolume(), askSide);
        } else {
            traded = tradeVolume(order.getVolume(), bidSide);
        }
        order.tradeVolume(traded);
    }

    private double tradeVolume(double volume, double limit, OrderTree side){
        double neededVolume = volume;

        // start order execution at the best price (lowest ask / highest bid)
        Tick t = side.getBestPrice();

        while (neededVolume > 0 && t != null){

            // ensure that limit is not violated
            if (side instanceof BidTree && t.getTickValue() < limit){
                break;
            } else if (side instanceof AskTree && t.getTickValue() > limit){
                break;
            }

            if (t.getAvailableVolume() <= neededVolume){
                double availableVolume = t.getAvailableVolume();
                // trade entire tick volume, reduce needed volume accordingly
                // delete tick from order book and get next best tick
                t.tradeVolume(neededVolume);
                neededVolume -= availableVolume;

                // delete tick from order tree and order hashmap
                side.orders.delete(t);
                side.orderMap.remove(t.getTickValue());

                // get the next best price
                t = side.getBestPrice();
            } else {
                t.tradeVolume(neededVolume);
                neededVolume = 0;
                break;
            }
        }
        return (volume - neededVolume); // returns traded volume
    }

    private double tradeVolume(double volume, OrderTree side){
        return tradeVolume(volume, Double.MAX_VALUE, side);
    }

    void updateOrder(Order originalOrder, Order updatedOrder){
        // register trade logger for updated order
        updatedOrder.setTradeLogger(tradeLogger);

        if (originalOrder.getSide() == OrderType.BUY){
            bidSide.updateOrder(originalOrder, originalOrder);
        } else {
            askSide.updateOrder(originalOrder, originalOrder);
        }
    }

    void deleteOrder(Order order) {
        if (order.getSide() == OrderType.BUY){
            bidSide.deleteOrder(order);

        } else {
            askSide.deleteOrder(order);
        }
    }

    // check if an order can be executed given the best bid and ask
    private boolean isExecutable(Order order){

        // market orders are always executable
        if (order instanceof MarketOrder)
            return true;

        if (order.getSide().equals(OrderType.BUY)){
            Tick bestPrice = askSide.getBestPrice();
            if (bestPrice == null)
                return false;
            else
                return (bestPrice.getTickValue() <= order.getLimit());
        } else {
            Tick bestPrice = bidSide.getBestPrice();
            if (bestPrice == null)
                return false;
            else
                return (bestPrice.getTickValue() >= order.getLimit());
        }
    }
    boolean isEmpty(){
        return bidSide.size() == 0 && askSide.size() == 0;
    }
}
