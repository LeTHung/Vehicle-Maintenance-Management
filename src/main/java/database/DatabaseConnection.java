package database;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String CONFIG_PATH = "config/database.properties";
    private static final DatabaseConfig CONFIG = DatabaseConfig.load();

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.url(),
                CONFIG.user(),
                CONFIG.password());
    }

    public static String getUrl() {
        return CONFIG.url();
    }

    public static String getUser() {
        return CONFIG.user();
    }

    public static String getPassword() {
        return CONFIG.password();
    }

    public static String getConfigMode() {
        return CONFIG.mode();
    }

    public static String getConfigSource() {
        return CONFIG.source();
    }

    public static String getConfigPath() {
        return Path.of(CONFIG_PATH).toAbsolutePath().normalize().toString();
    }

    private record DatabaseConfig(
            String mode,
            String url,
            String user,
            String password,
            String source) {

        private static final String DEFAULT_MYSQL_PORT = "3306";
        private static final String DEFAULT_JDBC_PARAMS = "useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true";

        private static DatabaseConfig load() {
            Properties properties = loadProperties();

            String mode = normalizeMode(readValue("DB_MODE", properties, "DB_MODE", "auto"));
            if ("auto".equals(mode)) {
                mode = hasRailwayConfig(properties) ? "railway" : "local";
            }

            if ("local".equals(mode)) {
                return loadLocal(properties, mode);
            }

            if ("railway".equals(mode)) {
                return loadRailway(properties, mode);
            }

            throw new IllegalStateException("Invalid DB_MODE: " + mode + ". Use local, railway, or auto.");
        }

        private static DatabaseConfig loadLocal(Properties properties, String mode) {
            String url = readValue("DB_URL", properties, "DB_URL", "");
            String user = readValue("DB_USER", properties, "DB_USER", "");
            String password = readValue("DB_PASSWORD", properties, "DB_PASSWORD", "");

            validate(mode, url, user);

            return new DatabaseConfig(
                    mode,
                    url,
                    user,
                    password,
                    "local: DB_URL / DB_USER / DB_PASSWORD (system props, env, then " + CONFIG_PATH + ")");
        }

        private static DatabaseConfig loadRailway(Properties properties, String mode) {
            String mysqlUrl = firstText(
                    readValue("MYSQL_PUBLIC_URL", properties, "MYSQL_PUBLIC_URL", ""),
                    readValue("MYSQL_URL", properties, "MYSQL_URL", ""));
            String url;
            String user;
            String password;

            if (!mysqlUrl.isBlank()) {
                ParsedMysqlUrl parsedMysqlUrl = parseMysqlUrl(mysqlUrl);
                url = parsedMysqlUrl.jdbcUrl();
                user = readValue("MYSQLUSER", properties, "MYSQLUSER", parsedMysqlUrl.user());
                password = readValue("MYSQLPASSWORD", properties, "MYSQLPASSWORD", parsedMysqlUrl.password());
            } else {
                String host = readValue("MYSQLHOST", properties, "MYSQLHOST", "");
                String port = readValue("MYSQLPORT", properties, "MYSQLPORT", DEFAULT_MYSQL_PORT);
                String database = readValue("MYSQLDATABASE", properties, "MYSQLDATABASE", "");

                if (host.isBlank() || database.isBlank()) {
                    throw new IllegalStateException(
                            "Missing Railway database config. Set MYSQL_PUBLIC_URL, MYSQL_URL, or set MYSQLHOST / MYSQLPORT / MYSQLDATABASE / MYSQLUSER / MYSQLPASSWORD in system properties, environment variables, or "
                                    + CONFIG_PATH);
                }

                url = buildJdbcUrl(host, port, database, "");
                user = readValue("MYSQLUSER", properties, "MYSQLUSER", "");
                password = readValue("MYSQLPASSWORD", properties, "MYSQLPASSWORD", "");
            }

            validate(mode, url, user);

            return new DatabaseConfig(
                    mode,
                    url,
                    user,
                    password,
                    "railway: MYSQL_PUBLIC_URL, MYSQL_URL, or MYSQLHOST / MYSQLPORT / MYSQLDATABASE / MYSQLUSER / MYSQLPASSWORD (system props, env, then "
                            + CONFIG_PATH + ")");
        }

        private static Properties loadProperties() {
            Properties properties = new Properties();
            Path path = Path.of(CONFIG_PATH);

            if (!Files.exists(path)) {
                return properties;
            }

            try (InputStream inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot read config file: " + CONFIG_PATH, exception);
            }

            return properties;
        }

        private static String readValue(
                String envName,
                Properties properties,
                String propertyName,
                String defaultValue) {
            String systemValue = System.getProperty(envName);
            if (systemValue != null && !systemValue.isBlank()) {
                return systemValue.trim();
            }

            String envValue = System.getenv(envName);
            if (envValue != null && !envValue.isBlank()) {
                return envValue.trim();
            }

            String propertyValue = properties.getProperty(propertyName);
            if (propertyValue != null && !propertyValue.isBlank()) {
                return propertyValue.trim();
            }

            return defaultValue;
        }

        private static String normalizeMode(String mode) {
            if (mode == null || mode.isBlank()) {
                return "auto";
            }

            return mode.trim().toLowerCase();
        }

        private static boolean hasRailwayConfig(Properties properties) {
            return hasValue("MYSQL_PUBLIC_URL", properties)
                    || hasValue("MYSQL_URL", properties)
                    || hasValue("MYSQLHOST", properties)
                    || hasValue("MYSQLDATABASE", properties)
                    || hasValue("MYSQLUSER", properties)
                    || hasValue("MYSQLPASSWORD", properties);
        }

        private static boolean hasValue(String name, Properties properties) {
            String systemValue = System.getProperty(name);
            if (systemValue != null && !systemValue.isBlank()) {
                return true;
            }

            String envValue = System.getenv(name);
            if (envValue != null && !envValue.isBlank()) {
                return true;
            }

            String propertyValue = properties.getProperty(name);
            return propertyValue != null && !propertyValue.isBlank();
        }

        private static String firstText(String... values) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }

            return "";
        }

        private static ParsedMysqlUrl parseMysqlUrl(String mysqlUrl) {
            if (mysqlUrl.startsWith("jdbc:mysql://")) {
                return new ParsedMysqlUrl(appendDefaultJdbcParams(mysqlUrl), "", "");
            }

            try {
                URI uri = new URI(mysqlUrl);
                if (!"mysql".equalsIgnoreCase(uri.getScheme())) {
                    throw new IllegalStateException("Invalid MYSQL_URL. It must start with mysql:// or jdbc:mysql://");
                }

                String user = "";
                String password = "";
                String rawUserInfo = uri.getRawUserInfo();
                if (rawUserInfo != null && !rawUserInfo.isBlank()) {
                    String[] credentials = rawUserInfo.split(":", 2);
                    user = decode(credentials[0]);
                    if (credentials.length > 1) {
                        password = decode(credentials[1]);
                    }
                }

                String host = uri.getHost();
                String port = uri.getPort() > 0 ? String.valueOf(uri.getPort()) : DEFAULT_MYSQL_PORT;
                String database = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
                String query = uri.getRawQuery() == null ? "" : uri.getRawQuery();

                if (host == null || host.isBlank() || database.isBlank()) {
                    throw new IllegalStateException("Invalid MYSQL_URL. Missing host or database name.");
                }

                return new ParsedMysqlUrl(buildJdbcUrl(host, port, database, query), user, password);
            } catch (URISyntaxException exception) {
                throw new IllegalStateException("Invalid MYSQL_URL.", exception);
            }
        }

        private static String buildJdbcUrl(String host, String port, String database, String query) {
            String baseUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
            if (query == null || query.isBlank()) {
                return baseUrl + "?" + DEFAULT_JDBC_PARAMS;
            }

            return appendDefaultJdbcParams(baseUrl + "?" + query);
        }

        private static String appendDefaultJdbcParams(String jdbcUrl) {
            if (jdbcUrl.contains("useUnicode=")) {
                return jdbcUrl;
            }

            String separator = jdbcUrl.contains("?") ? "&" : "?";
            return jdbcUrl + separator + DEFAULT_JDBC_PARAMS;
        }

        private static String decode(String value) {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }

        private static void validate(String mode, String url, String user) {
            if (url == null || url.isBlank()) {
                if ("railway".equals(mode)) {
                    throw new IllegalStateException(
                            "Missing Railway database URL. Set MYSQL_PUBLIC_URL, MYSQL_URL, or MYSQLHOST / MYSQLPORT / MYSQLDATABASE in system properties, environment variables, or "
                                    + CONFIG_PATH);
                }

                throw new IllegalStateException(
                        "Missing DB_URL. Please set DB_URL in system properties, environment variables, or "
                                + CONFIG_PATH);
            }

            if (user == null || user.isBlank()) {
                if ("railway".equals(mode)) {
                    throw new IllegalStateException(
                            "Missing MYSQLUSER. Please set MYSQLUSER in system properties, environment variables, or "
                                    + CONFIG_PATH);
                }

                throw new IllegalStateException(
                        "Missing DB_USER. Please set DB_USER in system properties, environment variables, or "
                                + CONFIG_PATH);
            }

            if (!url.startsWith("jdbc:mysql://")) {
                throw new IllegalStateException(
                        "Invalid DB_URL. It must start with jdbc:mysql://");
            }

            if (mode == null || mode.isBlank()) {
                throw new IllegalStateException("Missing DB_MODE.");
            }
        }

        private record ParsedMysqlUrl(String jdbcUrl, String user, String password) {
        }
    }
}
