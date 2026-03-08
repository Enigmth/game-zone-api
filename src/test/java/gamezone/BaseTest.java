package gamezone;

import gamezone.domain.ContextHeaders;
import gamezone.domain.dto.users.UserProfileResponse;
import gamezone.services.UserService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseTest {

    protected ContainerRequestContext createContainerRequestContext() {
        return createContainerRequestContext("me");
    }

    protected ContainerRequestContext createContainerRequestContext(String userId) {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(ContextHeaders.TOKEN_SOURCE, "APPLE");
        headers.add(ContextHeaders.USER_ID_HEADER, userId);

        UserProfileResponse user = new UserService().getMe(userId);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("user", user);
        properties.put(ContextHeaders.AUTH_USER_ID, userId);

        return new ContainerRequestContext() {
            @Override
            public Object getProperty(String name) {
                return properties.get(name);
            }

            @Override
            public Collection<String> getPropertyNames() {
                return properties.keySet();
            }

            @Override
            public void setProperty(String name, Object object) {
                properties.put(name, object);
            }

            @Override
            public void removeProperty(String name) {
                properties.remove(name);
            }

            @Override
            public UriInfo getUriInfo() {
                return null;
            }

            @Override
            public void setRequestUri(URI requestUri) {
            }

            @Override
            public void setRequestUri(URI baseUri, URI requestUri) {
            }

            @Override
            public Request getRequest() {
                return null;
            }

            @Override
            public String getMethod() {
                return "GET";
            }

            @Override
            public void setMethod(String method) {
            }

            @Override
            public MultivaluedMap<String, String> getHeaders() {
                return headers;
            }

            @Override
            public String getHeaderString(String name) {
                List<String> values = headers.get(name);
                if (values == null || values.isEmpty()) {
                    return null;
                }
                return values.getFirst();
            }

            @Override
            public Date getDate() {
                return null;
            }

            @Override
            public Locale getLanguage() {
                return null;
            }

            @Override
            public int getLength() {
                return 0;
            }

            @Override
            public MediaType getMediaType() {
                return null;
            }

            @Override
            public List<MediaType> getAcceptableMediaTypes() {
                return List.of();
            }

            @Override
            public List<Locale> getAcceptableLanguages() {
                return List.of();
            }

            @Override
            public Map<String, Cookie> getCookies() {
                return Map.of();
            }

            @Override
            public boolean hasEntity() {
                return false;
            }

            @Override
            public InputStream getEntityStream() {
                return null;
            }

            @Override
            public void setEntityStream(InputStream input) {
            }

            @Override
            public SecurityContext getSecurityContext() {
                return null;
            }

            @Override
            public void setSecurityContext(SecurityContext context) {
            }

            @Override
            public void abortWith(Response response) {
            }
        };
    }
}
