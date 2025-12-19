package sk.upjs.ics.spendwise.dao.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;

@Testcontainers
abstract class DaoIntegrationTestBase {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("spendwise")
            .withUsername("spendwise")
            .withPassword("spendwise")
            .withInitScript("test-init.sql");

    protected static JdbcTemplate jdbcTemplate;
    private static HikariDataSource dataSource;

    @BeforeAll
    static void setUpDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(POSTGRES.getJdbcUrl());
        config.setUsername(POSTGRES.getUsername());
        config.setPassword(POSTGRES.getPassword());
        dataSource = new HikariDataSource(config);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AfterAll
    static void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.update("TRUNCATE budget, txn, category, account, app_user RESTART IDENTITY CASCADE");
    }

    protected Long insertUser(String username) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO app_user (username, password_hash) VALUES (?, ?) RETURNING id",
                Long.class,
                username,
                "hash"
        );
    }

    protected Account createAccount(Long userId, String name) {
        JdbcAccountDao dao = new JdbcAccountDao(jdbcTemplate);
        Account account = new Account();
        account.setUserId(userId);
        account.setName(name);
        account.setCurrency("EUR");
        return dao.save(account);
    }

    protected Category createCategory(Long userId, String name, CategoryType type) {
        JdbcCategoryDao dao = new JdbcCategoryDao(jdbcTemplate);
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        return dao.save(category);
    }
}
