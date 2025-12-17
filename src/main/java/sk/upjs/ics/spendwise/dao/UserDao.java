package sk.upjs.ics.spendwise.dao;

import java.util.Optional;
import sk.upjs.ics.spendwise.entity.AppUser;

public interface UserDao {
    AppUser create(AppUser user);

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
