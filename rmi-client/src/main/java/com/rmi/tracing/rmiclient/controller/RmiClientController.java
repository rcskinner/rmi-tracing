package com.rmi.tracing.rmiclient.controller;

import com.rmi.tracing.UserService;
import com.rmi.tracing.rmiclient.client.RmiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.rmi.Naming;
import java.rmi.RemoteException;

@RestController
@RequestMapping("/api")
public class RmiClientController {

    @Autowired
    private RmiClient rmiClient;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Service A!";
    }

    @PostMapping("/add-user")
    public String addUser(@RequestParam String userId, @RequestParam String userData) {
        try {
            rmiClient.updateUser(userId, userData);
            return "User " + userId + " added successfully via RMI";
        } catch (Exception e) {
            return "Error adding user: " + e.getMessage();
        }
    }
}
