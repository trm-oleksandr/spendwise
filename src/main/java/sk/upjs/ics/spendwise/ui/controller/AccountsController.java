package sk.upjs.ics.spendwise.ui.controller;

import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.factory.DaoFactory;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.util.Alerts;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class AccountsController {

    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, String> nameCol;

    @FXML
    private TableColumn<Account, String> currencyCol;

    @FXML
    private TextField nameField;

    @FXML
    private TextField currencyField;

    private final AccountDao accountDao;

    public AccountsController() {
        DaoFactory daoFactory = JdbcDaoFactory.getInstance();
        this.accountDao = daoFactory.accountDao();
    }

    @FXML
    public void initialize() {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));

        loadAccounts(currentUser.getId());

        accountsTable.getSelectionModel()
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

        String currency = currencyField.getText();
        if (currency == null || currency.isBlank()) {
            currency = "EUR";
            currencyField.setText(currency);
        }

        Account account = new Account();
        account.setUserId(currentUser.getId());
        account.setName(name);
        account.setCurrency(currency);

        accountDao.create(account);
        loadAccounts(currentUser.getId());
        clearForm();
    }

    @FXML
    private void onUpdate(ActionEvent event) {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("No selection", "Please select an account to update.");
            return;
        }

        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            Alerts.error("Validation error", "Name is required.");
            return;
        }

        String currency = currencyField.getText();
        if (currency == null || currency.isBlank()) {
            currency = "EUR";
            currencyField.setText(currency);
        }

        selected.setName(name);
        selected.setCurrency(currency);
        accountDao.update(selected);
        loadAccounts(selected.getUserId());
        clearSelection();
    }

    @FXML
    private void onDelete(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("No selection", "Please select an account to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the selected account?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            accountDao.delete(currentUser.getId(), selected.getId());
            loadAccounts(currentUser.getId());
            clearForm();
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        SceneSwitcher.switchTo("ui/dashboard.fxml");
    }

    private void loadAccounts(long userId) {
        List<Account> accounts = accountDao.findAll(userId);
        accountsTable.setItems(FXCollections.observableArrayList(accounts));
    }

    private void fillForm(Account account) {
        if (account == null) {
            clearForm();
            return;
        }

        nameField.setText(account.getName());
        currencyField.setText(account.getCurrency());
    }

    private void clearForm() {
        nameField.clear();
        currencyField.clear();
        clearSelection();
    }

    private void clearSelection() {
        accountsTable.getSelectionModel().clearSelection();
    }
}
