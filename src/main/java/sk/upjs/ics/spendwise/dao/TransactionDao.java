package sk.upjs.ics.spendwise.dao;

import sk.upjs.ics.spendwise.entity.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionDao {
    List<Transaction> getAll(Long userId);
    Optional<Transaction> getById(Long id);
    Transaction save(Transaction transaction);
    boolean delete(Long id);
}