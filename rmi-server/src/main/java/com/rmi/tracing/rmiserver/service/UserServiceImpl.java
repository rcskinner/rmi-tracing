package com.rmi.tracing.rmiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmi.tracing.UserService;
import com.rmi.tracing.TraceContext;
import com.rmi.tracing.TracingUtils;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;
import io.opentracing.Span;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    
    // Store users in memory using ConcurrentHashMap for thread safety
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDataProcessor userDataProcessor = new UserDataProcessor();

    public UserServiceImpl() throws RemoteException {
        super();
    }


    @Override
    public void updateUser(String userId, String userData, TraceContext traceContext) throws RemoteException {
        try {
            Span span = TracingUtils.continueTraceContext(traceContext, "rmi.updateUser");
            if (span != null) {
                span.setTag("user.id", userId);
            }
            
            System.out.println("RMI Call received - User ID: " + userId + ", Data: " + userData);
            users.put(userId, userData);
            System.out.println("User stored in memory. Total users: " + users.size());
            
            // Process user data with our instrumentable function
            userDataProcessor.processUserData(userId);
            
            if (span != null) {
                span.finish();
            }
        } catch (Exception e) {
            throw new RemoteException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public String getUser(String userId, TraceContext traceContext) throws RemoteException {
        try {
            Span span = TracingUtils.continueTraceContext(traceContext, "rmi.getUser");
            if (span != null) {
                span.setTag("user.id", userId);
            }
            
            String userData = users.get(userId);
            if (userData == null) {
                if (span != null) {
                    span.setTag("user.found", false);
                }
                if (span != null) {
                    span.finish();
                }
                return "{\"error\": \"User not found\", \"id\": \"" + userId + "\"}";
            }
            
            if (span != null) {
                span.setTag("user.found", true);
                span.finish();
            }
            return "{\"id\": \"" + userId + "\", \"data\": \"" + userData + "\"}";
        } catch (Exception e) {
            throw new RemoteException("Error getting user: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAllUsers(TraceContext traceContext) throws RemoteException {
        try {
            Span span = TracingUtils.continueTraceContext(traceContext, "rmi.getAllUsers");
            if (span != null) {
                span.setTag("user.count", users.size());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            try {
                String result = objectMapper.writeValueAsString(response);
                if (span != null) {
                    span.finish();
                }
                return result;
            } catch (Exception e) {
                if (span != null) {
                    span.finish();
                }
                throw new RuntimeException("Error serializing users to JSON: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RemoteException("Error getting all users: " + e.getMessage(), e);
        }
    }

}
