package com.rmi.tracing;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Serializable trace context for RMI propagation
 */
public class TraceContext implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private boolean sampled;
    private Map<String, String> baggage;
    
    public TraceContext() {
        this.baggage = new HashMap<>();
    }
    
    public TraceContext(String traceId, String spanId, String parentSpanId, boolean sampled, Map<String, String> baggage) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.sampled = sampled;
        this.baggage = baggage != null ? new HashMap<>(baggage) : new HashMap<>();
    }
    
    // Getters
    public String getTraceId() {
        return traceId;
    }
    
    public String getSpanId() {
        return spanId;
    }
    
    public String getParentSpanId() {
        return parentSpanId;
    }
    
    public boolean isSampled() {
        return sampled;
    }
    
    public Map<String, String> getBaggage() {
        return baggage;
    }
    
    // Setters
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    
    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }
    
    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }
    
    public void setSampled(boolean sampled) {
        this.sampled = sampled;
    }
    
    public void setBaggage(Map<String, String> baggage) {
        this.baggage = baggage != null ? new HashMap<>(baggage) : new HashMap<>();
    }
    
    public boolean isEmpty() {
        return traceId == null && spanId == null && parentSpanId == null && 
               (baggage == null || baggage.isEmpty());
    }
}
