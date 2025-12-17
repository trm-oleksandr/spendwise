package sk.upjs.ics.spendwise.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private long id;
    private long userId;
    private long accountId;
    private long categoryId;
    private BigDecimal amount;
    private Instant occurredAt;
    private String note;
    private Instant createdAt;

    public Transaction() {
    }

    public Transaction(
            long id,
            long userId,
            long accountId,
            long categoryId,
            BigDecimal amount,
            Instant occurredAt,
            String note,
            Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.occurredAt = occurredAt;
        this.note = note;
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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "id=" + id
                + ", userId=" + userId
                + ", accountId=" + accountId
                + ", categoryId=" + categoryId
                + ", amount=" + amount
                + ", occurredAt=" + occurredAt
                + ", note='" + note + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
