package lk.jiat.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MessageImpl extends UnicastRemoteObject implements Message, Remote {

    MessageImpl() throws RemoteException {}

    @Override
    public void hello() {
        System.out.println("server: hello world...........");
    }
}
