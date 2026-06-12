package lk.jiat.rmi;

import lk.jiat.rmi.Client.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MessageImpl extends UnicastRemoteObject implements Message {

    MessageImpl() throws RemoteException {}

    @Override
    public String hello() throws RemoteException {
        return "server: hello world...........";
    }
}
