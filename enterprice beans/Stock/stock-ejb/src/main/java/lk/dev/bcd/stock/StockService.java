package lk.dev.bcd.stock;

import jakarta.ejb.Remote;

@Remote
public interface StockService {
    int getstock(String product);
}
