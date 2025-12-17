package sk.upjs.ics.spendwise.entity;

import java.time.Instant;

public class Account {
    private long id;
    private long userId;
    private String name;
    private String currency;
    private Instant createdAt;

    public Account() {
    }

    public Account(long id, long userId, String name, String currency, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Account{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", currency='" + currency + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
