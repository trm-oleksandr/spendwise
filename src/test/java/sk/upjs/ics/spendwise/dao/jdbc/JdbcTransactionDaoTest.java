package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcTransactionDaoTest extends DaoIntegrationTestBase {

    @Test
    void savesAndLoadsTransactionWithJoins() {
        Long userId = insertUser("txn-user");
        Account account = createAccount(userId, "Checking");
        Category category = createCategory(userId, "Dining", CategoryType.EXPENSE);
        JdbcTransactionDao dao = new JdbcTransactionDao(jdbcTemplate);

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAccountId(account.getId());
        transaction.setCategoryId(category.getId());
        transaction.setAmount(new BigDecimal("25.50"));
        transaction.setOccurredAt(Instant.now());
        transaction.setNote("Dinner");

        Transaction saved = dao.save(transaction);
        assertNotNull(saved.getId());

        Transaction loaded = dao.getById(saved.getId(), userId).orElseThrow();
        assertEquals(account.getName(), loaded.getAccountName());
        assertEquals(category.getName(), loaded.getCategoryName());
        assertEquals(CategoryType.EXPENSE, loaded.getType());

        List<Transaction> all = dao.getAll(userId);
        assertEquals(1, all.size());
        assertEquals(saved.getId(), all.getFirst().getId());
    }

    @Test
    void sumsOnlyExpenseTransactionsInRange() {
        Long userId = insertUser("sum-user");
        Account account = createAccount(userId, "Card");
        Category expense = createCategory(userId, "Groceries", CategoryType.EXPENSE);
        Category income = createCategory(userId, "Salary", CategoryType.INCOME);
        JdbcTransactionDao dao = new JdbcTransactionDao(jdbcTemplate);

        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant lastWeek = now.minus(7, ChronoUnit.DAYS);
        Instant nextWeek = now.plus(7, ChronoUnit.DAYS);

        Transaction expenseTxn = new Transaction();
        expenseTxn.setUserId(userId);
        expenseTxn.setAccountId(account.getId());
        expenseTxn.setCategoryId(expense.getId());
        expenseTxn.setAmount(new BigDecimal("40.00"));
        expenseTxn.setOccurredAt(now);
        dao.save(expenseTxn);

        Transaction outsideRange = new Transaction();
        outsideRange.setUserId(userId);
        outsideRange.setAccountId(account.getId());
        outsideRange.setCategoryId(expense.getId());
        outsideRange.setAmount(new BigDecimal("10.00"));
        outsideRange.setOccurredAt(lastWeek.minus(1, ChronoUnit.DAYS));
        dao.save(outsideRange);

        Transaction incomeTxn = new Transaction();
        incomeTxn.setUserId(userId);
        incomeTxn.setAccountId(account.getId());
        incomeTxn.setCategoryId(income.getId());
        incomeTxn.setAmount(new BigDecimal("999.00"));
        incomeTxn.setOccurredAt(now);
        dao.save(incomeTxn);

        BigDecimal sum = dao.sumExpensesForAccountBetween(userId, account.getId(), lastWeek, nextWeek);
        assertEquals(new BigDecimal("40.00"), sum);
    }
}
