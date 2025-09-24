package com.rmi.tracing;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Serializable trace context for RMI propagation
 */
public class TraceContext implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<String, String> contextMap;
    
    public TraceContext() {
        this.contextMap = new HashMap<>();
    }
    
    public TraceContext(Map<String, String> contextMap) {
        this.contextMap = contextMap != null ? new HashMap<>(contextMap) : new HashMap<>();
    }
    
    public Map<String, String> getContextMap() {
        return contextMap;
    }
    
    public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap != null ? new HashMap<>(contextMap) : new HashMap<>();
    }
    
    public boolean isEmpty() {
        return contextMap == null || contextMap.isEmpty();
    }
}
