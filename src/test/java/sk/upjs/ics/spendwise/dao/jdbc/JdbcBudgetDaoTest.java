package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcBudgetDaoTest extends DaoIntegrationTestBase {

    @Test
    void savesUpdatesAndDeletesBudgets() {
        Long userId = insertUser("budget-user");
        Account account = createAccount(userId, "Savings");
        JdbcBudgetDao dao = new JdbcBudgetDao(jdbcTemplate);

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setAccountId(account.getId());
        budget.setLimitAmount(new BigDecimal("200.00"));
        budget.setStartDate(LocalDate.now());
        budget.setEndDate(LocalDate.now().plusDays(30));

        Budget saved = dao.save(budget);
        assertNotNull(saved.getId());

        Optional<Budget> loaded = dao.getById(saved.getId(), userId);
        assertTrue(loaded.isPresent());
        assertEquals(account.getName(), loaded.orElseThrow().getAccountName());

        saved.setLimitAmount(new BigDecimal("250.00"));
        dao.save(saved);
        BigDecimal updatedLimit = dao.getById(saved.getId(), userId).orElseThrow().getLimitAmount();
        assertEquals(new BigDecimal("250.00"), updatedLimit);

        assertEquals(1, dao.getAll(userId).size());
        assertTrue(dao.delete(saved.getId(), userId));
        assertTrue(dao.getAll(userId).isEmpty());
    }
}
