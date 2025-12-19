package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryDaoTest extends DaoIntegrationTestBase {

    @Test
    void createsUpdatesAndListsCategories() {
        Long userId = insertUser("category-owner");
        JdbcCategoryDao dao = new JdbcCategoryDao(jdbcTemplate);

        Category category = new Category();
        category.setUserId(userId);
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);

        Category saved = dao.save(category);
        assertNotNull(saved.getId());
        assertEquals(CategoryType.EXPENSE, saved.getType());

        saved.setName("Groceries");
        saved.setType(CategoryType.INCOME);
        dao.save(saved);

        Category updated = dao.getById(saved.getId(), userId).orElseThrow();
        assertEquals("Groceries", updated.getName());
        assertEquals(CategoryType.INCOME, updated.getType());

        List<Category> categories = dao.getAll(userId);
        assertEquals(1, categories.size());
        assertEquals(saved.getId(), categories.getFirst().getId());
    }

    @Test
    void deleteRespectsUserIsolation() {
        Long ownerId = insertUser("category-user");
        Long otherUserId = insertUser("another-user");
        JdbcCategoryDao dao = new JdbcCategoryDao(jdbcTemplate);

        Category category = new Category();
        category.setUserId(ownerId);
        category.setName("Transport");
        category.setType(CategoryType.EXPENSE);
        Category saved = dao.save(category);

        assertFalse(dao.delete(saved.getId(), otherUserId));
        assertTrue(dao.delete(saved.getId(), ownerId));
        assertTrue(dao.getById(saved.getId(), ownerId).isEmpty());
    }
}
