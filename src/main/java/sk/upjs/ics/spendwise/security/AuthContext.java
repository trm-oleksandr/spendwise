package sk.upjs.ics.spendwise.security;

import sk.upjs.ics.spendwise.entity.AppUser;

public final class AuthContext {

    private static AppUser currentUser;

    private AuthContext() {
    }

    public static AppUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(AppUser user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }
}
