package sk.upjs.ics.spendwise.factory;

import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.dao.UserDao;

public interface DaoFactory {
    AccountDao accountDao();

    CategoryDao categoryDao();

    TransactionDao transactionDao();

    UserDao userDao();
}
