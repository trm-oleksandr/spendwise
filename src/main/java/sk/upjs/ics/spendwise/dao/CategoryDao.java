package sk.upjs.ics.spendwise.dao;

import java.util.List;
import java.util.Optional;
import sk.upjs.ics.spendwise.entity.Category;

public interface CategoryDao {
    List<Category> findAll(long userId);

    Optional<Category> findById(long userId, long id);

    Category create(Category category);

    Category update(Category category);

    void delete(long userId, long id);
}
