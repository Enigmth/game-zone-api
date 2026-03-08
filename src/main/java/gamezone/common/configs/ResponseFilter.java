package gamezone.common.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResponseFilter implements ContainerResponseFilter {
    @Context
    HttpServletRequest servletRequest;
    @Context
    HttpServletResponse servletResponse;

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext responseContext) {
        try {
            servletResponse.setCharacterEncoding("UTF-8");
            MultivaluedMap<String, Object> headersResponse = responseContext.getHeaders();
            headersResponse.add("Access-Control-Allow-Origin", "*");
            headersResponse.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            headersResponse.add("Access-Control-Allow-Headers", " Content-Type,Authorization,Accept,Origin,token,token_source");
            headersResponse.add("Access-Control-Allow-Credentials", "true");
        } catch (Exception e) {
            System.out.println("Failed to process response filter" + e.getMessage());
        }
    }
}
