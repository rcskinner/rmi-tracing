package com.rmi.tracing.utils;

import com.rmi.tracing.TraceContext;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling distributed tracing in RMI services.
 * This class provides reusable methods for trace context extraction and
 * business logic execution with automatic tracing.
 */
public class TracingUtils {

    /**
     * Extract trace context from TraceContext and continue the distributed trace
     * @param traceContext The trace context from the RMI call
     * @param operationName The name of the operation for the span
     * @return A new span that continues the distributed trace
     */
    public static Span continueTraceContext(TraceContext traceContext, String operationName) {
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

    /**
     * Helper method to wrap business logic with tracing boilerplate.
     * This method handles span creation, tagging, error handling, and cleanup.
     * 
     * @param traceContext The trace context from the RMI call
     * @param operationName The name of the operation for the span
     * @param businessLogic The business logic to execute, receives the span for custom tagging
     * @param <T> The return type of the business logic
     * @return The result from the business logic
     * @throws Exception Any exception thrown by the business logic
     */
    public static <T> T executeWithTracing(TraceContext traceContext, String operationName, Function<Span, T> businessLogic) throws Exception {
        Span span = continueTraceContext(traceContext, operationName);
        try {
            span.setTag("operation", operationName);
            T result = businessLogic.apply(span);
            span.setTag("success", true);
            return result;
        } catch (Exception e) {
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            throw e;
        } finally {
            span.finish();
        }
    }

    /**
     * Helper method for void operations that don't return a value.
     * 
     * @param traceContext The trace context from the RMI call
     * @param operationName The name of the operation for the span
     * @param businessLogic The business logic to execute, receives the span for custom tagging
     * @throws Exception Any exception thrown by the business logic
     */
    public static void executeWithTracing(TraceContext traceContext, String operationName, java.util.function.Consumer<Span> businessLogic) throws Exception {
        Span span = continueTraceContext(traceContext, operationName);
        try {
            span.setTag("operation", operationName);
            businessLogic.accept(span);
            span.setTag("success", true);
        } catch (Exception e) {
            span.setTag("error", true);
            span.setTag("error.message", e.getMessage());
            throw e;
        } finally {
            span.finish();
        }
    }
}

