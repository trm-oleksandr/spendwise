package sk.upjs.ics.spendwise.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sk.upjs.ics.spendwise.dao.UserDao;
import sk.upjs.ics.spendwise.entity.AppUser;

public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AppUser create(AppUser user) {
        return jdbcTemplate.queryForObject(
                """
                    INSERT INTO app_user (username, password_hash)
                    VALUES (?, ?)
                    RETURNING id, username, password_hash, created_at
                    """,
                userRowMapper(),
                user.getUsername(),
                user.getPasswordHash()
        );
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        List<AppUser> users = jdbcTemplate.query(
                "SELECT id, username, password_hash, created_at FROM app_user WHERE username = ?",
                userRowMapper(),
                username
        );
        return users.stream().findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM app_user WHERE username = ?)",
                Boolean.class,
                username
        );
        return Boolean.TRUE.equals(exists);
    }

    private RowMapper<AppUser> userRowMapper() {
        return new AppUserRowMapper();
    }

    private static class AppUserRowMapper implements RowMapper<AppUser> {
        @Override
        public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getObject("id", Long.class);
            String username = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            OffsetDateTime createdAt = rs.getObject("created_at", OffsetDateTime.class);
            return new AppUser(id, username, passwordHash, createdAt);
        }
    }
}