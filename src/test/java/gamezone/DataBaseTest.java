package gamezone;

import gamezone.domain.ContextHeaders;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.flywaydb.core.internal.database.base.Database;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataBaseTest extends BaseTest {

    @Test
    void containerRequestContextContainsAuthenticatedUserFromBaseTest() {
        ContainerRequestContext requestContext = createContainerRequestContext("user-123");
        assertEquals("user-123", requestContext.getProperty(ContextHeaders.AUTH_USER_ID));
        assertEquals("user-123", requestContext.getHeaderString(ContextHeaders.USER_ID_HEADER));
    }

    @Test
    void jdbcUrlBuilderIncludesRequestedSslMode() throws Exception {
        Field dotEnvField = DataBase.class.getDeclaredField("DOT_ENV");
        dotEnvField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, String> dotEnv = (Map<String, String>) dotEnvField.get(null);

        String previousSslMode = dotEnv.put("GAMEZONE_DB_SSL_MODE", "REQUIRED");
        try {
            Method buildJdbcUrl = DataBase.class.getDeclaredMethod("buildJdbcUrl", String.class, String.class, String.class);
            buildJdbcUrl.setAccessible(true);
            String jdbcUrl = (String) buildJdbcUrl.invoke(null, "db-host", "22680", "defaultdb");

            assertTrue(jdbcUrl.startsWith("jdbc:mysql://db-host:22680/defaultdb"));
            assertTrue(jdbcUrl.contains("sslMode=REQUIRED"));
            assertTrue(jdbcUrl.contains("serverTimezone=UTC"));
        } finally {
            restore(dotEnv, "GAMEZONE_DB_SSL_MODE", previousSslMode);
        }
    }

    @Test
    void loadDotEnvParsesKeyValuePairsAndQuotedValues() throws Exception {
        Path tempDir = Files.createTempDirectory("gamezone-db-test-");
        Files.writeString(tempDir.resolve(".env"), """
                A=1
                QUOTED=\"value\"
                # comment
                INVALID_LINE
                """);

        String previousUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            Method loadDotEnv = DataBase.class.getDeclaredMethod("loadDotEnv");
            loadDotEnv.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> values = (Map<String, String>) loadDotEnv.invoke(null);

            assertEquals("1", values.get("A"));
            assertEquals("value", values.get("QUOTED"));
            assertNotNull(values);
        } finally {
            System.setProperty("user.dir", previousUserDir);
            Files.deleteIfExists(tempDir.resolve(".env"));
            Files.deleteIfExists(tempDir);
        }
    }

    private void restore(Map<String, String> map, String key, String previousValue) {
        if (previousValue == null) {
            map.remove(key);
            return;
        }
        map.put(key, previousValue);
    }

    @Test
    void getConnection() throws SQLException {
        DataBase.getConnection();
    }
}
