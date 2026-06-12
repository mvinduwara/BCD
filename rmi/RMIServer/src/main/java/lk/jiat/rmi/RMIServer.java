package lk.jiat.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.createRegistry(6000);
            registry.rebind("message_service", new MessageImpl());

            System.out.println("RMI Server ready");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
