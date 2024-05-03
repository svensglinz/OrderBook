package MatchingEngine;

import MatchingEngine.OrderTypes.Order;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a tick value (price) in an order book.
 * <br>
 * Tick extends RedBlackTreeNode. This allows for each tick to be part of a Red Black Binary Tree structure
 * which allows the order book to insert, locate and delete ticks while maintaining an ordered view of all ticks in O(logN)
 * <br>
 * Tick contains an internal queue that holds all orders with a limit price of the Tick values' <code>tickValue</code>
 * The class contains methods to add and remove orders from the tick as well as execute a specified amount of volume from the tick
 */

class Tick extends RedBlackTreeNode<Tick> {
    private final DoublyLinkedQueue<Order> orderQueue = new DoublyLinkedQueue<Order>();
    private final Map<Integer, Order> idToOrder = new HashMap<>();
    private final double tickValue;
    private double availableVolume;

    // to create a new tick, a first order must be submitted too
    public Tick(double tickValue, Order order){
        this.tickValue = tickValue;
        addOrder(order);
    }

    void addOrder(Order order){
        // add order and increase tick volume
        orderQueue.addLast(order);
        idToOrder.put(order.getOrderID(), order);
        availableVolume += order.getVolume();
    }

    /**
     * tradeVolume iterates over all trades in the Tick's internal order queue
     * and executes them until the needed volume is executed. Execution is done in FOFO order
     * @param volume volume to be removed from this tick (using trades in FIFO order)
     */
    void tradeVolume(double volume){

        double neededVolume = volume;
        Iterator<Order> it = orderQueue.iterator();

        while (neededVolume > 0 && it.hasNext()){
            Order nextOrder = it.next();

            // order is fully matched and removed from the Tick
            if (nextOrder.getVolume() <= neededVolume){
                neededVolume -= nextOrder.getVolume();
                nextOrder.tradeTotalVolume();

                // remove executed order from queue
                idToOrder.remove(nextOrder.getOrderID());
                it.remove();

                // partially execute the order and leave inside the order queue
            } else {
                nextOrder.tradeVolume(neededVolume);
                break;
            }
        }
        // update available volume inside tick
        this.availableVolume -= volume;
    }

    /**
     * Removes the total volume from this tick
     */
    void tradeTotalVolume(){
        tradeVolume(this.availableVolume);
    }

    /**
     * @param order order to be deleted
     */
    void deleteOrder(Order order){
        Order o = idToOrder.remove(order.getOrderID());
        orderQueue.remove(o);
    }

    // getter & helper methods
    boolean isEmpty(){return orderQueue.isEmpty();}
    double getTickValue() {return this.tickValue;}
    double getAvailableVolume() {return this.availableVolume;}
}
