package sk.upjs.ics.spendwise.dao;

import sk.upjs.ics.spendwise.entity.Account;
import java.util.List;
import java.util.Optional;

public interface AccountDao {
    List<Account> getAll(Long userId);
    Optional<Account> getById(Long id);
    Account save(Account account);
    boolean delete(Long id);
}