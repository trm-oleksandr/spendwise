package sk.upjs.ics.spendwise.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.spendwise.dao.BudgetDao;
import sk.upjs.ics.spendwise.entity.Budget;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class JdbcBudgetDao implements BudgetDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBudgetDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Budget> getAll(Long userId) {
        String sql = """
            SELECT b.*, a.name AS account_name
            FROM budget b
            JOIN account a ON b.account_id = a.id
            WHERE b.user_id = ?
            ORDER BY b.start_date DESC, b.id DESC
        """;
        return jdbcTemplate.query(sql, new BudgetMapper(), userId);
    }

    @Override
    public Optional<Budget> getById(Long id, Long userId) {
        String sql = """
            SELECT b.*, a.name AS account_name
            FROM budget b
            JOIN account a ON b.account_id = a.id
            WHERE b.id = ? AND b.user_id = ?
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new BudgetMapper(), id, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Budget save(Budget budget) {
        if (budget.getId() == null) {
            String sql = "INSERT INTO budget (user_id, account_id, limit_amount, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, budget.getUserId());
                ps.setLong(2, budget.getAccountId());
                ps.setBigDecimal(3, budget.getLimitAmount());
                ps.setDate(4, Date.valueOf(budget.getStartDate()));
                ps.setDate(5, Date.valueOf(budget.getEndDate()));
                return ps;
            }, keyHolder);
            budget.setId(((Number) keyHolder.getKeys().get("id")).longValue());
            return budget;
        }

        String sql = "UPDATE budget SET account_id = ?, limit_amount = ?, start_date = ?, end_date = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql,
                budget.getAccountId(),
                budget.getLimitAmount(),
                Date.valueOf(budget.getStartDate()),
                Date.valueOf(budget.getEndDate()),
                budget.getId(),
                budget.getUserId());
        return budget;
    }

    @Override
    public boolean delete(Long id, Long userId) {
        String sql = "DELETE FROM budget WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    private static class BudgetMapper implements RowMapper<Budget> {
        @Override
        public Budget mapRow(ResultSet rs, int rowNum) throws SQLException {
            Budget b = new Budget();
            b.setId(rs.getLong("id"));
            b.setUserId(rs.getLong("user_id"));
            b.setAccountId(rs.getLong("account_id"));
            b.setLimitAmount(rs.getBigDecimal("limit_amount"));
            Date start = rs.getDate("start_date");
            Date end = rs.getDate("end_date");
            Timestamp createdAt = rs.getTimestamp("created_at");
            b.setStartDate(start != null ? start.toLocalDate() : LocalDate.now());
            b.setEndDate(end != null ? end.toLocalDate() : LocalDate.now());
            if (createdAt != null) {
                b.setCreatedAt(createdAt.toInstant());
            }
            b.setAccountName(rs.getString("account_name"));
            return b;
        }
    }
}
