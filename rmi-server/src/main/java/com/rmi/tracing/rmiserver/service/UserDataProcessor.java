package com.rmi.tracing.rmiserver.service;

/**
 * Simple service class for processing user data.
 * This class is designed to be instrumented with Datadog APM tracing.
 */
public class UserDataProcessor {
    
    /**
     * Processes user data with a simulated delay.
     * This method is intended for Datadog APM instrumentation demonstration.
     * 
     * @param userId the ID of the user to process
     */
    public void processUserData(String userId) {
        try {
            System.out.println("Processing user data for: " + userId);
            Thread.sleep(500); // Sleep for 0.5 seconds
            System.out.println("Finished processing user data for: " + userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processing interrupted for user: " + userId);
        }
    }
}
