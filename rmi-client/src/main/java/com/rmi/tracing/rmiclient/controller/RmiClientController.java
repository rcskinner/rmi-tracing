package com.rmi.tracing.rmiclient.controller;

import com.rmi.tracing.rmiclient.client.RmiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/get-users")
    public String getAllUsers() {
        try {
            return rmiClient.getAllUsers();
        } catch (Exception e) {
            return "{\"error\": \"Failed to retrieve users: " + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/get-user/{userId}")
    public String getUser(@PathVariable String userId) {
        try {
            return rmiClient.getUser(userId);
        } catch (Exception e) {
            return "{\"error\": \"Failed to retrieve user: " + e.getMessage() + "\"}";
        }
    }
}
