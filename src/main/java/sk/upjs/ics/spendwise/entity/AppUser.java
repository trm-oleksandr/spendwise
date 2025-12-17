package sk.upjs.ics.spendwise.entity;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AppUser {
    private Long id;
    private String username;
    private String passwordHash;
    private OffsetDateTime createdAt;

    public AppUser() {
    }

    public AppUser(Long id, String username, String passwordHash, OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser appUser)) {
            return false;
        }
        return Objects.equals(id, appUser.id)
            && Objects.equals(username, appUser.username)
            && Objects.equals(passwordHash, appUser.passwordHash)
            && Objects.equals(createdAt, appUser.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordHash, createdAt);
    }

    @Override
    public String toString() {
        return "AppUser{"
            + "id=" + id
            + ", username='" + username + '\''
            + ", createdAt=" + createdAt
            + '}';
    }
}
