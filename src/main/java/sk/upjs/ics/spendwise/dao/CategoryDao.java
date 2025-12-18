package sk.upjs.ics.spendwise.dao;

import sk.upjs.ics.spendwise.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    List<Category> getAll(Long userId);
    Optional<Category> getById(Long id, Long userId);
    Category save(Category category);
    boolean delete(Long id, Long userId);
}