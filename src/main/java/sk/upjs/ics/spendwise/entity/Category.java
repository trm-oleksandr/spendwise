package sk.upjs.ics.spendwise.entity;

public class Category {
    private long id;
    private long userId;
    private String name;
    private CategoryType type;

    public Category() {
    }

    public Category(long id, long userId, String name, CategoryType type) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Category{"
                + "id=" + id
                + ", userId=" + userId
                + ", name='" + name + '\''
                + ", type=" + type
                + '}';
    }
}
