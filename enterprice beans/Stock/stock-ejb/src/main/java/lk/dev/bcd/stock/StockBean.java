package lk.dev.bcd.stock;

import java.util.HashMap;
import java.util.Map;

public class StockBean implements StockService {

    private Map<String, Integer> stock = new HashMap<>();

    public StockBean() {
        stock.put("Apple", 100);
        stock.put("Pine Apple", 100);
        stock.put("Orange", 100);
        stock.put("Mango", 100);
        stock.put("Peach", 100);
    }

    @java.lang.Override
    public int getstock(java.lang.String product) {
        return 0;
    }
}
