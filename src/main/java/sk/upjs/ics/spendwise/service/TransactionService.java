package sk.upjs.ics.spendwise.service;

import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class TransactionService {

    private final TransactionDao transactionDao;

    public TransactionService() {
        this(JdbcDaoFactory.INSTANCE.transactionDao());
    }

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<Transaction> getAll(Long userId) {
        return transactionDao.getAll(userId);
    }

    public void save(Transaction transaction) {
        transactionDao.save(transaction);
    }

    public void delete(Long id, Long userId) {
        transactionDao.delete(id, userId);
    }

    public BigDecimal getExpenseSumForAccountBetween(Long userId, Long accountId, Instant fromInclusive, Instant toInclusive) {
        return transactionDao.sumExpensesForAccountBetween(userId, accountId, fromInclusive, toInclusive);
    }
}