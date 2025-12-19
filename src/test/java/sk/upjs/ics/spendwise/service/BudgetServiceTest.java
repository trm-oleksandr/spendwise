package sk.upjs.ics.spendwise.service;

import org.junit.jupiter.api.Test;
import sk.upjs.ics.spendwise.dao.BudgetDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Budget;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BudgetServiceTest {

    @Test
    void enrichesBudgetWithSpentAndRemainingAmounts() {
        FakeTransactionDao transactionDao = new FakeTransactionDao();
        transactionDao.setNextAmount(new BigDecimal("40.50"));
        TransactionService transactionService = new TransactionService(transactionDao);
        FakeBudgetDao budgetDao = new FakeBudgetDao();

        Budget budget = new Budget();
        budget.setUserId(1L);
        budget.setAccountId(99L);
        budget.setLimitAmount(new BigDecimal("150.00"));
        budget.setStartDate(LocalDate.of(2024, 1, 1));
        budget.setEndDate(LocalDate.of(2024, 1, 31));
        budgetDao.save(budget);

        BudgetService budgetService = new BudgetService(budgetDao, transactionService);
        List<Budget> budgets = budgetService.getAll(1L);

        assertEquals(1, budgets.size());
        Budget enriched = budgets.getFirst();
        assertEquals(new BigDecimal("40.50"), enriched.getSpentAmount());
        assertEquals(new BigDecimal("109.50"), enriched.getRemainingAmount());

        Instant expectedFrom = budget.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant expectedTo = budget.getEndDate().plusDays(1).atStartOfDay().minusSeconds(1).toInstant(ZoneOffset.UTC);
        assertEquals(expectedFrom, transactionDao.getLastFrom());
        assertEquals(expectedTo, transactionDao.getLastTo());
        assertEquals(1L, transactionDao.getLastUserId());
        assertEquals(99L, transactionDao.getLastAccountId());
    }

    private static class FakeBudgetDao implements BudgetDao {
        private final Map<Long, Budget> store = new LinkedHashMap<>();
        private long sequence = 1L;

        @Override
        public List<Budget> getAll(Long userId) {
            return store.values().stream()
                    .filter(budget -> budget.getUserId().equals(userId))
                    .toList();
        }

        @Override
        public Optional<Budget> getById(Long id, Long userId) {
            return Optional.ofNullable(store.get(id))
                    .filter(budget -> budget.getUserId().equals(userId));
        }

        @Override
        public Budget save(Budget budget) {
            if (budget.getId() == null) {
                budget.setId(sequence++);
            }
            store.put(budget.getId(), budget);
            return budget;
        }

        @Override
        public boolean delete(Long id, Long userId) {
            return store.remove(id) != null;
        }
    }

    private static class FakeTransactionDao implements TransactionDao {
        private BigDecimal nextAmount = BigDecimal.ZERO;
        private Long lastUserId;
        private Long lastAccountId;
        private Instant lastFrom;
        private Instant lastTo;

        @Override
        public List<sk.upjs.ics.spendwise.entity.Transaction> getAll(Long userId) {
            throw new UnsupportedOperationException("Not needed in test");
        }

        @Override
        public Optional<sk.upjs.ics.spendwise.entity.Transaction> getById(Long id, Long userId) {
            throw new UnsupportedOperationException("Not needed in test");
        }

        @Override
        public sk.upjs.ics.spendwise.entity.Transaction save(sk.upjs.ics.spendwise.entity.Transaction transaction) {
            throw new UnsupportedOperationException("Not needed in test");
        }

        @Override
        public boolean delete(Long id, Long userId) {
            throw new UnsupportedOperationException("Not needed in test");
        }

        @Override
        public BigDecimal sumExpensesForAccountBetween(Long userId, Long accountId, Instant fromInclusive, Instant toInclusive) {
            this.lastUserId = userId;
            this.lastAccountId = accountId;
            this.lastFrom = fromInclusive;
            this.lastTo = toInclusive;
            return nextAmount;
        }

        void setNextAmount(BigDecimal nextAmount) {
            this.nextAmount = nextAmount;
        }

        Long getLastUserId() {
            return lastUserId;
        }

        Long getLastAccountId() {
            return lastAccountId;
        }

        Instant getLastFrom() {
            return lastFrom;
        }

        Instant getLastTo() {
            return lastTo;
        }
    }
}
