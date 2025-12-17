package sk.upjs.ics.spendwise.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;

public class JdbcCategoryDao implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> findAll(long userId) {
        return jdbcTemplate.query(
                "SELECT id, user_id, name, type FROM category WHERE user_id = ? ORDER BY type, name",
                categoryRowMapper(),
                userId
        );
    }

    @Override
    public Optional<Category> findById(long userId, long id) {
        List<Category> categories = jdbcTemplate.query(
                "SELECT id, user_id, name, type FROM category WHERE user_id = ? AND id = ?",
                categoryRowMapper(),
                userId,
                id
        );
        return categories.stream().findFirst();
    }

    @Override
    public Category create(Category category) {
        return jdbcTemplate.queryForObject(
                """
                    INSERT INTO category (user_id, name, type)
                    VALUES (?, ?, ?)
                    RETURNING id, user_id, name, type
                    """,
                categoryRowMapper(),
                category.getUserId(),
                category.getName(),
                category.getType().name()
        );
    }

    @Override
    public Category update(Category category) {
        return jdbcTemplate.queryForObject(
                """
                    UPDATE category
                    SET name = ?, type = ?
                    WHERE user_id = ? AND id = ?
                    RETURNING id, user_id, name, type
                    """,
                categoryRowMapper(),
                category.getName(),
                category.getType().name(),
                category.getUserId(),
                category.getId()
        );
    }

    @Override
    public void delete(long userId, long id) {
        jdbcTemplate.update(
                "DELETE FROM category WHERE user_id = ? AND id = ?",
                userId,
                id
        );
    }

    private RowMapper<Category> categoryRowMapper() {
        return new CategoryRowMapper();
    }

    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getObject("id", Long.class);
            Long userId = rs.getObject("user_id", Long.class);
            String name = rs.getString("name");
            CategoryType type = CategoryType.valueOf(rs.getString("type"));
            return new Category(id, userId, name, type);
        }
    }
}
