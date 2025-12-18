package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import com.zaxxer.hikari.HikariDataSource;
import sk.upjs.ics.spendwise.entity.Account;

import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AccountDaoTest {

    // Подключение к базе (как в CategoryDaoTest)
    private JdbcTemplate getJdbcTemplate() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/spendwise");
        ds.setUsername("spendwise");
        ds.setPassword("spendwise");
        return new JdbcTemplate(ds);
    }

    @Test
    void testCreateAndRead() {
        JdbcAccountDao dao = new JdbcAccountDao(getJdbcTemplate());

        // 1. Создаем тестовый счет
        Account a = new Account();
        a.setUserId(1L); // Предполагаем, что юзер ID=1 есть
        a.setName("TEST_ACC_" + System.currentTimeMillis());
        a.setCurrency("EUR");
        a.setCreatedAt(Instant.now());

        // Сохраняем
        Account saved = dao.save(a);

        // Проверяем, что ID создался
        assertNotNull(saved.getId(), "Saved account should have an ID");

        // 2. Читаем список
        List<Account> all = dao.getAll(1L);
        boolean found = all.stream().anyMatch(acc -> acc.getId().equals(saved.getId()));

        assertTrue(found, "Account should be found in database");

        // 3. Удаляем (чистим мусор)
        dao.delete(saved.getId());
    }
}