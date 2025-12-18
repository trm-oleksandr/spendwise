package sk.upjs.ics.spendwise.entity;

public class Category {
    private Long id;
    private Long userId;
    private String name;
    private CategoryType type; // INCOME/EXPENSE

    public Category() {
    }

    public Category(Long id, Long userId, String name, CategoryType type) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}