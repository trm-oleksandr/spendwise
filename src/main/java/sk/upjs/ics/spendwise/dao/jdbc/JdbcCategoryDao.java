package sk.upjs.ics.spendwise.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class JdbcCategoryDao implements CategoryDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Category> categoryMapper;

    public JdbcCategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryMapper = (rs, rowNum) -> {
            Category c = new Category();
            c.setId(rs.getLong("id"));
            c.setUserId(rs.getLong("user_id"));
            c.setName(rs.getString("name"));
            c.setType(CategoryType.valueOf(rs.getString("type")));
            return c;
        };
    }

    @Override
    public List<Category> getAll(Long userId) {
        String sql = "SELECT * FROM category WHERE user_id = ? ORDER BY type, name";
        return jdbcTemplate.query(sql, categoryMapper, userId);
    }

    @Override
    public Optional<Category> getById(Long id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, categoryMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            String sql = "INSERT INTO category (user_id, name, type) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, category.getUserId());
                ps.setString(2, category.getName());
                ps.setString(3, category.getType().name());
                return ps;
            }, keyHolder);

            category.setId(((Number) keyHolder.getKeys().get("id")).longValue());

            return category;
        } else {
            String sql = "UPDATE category SET name = ?, type = ? WHERE id = ?";
            jdbcTemplate.update(sql, category.getName(), category.getType().name(), category.getId());
            return category;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM category WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}