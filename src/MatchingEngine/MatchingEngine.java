package MatchingEngine;
import MatchingEngine.OrderTypes.Order;

import java.util.Map;
import java.util.HashMap;

/**
 * This class implements a Matching Engine that allows for the following operations
 * <ul>
 * <li>insert trade -  for first trade at specified limit O((logN)), else O(1)</li>
 * <li>delete trade - O(1)</li>
 * <li>modify trade - O(1)</li>
 * <li>execute trade - O(1)</li>
 * </ul>
 * The Engine supports multiple order books for different instruments
 * Each trade must be submitted as an instance of an <code>MatchingEngine.OrderTypes.Order</code> object
 <br>
 <br>
 * Orders are matched using a Price/Time algorithm whereby orders at the same price are executed according
 * to their time of submission and orders with better prices (i.e. higher bids / lower asks) are executed before orders
 * with worse prices (lower bids / higher asks)
 <br>
 <br>
 The MatchingEngine.OrderBook constructor needs an instance of type <code>MatchingEngine.TradeLogger</code> which allows it to write executed trades
 which have the flag <code>logTrade</code> set to true to a logfile
 <br>
 <br>
 To start, create an instance of <code>MatchingEngine.TradeLogger</code> and <code>MatchingEngine.MatchingEngine</code> and
 register the logger
 <pre>{@code
MatchingEngine matcher = new MatchingEngine();
TradeLogger tl = new TradeLogger();
matcher.setTradeLogger(tl);
}</pre>
 To add new trades, create instances of <code>MarketOrder</code> or <code>LimitOrder</code> and submit them to the matcher
 <pre>{@code
 Order order = new LimitOrder("Amazon", 1, 100, 100, OrderType.SELL, true);
 matcher.addOrder(order);}</pre>
 In order to delete or modify trades, the same Order instance can be used. However, the matcher also accepts a new instance
 with identical parameters to the order that needs to be updated / deleted.

 Ultimately, the thread running the tradeLogger must be terminated with {@code tl.shutdown();}
 */
public class MatchingEngine {
    TradeLogger tradeLogger;
    Map<String, OrderBook> orderBooks = new HashMap<>();

    public void setTradeLogger(TradeLogger tradeLogger){
        this.tradeLogger = tradeLogger;
    }

    public void addOrder(Order order){
    OrderBook book = orderBooks.get(order.getProduct());

        // create new order book for new product type
        if (book == null){
            book = new OrderBook();
            book.setTradeLogger(tradeLogger);
            orderBooks.put(order.getProduct(), book);
            book.addOrder(order);
        // add product to existing order book
        } else {
            book.addOrder(order);
        }
    }

    /**
     * Deletes all trades inside the order book with the specified name
     * @param product order book to be deleted
     */
    public void closeBook(String product){
        orderBooks.remove(product);
    }

    /**
     * Deletes all order books and restores the initial state of the
     * matching engine
     */
    public void closeAllBooks(){
        orderBooks.clear();
    }

    /**
     * Deletes the supplied order from the order book
     * The method either accepts the original instance of the order that was
     * added or a new order instance with the same parameters as the original order
     * that should be deleted in the book
     * <pre>{@code
     * Order orderOriginal = new LimitOrder("Amazon", 1, 100, 100, OrderType.SELL, true);
     * matcher.addOrder(orderOriginal
     *
     * // original order instance may not be available anymore --> create a new one
     * Order replicate = new LimitOrder("Amazon", 1, 100, 100, OrderType.SELL, true);
     * // this deletes the original order inside the book
     * matcher.deleteOrder(replicate) }</pre>
     * @param order Order to be deleted
     */
    public void deleteOrder(Order order){
        OrderBook book = orderBooks.get(order.getProduct());
        if (book != null)
            book.deleteOrder(order);
    }

    // like this, order modification leads to a loss of the order's place in the queue
    // and the modified order is inserted at the end of the queue
    public void modifyOrder(Order originalOrder, Order modifiedOrder){

        // checks
        if (originalOrder.getSide().equals(modifiedOrder.getSide()))
            throw new IllegalArgumentException("Order modification cannot change between buy and sell");
        if (originalOrder.getClass() != modifiedOrder.getClass())
            throw new IllegalArgumentException("Order modification cannot change between order types");
        if (!originalOrder.getProduct().equals(modifiedOrder.getProduct()))
            throw new IllegalArgumentException("Order modification cannot change between products");

        // should throw errors down the line if order does not exist in the order book anymore!
        OrderBook book = orderBooks.get(originalOrder.getProduct());
        if (book != null){
            book.updateOrder(originalOrder, modifiedOrder);
        }
    }
}
