package com.rmi.tracing.rmiclient.client;

import com.rmi.tracing.UserService;
import com.rmi.tracing.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.Naming;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import java.util.HashMap;
import java.util.Map;

@Component
public class RmiClient {
    //How do we connect 
    @Value("${RMI_SERVER_URL:rmi://rmi-server:1099/UserService}")
    private String serviceBUrl;
    
    /**
     * Extract trace context from current span for RMI propagation
     */
    private TraceContext extractTraceContext() {
        Tracer tracer = GlobalTracer.get();
        Span currentSpan = tracer.activeSpan();
        if (currentSpan == null) {
            return new TraceContext();
        }
        
        Map<String, String> contextMap = new HashMap<>();
        tracer.inject(currentSpan.context(), Format.Builtin.TEXT_MAP, new TextMapAdapter(contextMap));
        return new TraceContext(contextMap);
    }

    public void updateUser(String userId, String userData) throws Exception {
        try {
            TraceContext traceContext = extractTraceContext();
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            userService.updateUser(userId, userData, traceContext);
        } catch (Exception e) {
            throw new Exception("Failed to call Service B via RMI: " + e.getMessage(), e);
        }
    }

    public String getUser(String userId) throws Exception {
        try {
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            TraceContext traceContext = extractTraceContext();
            return userService.getUser(userId, traceContext);
        } catch (Exception e) {
            throw new Exception("Failed to get user via RMI: " + e.getMessage(), e);
        }
    }

    public String getAllUsers() throws Exception {
        try {
            UserService userService = (UserService) Naming.lookup(serviceBUrl);
            TraceContext traceContext = extractTraceContext();
            return userService.getAllUsers(traceContext);
        } catch (Exception e) {
            throw new Exception("Failed to get all users via RMI: " + e.getMessage(), e);
        }
    }
}
