package com.rmi.tracing;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;

import java.util.Map;
import java.util.HashMap;

/**
 * Utility class for handling distributed tracing in RMI services.
 * Provides core functionality for trace context extraction and span creation.
 */
public class TracingUtils {

    /**
     * Extract trace context from TraceContext and continue the distributed trace.
     * Returns null if tracing is not available or fails, ensuring service continues to work.
     * @param traceContext The trace context from the RMI call
     * @param operationName The name of the operation for the span
     * @return A new span that continues the distributed trace, or null if tracing fails
     */
    public static Span continueTraceContext(TraceContext traceContext, String operationName) {
        try {
            Tracer tracer = GlobalTracer.get();
            
            // Check if tracer is available
            if (tracer == null) {
                System.err.println("Warning: No tracer available for operation '" + operationName + "'");
                return null;
            }
            
            // No trace context provided, create a new root span
            if (traceContext == null || traceContext.isEmpty()) {
                return tracer.buildSpan(operationName).start();
            }
            
            // Extract trace information for span tags
            String traceId = traceContext.getTraceId();
            String parentSpanId = traceContext.getParentSpanId();
            boolean sampled = traceContext.isSampled();
            
            // Extract the trace context from the baggage map
            Map<String, String> contextMap = traceContext.getBaggage();
            SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapAdapter(contextMap));
            
            // Continue the existing trace            
            if (spanContext != null) {
                Span span = tracer.buildSpan(operationName)
                        .asChildOf(spanContext)
                        .start();
                
                // Add trace metadata as span tags for better observability
                if (traceId != null) {
                    span.setTag("trace.id", traceId);
                }
                if (parentSpanId != null) {
                    span.setTag("parent.span.id", parentSpanId);
                }
                span.setTag("trace.sampled", sampled);
                span.setTag("trace.source", "rmi");
                
                return span;
            } else {
                // Failed to extract context, create new root span
                System.err.println("Warning: Failed to extract span context from baggage, creating new root span");
                return tracer.buildSpan(operationName).start();
            }
        } catch (Exception e) {
            // Log the error but don't break the service
            System.err.println("Warning: Failed to create span for operation '" + operationName + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract trace context from current active span for RMI propagation.
     * Returns empty TraceContext if no active span or tracing fails.
     * @return TraceContext containing current span context, or empty context if none available
     */
    public static TraceContext extractTraceContext() {
        try {
            Tracer tracer = GlobalTracer.get();
            
            // Check if tracer is available
            if (tracer == null) {
                System.err.println("Warning: No tracer available, returning empty trace context");
                return new TraceContext();
            }
            
            Span currentSpan = tracer.activeSpan();
            if (currentSpan == null) {
                // No active span, return empty context
                return new TraceContext();
            }
            
            // Extract individual trace fields from the current span
            SpanContext spanContext = currentSpan.context();
            String traceId = spanContext.toTraceId();
            String spanId = spanContext.toSpanId();
            String parentSpanId = null; // OpenTracing doesn't provide direct parent span ID access
            
            // Determine sampling decision - assume sampled if we have an active span
            boolean sampled = true;
            
            // Inject the current span context into a map for baggage
            Map<String, String> contextMap = new HashMap<>();
            tracer.inject(spanContext, Format.Builtin.TEXT_MAP, new TextMapAdapter(contextMap));
            
            // Create TraceContext with all individual fields populated
            return new TraceContext(traceId, spanId, parentSpanId, sampled, contextMap);
            
        } catch (Exception e) {
            // Log the error but don't break the service
            System.err.println("Warning: Failed to extract trace context: " + e.getMessage());
            return new TraceContext();
        }
    }
}
