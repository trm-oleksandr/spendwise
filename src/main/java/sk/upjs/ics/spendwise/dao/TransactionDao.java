package sk.upjs.ics.spendwise.dao;

import sk.upjs.ics.spendwise.entity.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionDao {
    List<Transaction> getAll(Long userId);
    Optional<Transaction> getById(Long id, Long userId);
    Transaction save(Transaction transaction);
    boolean delete(Long id, Long userId);
    BigDecimal sumExpensesForAccountBetween(Long userId, Long accountId, Instant fromInclusive, Instant toInclusive);
}