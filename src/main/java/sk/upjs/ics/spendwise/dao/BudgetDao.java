package sk.upjs.ics.spendwise.dao;

import sk.upjs.ics.spendwise.entity.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetDao {
    List<Budget> getAll(Long userId);
    Optional<Budget> getById(Long id, Long userId);
    Budget save(Budget budget);
    boolean delete(Long id, Long userId);
}
