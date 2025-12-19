package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.ics.spendwise.entity.AppUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcUserDaoTest extends DaoIntegrationTestBase {

    @Test
    void createsAndFindsUsers() {
        JdbcUserDao dao = new JdbcUserDao(jdbcTemplate);

        assertFalse(dao.findByUsername("missing").isPresent());
        assertFalse(dao.existsByUsername("missing"));

        AppUser created = dao.create(new AppUser(null, "john", "hash", null));
        assertNotNull(created.getId());
        assertEquals("john", created.getUsername());
        assertNotNull(created.getCreatedAt());

        Optional<AppUser> found = dao.findByUsername("john");
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.orElseThrow().getId());
        assertTrue(dao.existsByUsername("john"));
    }
}
