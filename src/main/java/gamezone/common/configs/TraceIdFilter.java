package gamezone.common.configs;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.UUID;

@Provider
public class TraceIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
    public static final String TRACE_ID_KEY = "traceId";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String incomingTraceId = requestContext.getHeaderString("X-Trace-Id");
        String traceId = incomingTraceId == null || incomingTraceId.isBlank() ? UUID.randomUUID().toString() : incomingTraceId;
        requestContext.setProperty(TRACE_ID_KEY, traceId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object traceId = requestContext.getProperty(TRACE_ID_KEY);
        if (traceId != null) {
            responseContext.getHeaders().putSingle("X-Trace-Id", traceId.toString());
        }
    }
}
