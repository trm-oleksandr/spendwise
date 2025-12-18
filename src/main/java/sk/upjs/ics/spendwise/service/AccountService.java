package sk.upjs.ics.spendwise.service;

import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import java.util.List;

public class AccountService {

    private final AccountDao accountDao = JdbcDaoFactory.INSTANCE.accountDao();

    public List<Account> getAll(Long userId) {
        return accountDao.getAll(userId);
    }

    public void save(Account account) {
        accountDao.save(account);
    }

    public void delete(Long id, Long userId) {
        accountDao.delete(id, userId);
    }
}