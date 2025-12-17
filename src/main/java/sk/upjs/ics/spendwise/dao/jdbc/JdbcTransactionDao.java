package sk.upjs.ics.spendwise.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;

public class JdbcTransactionDao implements TransactionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> findByMonth(long userId, YearMonth month, Long accountId, Long categoryId,
            CategoryType type) {
        Instant from = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT t.id, t.user_id, t.account_id, t.category_id, t.amount, t.occurred_at, t.note, t.created_at ");
        sql.append("FROM txn t ");
        if (type != null) {
            sql.append("JOIN category c ON c.id = t.category_id AND c.user_id = t.user_id ");
        }
        sql.append("WHERE t.user_id = ? AND t.occurred_at >= ? AND t.occurred_at < ? ");
        params.add(userId);
        params.add(from);
        params.add(to);

        if (accountId != null) {
            sql.append("AND t.account_id = ? ");
            params.add(accountId);
        }
        if (categoryId != null) {
            sql.append("AND t.category_id = ? ");
            params.add(categoryId);
        }
        if (type != null) {
            sql.append("AND c.type = ? ");
            params.add(type.name());
        }

        sql.append("ORDER BY t.occurred_at DESC");

        return jdbcTemplate.query(sql.toString(), transactionRowMapper(), params.toArray());
    }

    @Override
    public Optional<Transaction> findById(long userId, long id) {
        List<Transaction> transactions = jdbcTemplate.query(
                "SELECT id, user_id, account_id, category_id, amount, occurred_at, note, created_at "
                        + "FROM txn WHERE user_id = ? AND id = ?",
                transactionRowMapper(),
                userId,
                id
        );
        return transactions.stream().findFirst();
    }

    @Override
    public Transaction create(Transaction transaction) {
        return jdbcTemplate.queryForObject(
                """
                    INSERT INTO txn (user_id, account_id, category_id, amount, occurred_at, note)
                    VALUES (?, ?, ?, ?, ?, ?)
                    RETURNING id, user_id, account_id, category_id, amount, occurred_at, note, created_at
                    """,
                transactionRowMapper(),
                transaction.getUserId(),
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                transaction.getOccurredAt(),
                transaction.getNote()
        );
    }

    @Override
    public Transaction update(Transaction transaction) {
        return jdbcTemplate.queryForObject(
                """
                    UPDATE txn
                    SET account_id = ?, category_id = ?, amount = ?, occurred_at = ?, note = ?
                    WHERE user_id = ? AND id = ?
                    RETURNING id, user_id, account_id, category_id, amount, occurred_at, note, created_at
                    """,
                transactionRowMapper(),
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                transaction.getOccurredAt(),
                transaction.getNote(),
                transaction.getUserId(),
                transaction.getId()
        );
    }

    @Override
    public void delete(long userId, long id) {
        jdbcTemplate.update(
                "DELETE FROM txn WHERE user_id = ? AND id = ?",
                userId,
                id
        );
    }

    private RowMapper<Transaction> transactionRowMapper() {
        return new TransactionRowMapper();
    }

    private static class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            long userId = rs.getLong("user_id");
            long accountId = rs.getLong("account_id");
            long categoryId = rs.getLong("category_id");
            BigDecimal amount = rs.getBigDecimal("amount");
            Instant occurredAt = rs.getObject("occurred_at", Instant.class);
            String note = rs.getString("note");
            Instant createdAt = rs.getObject("created_at", Instant.class);
            return new Transaction(id, userId, accountId, categoryId, amount, occurredAt, note, createdAt);
        }
    }
}
