package com.rmi.tracing.rmiclient.client;

import com.rmi.tracing.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.Naming;

@Component
public class RmiClient {
    //How do we connect 
    @Value("${RMI_SERVICE_B_URL:rmi://rmi-server:1099/UserService}")
    private String serviceBUrl;

    public void updateUser(String userId, String userData) throws Exception {
        try {
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            userService.updateUser(userId, userData);
        } catch (Exception e) {
            throw new Exception("Failed to call Service B via RMI: " + e.getMessage(), e);
        }
    }
}
