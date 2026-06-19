package lk.dev.bcd;

import javax.naming.InitialContext;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client Application Started");

        try {
            InitialContext ic = new InitialContext();



        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
