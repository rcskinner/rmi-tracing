package com.rmi.tracing.rmiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmi.tracing.UserService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    
    // Store users in memory using ConcurrentHashMap for thread safety
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void updateUser(String userId, String userData) throws RemoteException {
        System.out.println("RMI Call received - User ID: " + userId + ", Data: " + userData);
        users.put(userId, userData);
        System.out.println("User stored in memory. Total users: " + users.size());
    }

    @Override
    public String getUser(String userId) throws RemoteException {
        String userData = users.get(userId);
        if (userData == null) {
            return "{\"error\": \"User not found\", \"id\": \"" + userId + "\"}";
        }
        return "{\"id\": \"" + userId + "\", \"data\": \"" + userData + "\"}";
    }

    @Override
    public String getAllUsers() throws RemoteException {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RemoteException("Error serializing users to JSON: " + e.getMessage(), e);
        }
    }
}
