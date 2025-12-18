package sk.upjs.ics.spendwise.factory;

import sk.upjs.ics.spendwise.service.AccountService;
import sk.upjs.ics.spendwise.service.BudgetService;
import sk.upjs.ics.spendwise.service.CategoryService;
import sk.upjs.ics.spendwise.service.TransactionService;

public interface ServiceFactory {

    AccountService accountService();

    CategoryService categoryService();

    TransactionService transactionService();

    BudgetService budgetService();
}
