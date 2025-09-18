package com.rmi.tracing;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {
    void updateUser(String userId, String userData) throws RemoteException;
    String getUser(String userId) throws RemoteException;
    String getAllUsers() throws RemoteException;
}
