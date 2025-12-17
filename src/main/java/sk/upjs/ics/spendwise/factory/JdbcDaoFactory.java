package sk.upjs.ics.spendwise.factory;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.ics.spendwise.config.AppConfig;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.dao.UserDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcAccountDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcCategoryDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcTransactionDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcUserDao;

public class JdbcDaoFactory implements DaoFactory {
    private static JdbcDaoFactory instance;
    private final JdbcTemplate jdbcTemplate;
    private JdbcUserDao userDao;
    private JdbcAccountDao accountDao;
    private JdbcCategoryDao categoryDao;
    private JdbcTransactionDao transactionDao;

    private JdbcDaoFactory() {
        jdbcTemplate = AppConfig.getInstance().getJdbcTemplate();
    }

    public static synchronized JdbcDaoFactory getInstance() {
        if (instance == null) {
            instance = new JdbcDaoFactory();
        }
        return instance;
    }

    public synchronized JdbcUserDao getUserDao() {
        if (userDao == null) {
            userDao = new JdbcUserDao(jdbcTemplate);
        }
        return userDao;
    }

    public synchronized JdbcAccountDao getAccountDao() {
        if (accountDao == null) {
            accountDao = new JdbcAccountDao(jdbcTemplate);
        }
        return accountDao;
    }

    public synchronized JdbcCategoryDao getCategoryDao() {
        if (categoryDao == null) {
            categoryDao = new JdbcCategoryDao(jdbcTemplate);
        }
        return categoryDao;
    }

    public synchronized JdbcTransactionDao getTransactionDao() {
        if (transactionDao == null) {
            transactionDao = new JdbcTransactionDao(jdbcTemplate);
        }
        return transactionDao;
    }

    @Override
    public UserDao userDao() {
        return getUserDao();
    }

    @Override
    public AccountDao accountDao() {
        return getAccountDao();
    }

    @Override
    public CategoryDao categoryDao() {
        return getCategoryDao();
    }

    @Override
    public TransactionDao transactionDao() {
        return getTransactionDao();
    }
}
