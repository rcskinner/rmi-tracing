package com.rmi.tracing.rmiserver.service;

import com.rmi.tracing.UserService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    public UserServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void updateUser(String userId, String userData) throws RemoteException {
        System.out.println("RMI Call received - User ID: " + userId + ", Data: " + userData);
        System.out.println("Logging user update event for tracing purposes");
    }
}
