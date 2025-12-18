package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.util.List;

public class AccountsController {

    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, Long> idCol;

    @FXML
    private TableColumn<Account, String> nameCol;

    @FXML
    private TableColumn<Account, String> currencyCol;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private final AccountDao accountDao = JdbcDaoFactory.INSTANCE.accountDao();

    // ВРЕМЕННО: Пока у нас нет передачи залогиненного юзера, хардкодим ID = 1
    // Позже мы заменим это на AuthContext.getCurrentUser().getId()
    private final Long currentUserId = 1L;

    @FXML
    public void initialize() {
        // Настраиваем колонки таблицы (связываем с полями класса Account)
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));

        backButton.setOnAction(event -> {
            SceneSwitcher.switchScene(event, "/ui/dashboard.fxml", "Dashboard");
        });

        // Загружаем данные
        refreshTable();

        // Простая логика кнопок (пока заглушки)
        addButton.setOnAction(event -> {
            // Тут будет логика добавления. Пока просто создадим тестовый счет
            Account newAccount = new Account();
            newAccount.setUserId(currentUserId);
            newAccount.setName("New Account " + System.currentTimeMillis() % 1000);
            newAccount.setCurrency("EUR");
            accountDao.save(newAccount);
            refreshTable();
        });

        deleteButton.setOnAction(event -> {
            Account selected = accountsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                accountDao.delete(selected.getId());
                refreshTable();
            } else {
                new Alert(Alert.AlertType.WARNING, "Select an account first!").show();
            }
        });

        // Кнопку "Back" пока не трогаем, так как сцены еще не переключаются
    }

    private void refreshTable() {
        List<Account> accounts = accountDao.getAll(currentUserId);
        accountsTable.setItems(FXCollections.observableArrayList(accounts));
    }
}