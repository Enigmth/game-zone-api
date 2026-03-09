package gamezone.common;

import gamezone.common.configs.CORSFilter;
import gamezone.common.configs.JwtAuthFilter;
import gamezone.common.configs.RestExceptionMapper;
import gamezone.common.configs.TraceIdFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/v1")
public class GamezoneApplication extends ResourceConfig {
    public GamezoneApplication() {
//        FlywayMigrator.migrate();
        packages("gamezone.resources", "gamezone.common.configs");
        register(JacksonFeature.class);
        register(RestExceptionMapper.class);
        register(CORSFilter.class);
        register(TraceIdFilter.class);
        register(JwtAuthFilter.class);
    }
}
