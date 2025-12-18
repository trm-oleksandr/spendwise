package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
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

    private final CategoryDao categoryDao = JdbcDaoFactory.INSTANCE.categoryDao();
    private final Long currentUserId = 1L; // Заглушка, позже заменим

    @FXML
    public void initialize() {
        // Настройка таблицы
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Заполнение выпадающего списка (INCOME, EXPENSE)
        typeComboBox.setItems(FXCollections.observableArrayList(CategoryType.values()));
        typeComboBox.getSelectionModel().select(CategoryType.EXPENSE); // По умолчанию Расход

        // Загрузка данных
        refreshTable();

        // Логика кнопок
        addButton.setOnAction(this::onAdd);
        deleteButton.setOnAction(this::onDelete);
        backButton.setOnAction(event ->
                SceneSwitcher.switchScene(event, "/ui/dashboard.fxml", "Dashboard")
        );
    }

    private void onAdd(ActionEvent event) {
        String name = nameField.getText().trim();
        CategoryType type = typeComboBox.getValue();

        if (name.isEmpty() || type == null) {
            new Alert(Alert.AlertType.WARNING, "Please enter name and type!").show();
            return;
        }

        Category c = new Category();
        c.setUserId(currentUserId);
        c.setName(name);
        c.setType(type);

        categoryDao.save(c);

        // Очистка полей и обновление
        nameField.clear();
        refreshTable();
    }

    private void onDelete(ActionEvent event) {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            categoryDao.delete(selected.getId());
            refreshTable();
        } else {
            new Alert(Alert.AlertType.WARNING, "Select a category to delete!").show();
        }
    }

    private void refreshTable() {
        List<Category> list = categoryDao.getAll(currentUserId);
        categoriesTable.setItems(FXCollections.observableArrayList(list));
    }
}