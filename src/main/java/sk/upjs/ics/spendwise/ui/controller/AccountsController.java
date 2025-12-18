package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.service.AccountService;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.time.Instant;
import java.util.List;

public class AccountsController {

    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, Long> idCol;
    @FXML private TableColumn<Account, String> nameCol;
    @FXML private TableColumn<Account, String> currencyCol;

    @FXML private TextField nameField;      // Новое поле
    @FXML private TextField currencyField;  // Новое поле

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    private final AccountService accountService = new AccountService();

    @FXML
    public void initialize() {
        // 1. Настройка таблицы
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));

        // 2. Загрузка данных
        refreshTable();

        // 3. Кнопки
        addButton.setOnAction(this::onAdd);
        deleteButton.setOnAction(this::onDelete);

        // Исправленная кнопка "Назад"
        backButton.setOnAction(event -> {
            SceneSwitcher.switchScene(event, "/ui/dashboard.fxml", "Dashboard");
        });
    }

    private void onAdd(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            String currency = currencyField.getText().trim();

            if (name.isEmpty() || currency.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Enter name and currency!").show();
                return;
            }

            Account a = new Account();
            a.setUserId(getCurrentUserId());
            a.setName(name);
            a.setCurrency(currency);
            a.setCreatedAt(Instant.now());

            accountService.save(a);

            nameField.clear();
            refreshTable();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void onDelete(ActionEvent event) {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            accountService.delete(selected.getId(), getCurrentUserId());
            refreshTable();
        } else {
            new Alert(Alert.AlertType.WARNING, "Select account first!").show();
        }
    }

    private void refreshTable() {
        List<Account> accounts = accountService.getAll(getCurrentUserId());
        accountsTable.setItems(FXCollections.observableArrayList(accounts));
    }

    private Long getCurrentUserId() {
        if (AuthContext.getCurrentUser() == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return AuthContext.getCurrentUser().getId();
    }
}