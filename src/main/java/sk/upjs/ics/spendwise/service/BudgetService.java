package sk.upjs.ics.spendwise.service;

import sk.upjs.ics.spendwise.dao.BudgetDao;
import sk.upjs.ics.spendwise.entity.Budget;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class BudgetService {

    private final BudgetDao budgetDao = JdbcDaoFactory.INSTANCE.budgetDao();
    private final TransactionService transactionService = new TransactionService();

    public List<Budget> getAll(Long userId) {
        List<Budget> budgets = budgetDao.getAll(userId);
        budgets.forEach(budget -> enrichWithStats(budget, userId));
        return budgets;
    }

    public void save(Budget budget) {
        budgetDao.save(budget);
    }

    public void delete(Long id, Long userId) {
        budgetDao.delete(id, userId);
    }

    public Optional<Budget> getById(Long id, Long userId) {
        Optional<Budget> budget = budgetDao.getById(id, userId);
        budget.ifPresent(b -> enrichWithStats(b, userId));
        return budget;
    }

    private void enrichWithStats(Budget budget, Long userId) {
        Instant from = budget.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = budget.getEndDate().plusDays(1).atStartOfDay().minusSeconds(1).toInstant(ZoneOffset.UTC);
        BigDecimal spent = transactionService.getExpenseSumForAccountBetween(userId, budget.getAccountId(), from, to);
        budget.setSpentAmount(spent);
        if (budget.getLimitAmount() != null) {
            budget.setRemainingAmount(budget.getLimitAmount().subtract(spent));
        }
    }
}
