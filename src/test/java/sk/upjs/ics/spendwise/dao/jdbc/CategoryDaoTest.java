package sk.upjs.ics.spendwise.dao.jdbc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import com.zaxxer.hikari.HikariDataSource;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requires running local PostgreSQL with seed data")
class CategoryDaoTest {

    // Подключение к базе данных для теста
    private JdbcTemplate getJdbcTemplate() {
        HikariDataSource ds = new HikariDataSource();
        // Используем правильные данные из твоего db.properties
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/spendwise");
        ds.setUsername("spendwise");
        ds.setPassword("spendwise");
        return new JdbcTemplate(ds);
    }

    @Test
    void testCreateAndRead() {
        JdbcCategoryDao dao = new JdbcCategoryDao(getJdbcTemplate());

        // 1. Создаем тестовую категорию
        Category c = new Category();
        c.setUserId(1L); // Предполагаем, что user с ID=1 уже есть (из init.sql)
        c.setName("JUNIT_TEST_" + System.currentTimeMillis());
        c.setType(CategoryType.EXPENSE);

        // Сохраняем
        Category saved = dao.save(c);

        // Проверяем, что ID создался
        assertNotNull(saved.getId());

        // 2. Читаем список из базы
        List<Category> all = dao.getAll(1L);

        // Ищем нашу категорию в списке
        boolean found = false;
        for (Category cat : all) {
            if (cat.getId().equals(saved.getId())) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Category should be found in database");

        // 3. Удаляем за собой (чтобы не мусорить)
        dao.delete(saved.getId(), 1L);
    }
}