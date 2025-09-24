package com.rmi.tracing;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {
    void updateUser(String userId, String userData, TraceContext traceContext) throws RemoteException;
    String getUser(String userId, TraceContext traceContext) throws RemoteException;
    String getAllUsers(TraceContext traceContext) throws RemoteException;
}
