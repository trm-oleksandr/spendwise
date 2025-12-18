package sk.upjs.ics.spendwise.factory;

import sk.upjs.ics.spendwise.service.AccountService;
import sk.upjs.ics.spendwise.service.BudgetService;
import sk.upjs.ics.spendwise.service.CategoryService;
import sk.upjs.ics.spendwise.service.TransactionService;

public enum DefaultServiceFactory implements ServiceFactory {
    INSTANCE;

    private final DaoFactory daoFactory = JdbcDaoFactory.getInstance();

    private AccountService accountService;
    private CategoryService categoryService;
    private TransactionService transactionService;
    private BudgetService budgetService;

    @Override
    public AccountService accountService() {
        if (accountService == null) {
            accountService = new AccountService(daoFactory.accountDao());
        }
        return accountService;
    }

    @Override
    public CategoryService categoryService() {
        if (categoryService == null) {
            categoryService = new CategoryService(daoFactory.categoryDao());
        }
        return categoryService;
    }

    @Override
    public TransactionService transactionService() {
        if (transactionService == null) {
            transactionService = new TransactionService(daoFactory.transactionDao());
        }
        return transactionService;
    }

    @Override
    public BudgetService budgetService() {
        if (budgetService == null) {
            budgetService = new BudgetService(daoFactory.budgetDao(), transactionService());
        }
        return budgetService;
    }
}
