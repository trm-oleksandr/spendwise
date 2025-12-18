package sk.upjs.ics.spendwise.factory;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.ics.spendwise.config.AppConfig;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.dao.BudgetDao;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.dao.UserDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcAccountDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcBudgetDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcCategoryDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcTransactionDao;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcUserDao;

public enum JdbcDaoFactory implements DaoFactory {
    INSTANCE;

    private final JdbcTemplate jdbcTemplate;

    // Кешируем DAO
    private JdbcUserDao userDao;
    private JdbcAccountDao accountDao;
    private JdbcCategoryDao categoryDao;
    private JdbcTransactionDao transactionDao;
    private JdbcBudgetDao budgetDao;

    JdbcDaoFactory() {
        this.jdbcTemplate = AppConfig.getInstance().getJdbcTemplate();
    }

    // LoginController
    public static JdbcDaoFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public UserDao userDao() {
        if (userDao == null) {
            userDao = new JdbcUserDao(jdbcTemplate);
        }
        return userDao;
    }

    @Override
    public AccountDao accountDao() {
        if (accountDao == null) {
            accountDao = new JdbcAccountDao(jdbcTemplate);
        }
        return accountDao;
    }

    @Override
    public CategoryDao categoryDao() {
        if (categoryDao == null) {
            categoryDao = new JdbcCategoryDao(jdbcTemplate);
        }
        return categoryDao;
    }

    @Override
    public TransactionDao transactionDao() {
        if (transactionDao == null) {
            transactionDao = new JdbcTransactionDao(jdbcTemplate);
        }
        return transactionDao;
    }

    @Override
    public BudgetDao budgetDao() {
        if (budgetDao == null) {
            budgetDao = new JdbcBudgetDao(jdbcTemplate);
        }
        return budgetDao;
    }
}