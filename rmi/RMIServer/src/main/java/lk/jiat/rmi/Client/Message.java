package lk.jiat.rmi.Client;

import lk.jiat.rmi.model.Data;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Message extends Remote {

    public String hello() throws RemoteException;
    public Data getData() throws RemoteException;
}
