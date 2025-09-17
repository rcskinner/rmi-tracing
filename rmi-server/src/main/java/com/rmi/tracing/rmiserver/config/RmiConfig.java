package com.rmi.tracing.rmiserver.config;

import com.rmi.tracing.UserService;
import com.rmi.tracing.rmiserver.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

@Configuration
public class RmiConfig {

    @Autowired
    private UserServiceImpl userService;

    @PostConstruct
    public void registerRmiService() {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("UserService", userService);
            System.out.println("RMI Service registered successfully");
        } catch (Exception e) {
            System.err.println("Error registering RMI service: " + e.getMessage());
        }
    }
}
