package sk.upjs.ics.spendwise.service;

import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import java.util.List;

public class CategoryService {

    private final CategoryDao categoryDao = JdbcDaoFactory.INSTANCE.categoryDao();

    public List<Category> getAll(Long userId) {
        return categoryDao.getAll(userId);
    }

    public void save(Category category) {
        // Здесь могла бы быть валидация, но пока просто сохраняем
        categoryDao.save(category);
    }

    public void delete(Long id) {
        categoryDao.delete(id);
    }
}