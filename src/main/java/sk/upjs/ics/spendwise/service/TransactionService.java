package sk.upjs.ics.spendwise.service;

import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import java.util.List;

public class TransactionService {

    private final TransactionDao transactionDao = JdbcDaoFactory.INSTANCE.transactionDao();

    public List<Transaction> getAll(Long userId) {
        return transactionDao.getAll(userId);
    }

    public void save(Transaction transaction) {
        transactionDao.save(transaction);
    }

    public void delete(Long id) {
        transactionDao.delete(id);
    }
}