package com.rmi.tracing.rmiserver.service;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

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
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("userDataProcessor.processUserData")
                .withTag("user.id", userId)
                .withTag("operation", "processUserData")
                .start();
        
        try {
            System.out.println("Processing user data for: " + userId);
            
            // Create a child span for the sleep operation
            Span sleepSpan = tracer.buildSpan("userDataProcessor.customInstrumentedSleep")
                    .asChildOf(span)
                    .withTag("customInstrumentedSleep.duration.ms", 500)
                    .start();
            
            try {
                Thread.sleep(500); // Sleep for 0.5 seconds
                sleepSpan.setTag("success", true);
            } catch (InterruptedException e) {
                sleepSpan.setTag("error", true);
                sleepSpan.setTag("error.message", "Sleep interrupted");
                throw e;
            } finally {
                sleepSpan.finish();
            }
            
            System.out.println("Finished processing user data for: " + userId);
            span.setTag("success", true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processing interrupted for user: " + userId);
            span.setTag("error", true);
            span.setTag("error.message", "Processing interrupted");
        } finally {
            span.finish();
        }
    }
}
