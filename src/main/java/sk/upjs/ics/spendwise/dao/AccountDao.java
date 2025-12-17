package sk.upjs.ics.spendwise.dao;

import java.util.List;
import java.util.Optional;
import sk.upjs.ics.spendwise.entity.Account;

public interface AccountDao {
    List<Account> findAll(long userId);

    Optional<Account> findById(long userId, long id);

    Account create(Account account);

    Account update(Account account);

    void delete(long userId, long id);
}
