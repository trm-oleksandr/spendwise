package sk.upjs.ics.spendwise.dao;

import java.util.Optional;
import sk.upjs.ics.spendwise.entity.AppUser;

public interface UserDao {
    AppUser create(AppUser user);

    default AppUser create(String username, String passwordHash) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        return create(user);
    }

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}