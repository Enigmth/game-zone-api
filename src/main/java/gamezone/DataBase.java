package gamezone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public final class DataBase {
    private static final String ENV_DB_URL = "GAMEZONE_DB_URL";
    private static final String ENV_DB_HOST = "GAMEZONE_DB_HOST";
    private static final String ENV_DB_PORT = "GAMEZONE_DB_PORT";
    private static final String ENV_DB_NAME = "GAMEZONE_DB_NAME";
    private static final String ENV_DB_USER = "GAMEZONE_DB_USER";
    private static final String ENV_DB_PASSWORD = "GAMEZONE_DB_PASSWORD";
    private static final String ENV_DB_SSL_MODE = "GAMEZONE_DB_SSL_MODE";
    private static final String ENV_DB_PARAMS = "GAMEZONE_DB_PARAMS";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "gamezone";
    private static final String DEFAULT_USER = "avnadmin";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String DEFAULT_PARAMS = "sslMode=REQUIRED&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true";
    private static final Map<String, String> DOT_ENV = loadDotEnv();

    static {
        try {
            Class.forName(MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL driver not found: " + MYSQL_DRIVER, e);
        }
    }

    private DataBase() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv(ENV_DB_URL);
        String user = read(ENV_DB_USER, DEFAULT_USER);
        String password = read(ENV_DB_PASSWORD, DEFAULT_PASSWORD);
        if (url != null && !url.isBlank()) {
            return DriverManager.getConnection(normalizeJdbcUrl(url), user, password);
        }

        String host = read(ENV_DB_HOST, DEFAULT_HOST);
        String port = read(ENV_DB_PORT, DEFAULT_PORT);
        String db = read(ENV_DB_NAME, DEFAULT_DB);
        String params = normalizeJdbcParams(readSetting(ENV_DB_PARAMS, ENV_DB_PARAMS, DEFAULT_PARAMS));
        String jdbcUrl = buildJdbcUrl(host, port, db, params);
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    private static String normalizeJdbcParams(String params) {
        String normalized = (params == null) ? "" : params.trim();
        normalized = appendParamIfMissing(normalized, "connectionTimeZone=UTC", "connectionTimeZone=");
        normalized = appendParamIfMissing(normalized, "forceConnectionTimeZoneToSession=true", "forceConnectionTimeZoneToSession=");
        return normalized;
    }

    private static String appendParamIfMissing(String params, String valueToAppend, String keyPrefix) {
        if (params.contains(keyPrefix)) {
            return params;
        }
        if (params.isEmpty()) {
            return valueToAppend;
        }
        return params + "&" + valueToAppend;
    }

    private static String readSetting(String envKey, String propKey, String defaultValue) {
        String propValue = System.getProperty(propKey);
        if (propValue != null && !propValue.isBlank()) {
            return propValue.trim();
        }
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }
        return defaultValue;
    }

    public static String getJdbcUrlForFlyway() {
//        String url = System.getenv(ENV_DB_URL);
//        if (url != null && !url.isBlank()) {
//            return normalizeJdbcUrl(url);
//        }
//        String host = read(ENV_DB_HOST, DEFAULT_HOST);
//        String port = read(ENV_DB_PORT, DEFAULT_PORT);
//        String db = read(ENV_DB_NAME, DEFAULT_DB);
//        return buildJdbcUrl(host, port, db);
        return null;
    }

    public static String getUserForFlyway() {
        return read(ENV_DB_USER, DEFAULT_USER);
    }

    public static String getPasswordForFlyway() {
        return read(ENV_DB_PASSWORD, DEFAULT_PASSWORD);
    }

    private static String read(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = DOT_ENV.get(key);
        }
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private static String buildJdbcUrl(String host, String port, String databaseName, String params) {
        StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(host).append(":").append(port).append("/").append(databaseName);
        if (params != null && !params.isBlank()) {
            if (params.charAt(0) != '?') {
                url.append('?');
            }
            url.append(params);
        }
        return url.toString();
    }

    private static String normalizeJdbcUrl(String rawUrl) {
        String url = rawUrl.trim()
                .replace("\\=", "=")
                .replace("\\&", "&")
                .replace("&amp;", "&")
                .replace("ssl-mode=", "sslMode=");

        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }
        return url;
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();
        Path envPath = Path.of(".env");
        if (!Files.exists(envPath)) {
            return values;
        }

        try {
            for (String rawLine : Files.readAllLines(envPath)) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }

                int firstEquals = line.indexOf('=');
                String key = line.substring(0, firstEquals).trim();
                String value = line.substring(firstEquals + 1).trim();
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                if (!key.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (IOException ignored) {
            // Ignore .env parsing failures and fallback to environment variables/defaults.
        }

        return values;
    }
}
