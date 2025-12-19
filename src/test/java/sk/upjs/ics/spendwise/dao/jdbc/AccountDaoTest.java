package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.ics.spendwise.entity.Account;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountDaoTest extends DaoIntegrationTestBase {

    @Test
    void savesAndUpdatesAccount() {
        Long userId = insertUser("account-owner");
        JdbcAccountDao dao = new JdbcAccountDao(jdbcTemplate);

        Account account = new Account();
        account.setUserId(userId);
        account.setName("Wallet");
        account.setCurrency("EUR");

        Account saved = dao.save(account);

        assertNotNull(saved.getId());
        assertEquals("Wallet", saved.getName());
        assertNotNull(saved.getCreatedAt());

        saved.setName("Updated wallet");
        dao.save(saved);

        Account updated = dao.getById(saved.getId(), userId).orElseThrow();
        assertEquals("Updated wallet", updated.getName());

        List<Account> accounts = dao.getAll(userId);
        assertEquals(1, accounts.size());
        assertEquals(saved.getId(), accounts.getFirst().getId());
    }

    @Test
    void deletesOnlyForMatchingUser() {
        Long ownerId = insertUser("owner");
        Long otherUserId = insertUser("other");
        JdbcAccountDao dao = new JdbcAccountDao(jdbcTemplate);

        Account account = new Account();
        account.setUserId(ownerId);
        account.setName("Card");
        account.setCurrency("USD");
        Account saved = dao.save(account);

        assertFalse(dao.delete(saved.getId(), otherUserId));
        assertTrue(dao.delete(saved.getId(), ownerId));
        assertTrue(dao.getById(saved.getId(), ownerId).isEmpty());
    }
}
