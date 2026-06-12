package lk.jiat.rmi;

import lk.jiat.rmi.model.User;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RMIServer {

    static Map<Integer,User> users = new HashMap<>();

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(6000);
            registry.rebind("message_service", new MessageImpl());
            registry.rebind("user_service", new UserServiceImpl());

            System.out.println("RMI Server ready");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
