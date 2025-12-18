package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.service.CategoryService; // <-- Новый импорт
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.util.List;

public class CategoriesController {

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, Long> idCol;
    @FXML private TableColumn<Category, String> nameCol;
    @FXML private TableColumn<Category, CategoryType> typeCol;

    @FXML private TextField nameField;
    @FXML private ComboBox<CategoryType> typeComboBox;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    // ИСПОЛЬЗУЕМ SERVICE ВМЕСТО DAO
    private final CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        typeComboBox.setItems(FXCollections.observableArrayList(CategoryType.values()));
        typeComboBox.getSelectionModel().select(CategoryType.EXPENSE);

        refreshTable();

        addButton.setOnAction(this::onAdd);
        deleteButton.setOnAction(this::onDelete);

        backButton.setOnAction(event -> {
            SceneSwitcher.switchScene(event, "/ui/dashboard.fxml", "Dashboard");
        });
    }

    private void onAdd(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            CategoryType type = typeComboBox.getValue();

            if (name.isEmpty() || type == null) {
                new Alert(Alert.AlertType.WARNING, "Please enter name and type!").show();
                return;
            }

            Category c = new Category();
            c.setUserId(getCurrentUserId());
            c.setName(name);
            c.setType(type);

            // Вызов через сервис
            categoryService.save(c);

            nameField.clear();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void onDelete(ActionEvent event) {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Вызов через сервис
            categoryService.delete(selected.getId(), getCurrentUserId());
            refreshTable();
        } else {
            new Alert(Alert.AlertType.WARNING, "Select a category to delete!").show();
        }
    }

    private void refreshTable() {
        // Вызов через сервис
        List<Category> list = categoryService.getAll(getCurrentUserId());
        categoriesTable.setItems(FXCollections.observableArrayList(list));
    }

    private Long getCurrentUserId() {
        if (AuthContext.getCurrentUser() == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return AuthContext.getCurrentUser().getId();
    }
}