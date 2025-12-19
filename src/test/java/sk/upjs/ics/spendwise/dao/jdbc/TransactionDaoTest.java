package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import com.zaxxer.hikari.HikariDataSource;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDaoTest {

    private JdbcTemplate getJdbcTemplate() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/spendwise");
        ds.setUsername("spendwise");
        ds.setPassword("spendwise");
        return new JdbcTemplate(ds);
    }

    @Test
    void testCreateAndReadTransaction() {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        // DAO
        JdbcAccountDao accountDao = new JdbcAccountDao(jdbcTemplate);
        JdbcCategoryDao categoryDao = new JdbcCategoryDao(jdbcTemplate);
        JdbcTransactionDao transactionDao = new JdbcTransactionDao(jdbcTemplate);

        // 1. ПОДГОТОВКА (Создаем владельца - Account и Category)
        Account acc = new Account();
        acc.setUserId(1L);
        acc.setName("TestAcc_Txn_" + System.currentTimeMillis());
        acc.setCurrency("EUR");
        acc.setCreatedAt(Instant.now());
        Account savedAccount = accountDao.save(acc);

        Category cat = new Category();
        cat.setUserId(1L);
        cat.setName("TestCat_Txn_" + System.currentTimeMillis());
        cat.setType(CategoryType.EXPENSE);
        Category savedCategory = categoryDao.save(cat);

        // 2. ТЕСТИРУЕМ ТРАНЗАКЦИЮ
        Transaction t = new Transaction();
        t.setUserId(1L);
        t.setAccountId(savedAccount.getId());
        t.setCategoryId(savedCategory.getId());
        t.setAmount(new BigDecimal("15.50"));
        t.setOccurredAt(Instant.now());
        t.setNote("JUnit Test Transaction");

        Transaction savedTxn = transactionDao.save(t);
        assertNotNull(savedTxn.getId(), "Transaction ID should not be null");

        // 3. ПРОВЕРЯЕМ (READ)
        List<Transaction> all = transactionDao.getAll(1L);
        Optional<Transaction> found = all.stream().filter(txn -> txn.getId().equals(savedTxn.getId())).findFirst();

        assertTrue(found.isPresent(), "Transaction should be found in database");

        // Проверяем JOIN (подтянулись ли имена)
        Transaction fetched = found.get();
        assertNotNull(fetched.getAccountName(), "JOIN should fetch Account Name");
        assertNotNull(fetched.getCategoryName(), "JOIN should fetch Category Name");

        // 4. УДАЛЯЕМ (Cleanup)
        // ИСПРАВЛЕНИЕ: Передаем ID пользователя (1L) вторым аргументом
        transactionDao.delete(savedTxn.getId(), 1L);

        categoryDao.delete(savedCategory.getId(), 1L);
        accountDao.delete(savedAccount.getId(), 1L);
    }
}