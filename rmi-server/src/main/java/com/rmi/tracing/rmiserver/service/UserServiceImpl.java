package com.rmi.tracing.rmiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmi.tracing.UserService;
import com.rmi.tracing.TraceContext;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    
    // Store users in memory using ConcurrentHashMap for thread safety
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDataProcessor userDataProcessor = new UserDataProcessor();

    public UserServiceImpl() throws RemoteException {
        super();
    }

    /**
     * Extract trace context from TraceContext and continue the distributed trace
     */
    private Span continueTraceContext(TraceContext traceContext, String operationName) {
        Tracer tracer = GlobalTracer.get();
        
        if (traceContext == null || traceContext.isEmpty()) {
            // No trace context provided, create a new root span
            return tracer.buildSpan(operationName).start();
        }
        
        // Extract the trace context from the map
        Map<String, String> contextMap = traceContext.getContextMap();
        SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapAdapter(contextMap));
        
        if (spanContext != null) {
            // Continue the existing trace
            return tracer.buildSpan(operationName)
                    .asChildOf(spanContext)
                    .start();
        } else {
            // Failed to extract context, create new root span
            return tracer.buildSpan(operationName).start();
        }
    }

    @Override
    public void updateUser(String userId, String userData, TraceContext traceContext) throws RemoteException {
        Span span = continueTraceContext(traceContext, "rmi.updateUser");
        try {
            span.setTag("user.id", userId);
            span.setTag("operation", "updateUser");
            
            System.out.println("RMI Call received - User ID: " + userId + ", Data: " + userData);
            users.put(userId, userData);
            System.out.println("User stored in memory. Total users: " + users.size());
            
            // Process user data with our instrumentable function
            userDataProcessor.processUserData(userId);
            
            span.setTag("success", true);
        } catch (Exception e) {
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            throw e;
        } finally {
            span.finish();
        }
    }

    @Override
    public String getUser(String userId, TraceContext traceContext) throws RemoteException {
        Span span = continueTraceContext(traceContext, "rmi.getUser");
        try {
            span.setTag("user.id", userId);
            span.setTag("operation", "getUser");
            
            String userData = users.get(userId);
            if (userData == null) {
                span.setTag("user.found", false);
                span.setTag("success", true);
                return "{\"error\": \"User not found\", \"id\": \"" + userId + "\"}";
            }
            
            span.setTag("user.found", true);
            span.setTag("success", true);
            return "{\"id\": \"" + userId + "\", \"data\": \"" + userData + "\"}";
        } catch (Exception e) {
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            throw e;
        } finally {
            span.finish();
        }
    }

    @Override
    public String getAllUsers(TraceContext traceContext) throws RemoteException {
        Span span = continueTraceContext(traceContext, "rmi.getAllUsers");
        try {
            span.setTag("operation", "getAllUsers");
            span.setTag("user.count", users.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            String result = objectMapper.writeValueAsString(response);
            
            span.setTag("success", true);
            return result;
        } catch (Exception e) {
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            throw new RemoteException("Error serializing users to JSON: " + e.getMessage(), e);
        } finally {
            span.finish();
        }
    }

}
