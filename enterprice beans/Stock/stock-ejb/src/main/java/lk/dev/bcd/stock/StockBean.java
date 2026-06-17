package lk.dev.bcd.stock;

import jakarta.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class StockBean implements StockService {

    private Map<String, Integer> stock = new HashMap<>();

    public StockBean() {
        stock.put("Apple", 52);
        stock.put("Pine Apple", 34);
        stock.put("Orange", 25);
        stock.put("Mango", 77);
        stock.put("Peach", 100);
    }

    @java.lang.Override
    public int getstock(java.lang.String product) {
        return 0;
    }
}
