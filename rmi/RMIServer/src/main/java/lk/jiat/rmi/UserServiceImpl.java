package lk.jiat.rmi;

import lk.jiat.rmi.Client.UserService;
import lk.jiat.rmi.model.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private Map<Integer, User> users = RMIServer.users;

    public UserServiceImpl() throws RemoteException {
    }

    @Override
    public User getUserById(int id) throws RemoteException {
        return users.get(id);
    }

    @Override
    public void addUser(Integer id, User user) throws RemoteException {
        users.put(id, user);
    }

    @Override
    public void updateUser(Integer id, User user) throws RemoteException {
        users.put(id, user);
    }

    @Override
    public void deleteUser(int id) throws RemoteException {
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() throws RemoteException {
        return List.of(users.values().toArray(new User[0]));
    }
}
