package sk.upjs.ics.spendwise.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final String PROPERTIES_FILE = "db.properties";
    private static volatile AppConfig instance;

    private final HikariDataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    private AppConfig() {
        Properties properties = loadProperties();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getProperty("url"));
        hikariConfig.setUsername(properties.getProperty("user"));
        hikariConfig.setPassword(properties.getProperty("password"));

        this.dataSource = new HikariDataSource(hikariConfig);
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        logger.info("HikariDataSource and JdbcTemplate initialized successfully");
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void close() {
        if (dataSource != null) {
            logger.info("Closing HikariDataSource");
            dataSource.close();
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException(PROPERTIES_FILE + " not found in classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Failed to load {}", PROPERTIES_FILE, e);
            throw new IllegalStateException("Could not load " + PROPERTIES_FILE, e);
        }

        validateRequiredProperties(properties);
        return properties;
    }

    private void validateRequiredProperties(Properties properties) {
        Objects.requireNonNull(properties.getProperty("url"), "Database URL (url) must be provided");
        Objects.requireNonNull(properties.getProperty("user"), "Database user (user) must be provided");
        Objects.requireNonNull(properties.getProperty("password"), "Database password (password) must be provided");
    }
}
