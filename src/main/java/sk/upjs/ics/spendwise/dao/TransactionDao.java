package sk.upjs.ics.spendwise.dao;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;

public interface TransactionDao {
    List<Transaction> findByMonth(long userId, YearMonth month, Long accountId, Long categoryId, CategoryType type);

    Optional<Transaction> findById(long userId, long id);

    Transaction create(Transaction t);

    Transaction update(Transaction t);

    void delete(long userId, long id);
}
