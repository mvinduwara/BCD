package lk.jiat.rmi;

import lk.jiat.rmi.Client.Message;
import lk.jiat.rmi.model.Data;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 6000);

            String[] list = registry.list();
            for (String s : list) {
                System.out.println(s);
            }

            Message message = (Message) registry.lookup("message_service");
            String msg = message.hello();

            Data data  = message.getData();

            System.out.println(msg.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
