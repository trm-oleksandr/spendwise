package sk.upjs.ics.spendwise.entity;

import java.time.Instant;

public class Account {
    private Long id;
    private Long userId;
    private String name;
    private String currency;
    private Instant createdAt;

    //(need for RowMapper)
    public Account() {
    }

    public Account(Long id, Long userId, String name, String currency, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.currency = currency;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
        return name + " (" + currency + ")";
    }
}