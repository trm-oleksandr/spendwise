package sk.upjs.ics.spendwise.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.entity.Account;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class JdbcAccountDao implements AccountDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Account> accountMapper;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountMapper = (rs, rowNum) -> {
            Account a = new Account();
            a.setId(rs.getLong("id"));
            a.setUserId(rs.getLong("user_id"));
            a.setName(rs.getString("name"));
            a.setCurrency(rs.getString("currency"));
            a.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return a;
        };
    }

    @Override
    public List<Account> getAll(Long userId) {
        String sql = "SELECT * FROM account WHERE user_id = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, accountMapper, userId);
    }

    @Override
    public Optional<Account> getById(Long id) {
        String sql = "SELECT * FROM account WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, accountMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Account save(Account account) {
        if (account.getId() == null) {
            // INSERT
            String sql = "INSERT INTO account (user_id, name, currency) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, account.getUserId());
                ps.setString(2, account.getName());
                ps.setString(3, account.getCurrency());
                return ps;
            }, keyHolder);
            account.setId(keyHolder.getKey().longValue());
            // Обновляем created_at из БД (опционально, но полезно)
            return getById(account.getId()).orElse(account);
        } else {
            // UPDATE
            String sql = "UPDATE account SET name = ?, currency = ? WHERE id = ?";
            jdbcTemplate.update(sql, account.getName(), account.getCurrency(), account.getId());
            return account;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM account WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}