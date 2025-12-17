package sk.upjs.ics.spendwise.ui.model;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionRow {

    private final long id;
    private final Instant occurredAt;
    private final String accountName;
    private final String categoryName;
    private final String type;
    private final BigDecimal amount;
    private final String note;

    public TransactionRow(
            long id,
            Instant occurredAt,
            String accountName,
            String categoryName,
            String type,
            BigDecimal amount,
            String note) {
        this.id = id;
        this.occurredAt = occurredAt;
        this.accountName = accountName;
        this.categoryName = categoryName;
        this.type = type;
        this.amount = amount;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }
}
