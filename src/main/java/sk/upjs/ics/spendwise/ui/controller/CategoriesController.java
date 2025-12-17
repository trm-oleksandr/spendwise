package sk.upjs.ics.spendwise.ui.controller;

import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.factory.DaoFactory;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.util.Alerts;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class CategoriesController {

    @FXML
    private TableView<Category> categoriesTable;

    @FXML
    private TableColumn<Category, String> nameCol;

    @FXML
    private TableColumn<Category, CategoryType> typeCol;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<CategoryType> typeBox;

    private final CategoryDao categoryDao;

    public CategoriesController() {
        DaoFactory daoFactory = JdbcDaoFactory.getInstance();
        this.categoryDao = daoFactory.categoryDao();
    }

    @FXML
    public void initialize() {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        typeBox.setItems(FXCollections.observableArrayList(CategoryType.values()));

        loadCategories(currentUser.getId());

        categoriesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> fillForm(newValue));
    }

    @FXML
    private void onAdd(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            Alerts.error("Validation error", "Name is required.");
            return;
        }

        CategoryType type = typeBox.getValue();
        if (type == null) {
            Alerts.error("Validation error", "Type is required.");
            return;
        }

        Category category = new Category();
        category.setUserId(currentUser.getId());
        category.setName(name);
        category.setType(type);

        categoryDao.create(category);
        loadCategories(currentUser.getId());
        clearForm();
    }

    @FXML
    private void onUpdate(ActionEvent event) {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("No selection", "Please select a category to update.");
            return;
        }

        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            Alerts.error("Validation error", "Name is required.");
            return;
        }

        CategoryType type = typeBox.getValue();
        if (type == null) {
            Alerts.error("Validation error", "Type is required.");
            return;
        }

        selected.setName(name);
        selected.setType(type);
        categoryDao.update(selected);
        loadCategories(selected.getUserId());
        clearSelection();
    }

    @FXML
    private void onDelete(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("No selection", "Please select a category to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete category");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected category?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            categoryDao.delete(currentUser.getId(), selected.getId());
            loadCategories(currentUser.getId());
            clearForm();
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        SceneSwitcher.switchTo("ui/dashboard.fxml");
    }

    private void loadCategories(long userId) {
        List<Category> categories = categoryDao.findAll(userId);
        categoriesTable.setItems(FXCollections.observableArrayList(categories));
    }

    private void fillForm(Category category) {
        if (category == null) {
            clearForm();
            return;
        }

        nameField.setText(category.getName());
        typeBox.setValue(category.getType());
    }

    private void clearForm() {
        nameField.clear();
        typeBox.getSelectionModel().clearSelection();
        clearSelection();
    }

    private void clearSelection() {
        categoriesTable.getSelectionModel().clearSelection();
    }
}
