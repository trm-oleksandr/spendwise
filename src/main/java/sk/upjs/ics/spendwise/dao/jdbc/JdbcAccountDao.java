package sk.upjs.ics.spendwise.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.entity.Account;

public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Account> findAll(long userId) {
        return jdbcTemplate.query(
                "SELECT id, user_id, name, currency, created_at "
                        + "FROM account WHERE user_id = ? ORDER BY name",
                accountRowMapper(),
                userId
        );
    }

    @Override
    public Optional<Account> findById(long userId, long id) {
        List<Account> accounts = jdbcTemplate.query(
                "SELECT id, user_id, name, currency, created_at "
                        + "FROM account WHERE user_id = ? AND id = ?",
                accountRowMapper(),
                userId,
                id
        );
        return accounts.stream().findFirst();
    }

    @Override
    public Account create(Account account) {
        return jdbcTemplate.queryForObject(
                """
                    INSERT INTO account (user_id, name, currency)
                    VALUES (?, ?, ?)
                    RETURNING id, user_id, name, currency, created_at
                    """,
                accountRowMapper(),
                account.getUserId(),
                account.getName(),
                account.getCurrency()
        );
    }

    @Override
    public Account update(Account account) {
        Instant createdAt = jdbcTemplate.queryForObject(
                """
                    UPDATE account
                    SET name = ?, currency = ?
                    WHERE user_id = ? AND id = ?
                    RETURNING created_at
                    """,
                Instant.class,
                account.getName(),
                account.getCurrency(),
                account.getUserId(),
                account.getId()
        );
        account.setCreatedAt(createdAt);
        return account;
    }

    @Override
    public void delete(long userId, long id) {
        jdbcTemplate.update(
                "DELETE FROM account WHERE user_id = ? AND id = ?",
                userId,
                id
        );
    }

    private RowMapper<Account> accountRowMapper() {
        return new AccountRowMapper();
    }

    private static class AccountRowMapper implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getObject("id", Long.class);
            Long userId = rs.getObject("user_id", Long.class);
            String name = rs.getString("name");
            String currency = rs.getString("currency");
            Instant createdAt = rs.getObject("created_at", Instant.class);
            return new Account(id, userId, name, currency, createdAt);
        }
    }
}
