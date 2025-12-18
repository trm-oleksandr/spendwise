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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class JdbcTransactionDao implements TransactionDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Transaction> transactionMapper;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        // Маппер собирает данные из JOIN-ов (имя категории, имя счета)
        this.transactionMapper = (rs, rowNum) -> {
            Transaction t = new Transaction();
            t.setId(rs.getLong("id"));
            t.setUserId(rs.getLong("user_id"));
            t.setAccountId(rs.getLong("account_id"));
            t.setCategoryId(rs.getLong("category_id"));
            t.setAmount(rs.getBigDecimal("amount"));
            t.setOccurredAt(rs.getTimestamp("occurred_at").toInstant());
            t.setNote(rs.getString("note"));
            t.setCreatedAt(rs.getTimestamp("created_at").toInstant());

            // Дополнительные поля из JOIN (для красивого отображения в таблице)
            // Проверяем, есть ли эти колонки в ResultSet (чтобы не падать на простых запросах)
            try {
                t.setCategoryName(rs.getString("category_name"));
                t.setType(CategoryType.valueOf(rs.getString("category_type")));
                t.setAccountName(rs.getString("account_name"));
            } catch (Exception e) {
                // Игнорируем, если колонок нет
            }
            return t;
        };
    }

    @Override
    public List<Transaction> getAll(Long userId) {
        // Делаем JOIN, чтобы сразу получить имена категорий и счетов
        String sql = """
            SELECT t.*, c.name as category_name, c.type as category_type, a.name as account_name
            FROM txn t
            JOIN category c ON t.category_id = c.id
            JOIN account a ON t.account_id = a.id
            WHERE t.user_id = ?
            ORDER BY t.occurred_at DESC
        """;
        return jdbcTemplate.query(sql, transactionMapper, userId);
    }

    @Override
    public Optional<Transaction> getById(Long id) {
        String sql = "SELECT * FROM txn WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, transactionMapper, id));
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
            t.setId(keyHolder.getKey().longValue());
            return getById(t.getId()).orElse(t);
        } else {
            String sql = "UPDATE txn SET account_id=?, category_id=?, amount=?, occurred_at=?, note=? WHERE id=?";
            jdbcTemplate.update(sql,
                    t.getAccountId(), t.getCategoryId(), t.getAmount(),
                    Timestamp.from(t.getOccurredAt()), t.getNote(), t.getId());
            return t;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM txn WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}