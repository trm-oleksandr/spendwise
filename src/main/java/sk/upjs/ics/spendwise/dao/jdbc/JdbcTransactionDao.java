package sk.upjs.ics.spendwise.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional; // ОБЯЗАТЕЛЬНО

public class JdbcTransactionDao implements TransactionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> getAll(Long userId) {
        // Достаем тип категории (INCOME/EXPENSE) через JOIN
        String sql = """
            SELECT 
                t.id, t.amount, t.occurred_at, t.note, t.account_id, t.category_id,
                c.name AS category_name, c.type AS category_type,
                a.name AS account_name
            FROM txn t
            JOIN category c ON t.category_id = c.id
            JOIN account a ON t.account_id = a.id
            WHERE t.user_id = ?
            ORDER BY t.occurred_at DESC
        """;
        return jdbcTemplate.query(sql, new TransactionMapper(), userId);
    }

    @Override
    public Optional<Transaction> getById(Long id) { // Возвращаем Optional
        String sql = """
            SELECT 
                t.id, t.amount, t.occurred_at, t.note, t.account_id, t.category_id,
                c.name AS category_name, c.type AS category_type,
                a.name AS account_name
            FROM txn t
            JOIN category c ON t.category_id = c.id
            JOIN account a ON t.account_id = a.id
            WHERE t.id = ?
        """;
        try {
            Transaction t = jdbcTemplate.queryForObject(sql, new TransactionMapper(), id);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Transaction save(Transaction t) {
        if (t.getId() == null) {
            String sql = "INSERT INTO txn (user_id, account_id, category_id, amount, occurred_at, note) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, t.getUserId());
                ps.setLong(2, t.getAccountId());
                ps.setLong(3, t.getCategoryId());
                ps.setBigDecimal(4, t.getAmount());
                ps.setTimestamp(5, Timestamp.from(t.getOccurredAt()));
                ps.setString(6, t.getNote());
                return ps;
            }, keyHolder);
            t.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        }
        return t;
    }

    @Override
    public boolean delete(Long id) { // Возвращаем boolean
        String sql = "DELETE FROM txn WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    private static class TransactionMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction t = new Transaction();
            t.setId(rs.getLong("id"));
            t.setAmount(rs.getBigDecimal("amount"));
            t.setOccurredAt(rs.getTimestamp("occurred_at").toInstant());
            t.setNote(rs.getString("note"));

            t.setAccountId(rs.getLong("account_id"));
            t.setCategoryId(rs.getLong("category_id"));

            t.setAccountName(rs.getString("account_name"));
            t.setCategoryName(rs.getString("category_name"));

            // ПРЕВРАЩАЕМ СТРОКУ "EXPENSE" В ENUM
            String typeStr = rs.getString("category_type");
            if (typeStr != null) {
                try {
                    t.setType(CategoryType.valueOf(typeStr));
                } catch (IllegalArgumentException e) {
                    t.setType(CategoryType.EXPENSE);
                }
            }

            return t;
        }
    }
}