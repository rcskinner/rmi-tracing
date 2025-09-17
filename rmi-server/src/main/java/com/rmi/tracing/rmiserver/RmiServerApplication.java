package com.rmi.tracing.rmiserver;

import com.rmi.tracing.UserService;
import com.rmi.tracing.rmiserver.service.UserServiceImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServerApplication {
    public static void main(String[] args) {
        try {
            // Create RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry created on port 1099");
            
            // Create the service implementation
            UserService userService = new UserServiceImpl();
            
            // Bind the service to the registry
            registry.rebind("UserService", userService);
            System.out.println("RMI Service 'UserService' registered successfully");
            
            // Keep the server running
            System.out.println("RMI Server is running... Press Ctrl+C to stop");
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Error starting RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
