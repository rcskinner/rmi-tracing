package com.rmi.tracing.rmiclient.client;

import com.rmi.tracing.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.Naming;

@Component
public class RmiClient {
    //How do we connect 
    @Value("${RMI_SERVER_URL:rmi://rmi-server:1099/UserService}")
    private String serviceBUrl;

    public void updateUser(String userId, String userData) throws Exception {
        try {
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            userService.updateUser(userId, userData);
        } catch (Exception e) {
            throw new Exception("Failed to call Service B via RMI: " + e.getMessage(), e);
        }
    }

    public String getUser(String userId) throws Exception {
        try {
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            return userService.getUser(userId);
        } catch (Exception e) {
            throw new Exception("Failed to get user via RMI: " + e.getMessage(), e);
        }
    }

    public String getAllUsers() throws Exception {
        try {
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            return userService.getAllUsers();
        } catch (Exception e) {
            throw new Exception("Failed to get all users via RMI: " + e.getMessage(), e);
        }
    }
}
