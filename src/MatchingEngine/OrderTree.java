package MatchingEngine;

import MatchingEngine.OrderTypes.Order;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

class OrderTree {
    //TreeMap<Double, Tick> orders;
    Map<Double, Tick> orderMap;
    RedBlackTree<Tick> orders;
    public OrderTree() {
        orderMap = new HashMap<>(1_000);
        orders = new RedBlackTree<Tick>(Comparator.comparingDouble((Tick a) -> a.getTickValue()));
    }

    /**
     * add a new order to the Order Book
     * If the OrderTree already contains a tick with the same price as the Price of the Order,
     * the order is appended to the internal order queue of the tick.
     * If no tick for the price of the order can be found, a new tick is created and appended
     * to the order tree
     *
     * @param order order which is added to the order book
     */
    public void addOrder(Order order) {

        Tick t = orderMap.get(order.getLimit());
        // tick is not yet in orderMap
        if (t == null) {
            Tick tick = new Tick(order.getLimit(), order);
            orders.insert(tick);
            orderMap.put(order.getLimit(), tick);
        } else {
            t.addOrder(order);
        }
    }

    /**
     * Attempts to delete the passed order from the orderbook. If the order is not in the book,
     * nothing is executed
     *
     * @param order order to be deleted from the orderbook
     */
    void deleteOrder(Order order) {
        Tick tick = orderMap.get(order.getLimit());

        if (tick != null){
            tick.deleteOrder(order);

            // if order queue of tick is now empty, tick can be deleted too
            if (tick.isEmpty()){
                orders.delete(tick);
                orderMap.remove(order.getLimit());
            }
        }
    }

    void updateOrder(Order originalOrder, Order modifiedOrder){
        deleteOrder(originalOrder);
        addOrder(modifiedOrder);
    }

    // method implemented in subclasses
    Tick getBestPrice(){
        return null;
    }

    public int size(){
        return orders.size();
    }
}

class BidTree extends OrderTree {
    @Override
    public Tick getBestPrice(){
        return orders.getMaximum();
    }
}

class AskTree extends OrderTree {
    @Override
    public Tick getBestPrice(){
        return orders.getMinimum();
    }
}