
import MatchingEngine.*;
import MatchingEngine.OrderTypes.*;

import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // register new trade logger
        TradeLogger tl = new TradeLogger("logFile.txt");

        // create match engine and register trade logger
        MatchingEngine matcher = new MatchingEngine();
        matcher.setTradeLogger(tl);

        // execute trades
        Random rand = new Random();
        long t1 = System.nanoTime();

        String[] inst = new String[]{"1", "2", "3", "4", "5"};
        int limit = 5_000_000;
        for (int i = 0; i < limit; i++){

            Order o1 = new LimitOrder(inst[i%5], 1, rand.nextInt(1000), rand.nextInt(1000), OrderType.SELL, false);
            Order o2 = new LimitOrder(inst[i%5], 1, rand.nextInt(1000), rand.nextInt(1000), OrderType.BUY, false);

            matcher.addOrder(o2);
            matcher.addOrder(o1);
            //matcher.deleteOrder(o1);
        }

        long t2 = System.nanoTime();
    System.out.println((t2 - t1) / (2.0 * limit) + " nanoseconds / order");
        System.out.println((t2 - t1) / 1.0e9 + " seconds total");

        // shut down trade logger thread
        tl.shutdown();
    }
}