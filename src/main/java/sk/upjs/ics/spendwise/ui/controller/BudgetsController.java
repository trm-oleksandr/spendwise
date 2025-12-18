package sk.upjs.ics.spendwise.ui.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Budget;
import sk.upjs.ics.spendwise.factory.DefaultServiceFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.service.AccountService;
import sk.upjs.ics.spendwise.service.BudgetService;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BudgetsController {

    @FXML private TableView<Budget> budgetsTable;
    @FXML private TableColumn<Budget, String> accountCol;
    @FXML private TableColumn<Budget, String> periodCol;
    @FXML private TableColumn<Budget, BigDecimal> limitCol;
    @FXML private TableColumn<Budget, BigDecimal> spentCol;
    @FXML private TableColumn<Budget, BigDecimal> remainingCol;

    @FXML private ComboBox<Account> accountCombo;
    @FXML private TextField limitField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Button saveBtn;
    @FXML private Button resetBtn;
    @FXML private Button deleteBtn;
    @FXML private Button backBtn;

    private final BudgetService budgetService = DefaultServiceFactory.INSTANCE.budgetService();
    private final AccountService accountService = DefaultServiceFactory.INSTANCE.accountService();

    @FXML
    public void initialize() {
        setupTable();
        loadAccounts();
        refreshTable();

        saveBtn.setOnAction(this::onSave);
        resetBtn.setOnAction(e -> clearForm());
        deleteBtn.setOnAction(this::onDelete);
        backBtn.setOnAction(e -> SceneSwitcher.switchScene(e, "/ui/dashboard.fxml", "Dashboard"));

        budgetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
            }
        });
    }

    private void setupTable() {
        accountCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountName()));
        periodCol.setCellValueFactory(data -> {
            Budget b = data.getValue();
            String text = b.getStartDate() + " → " + b.getEndDate();
            return new SimpleStringProperty(text);
        });
        limitCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLimitAmount()));
        spentCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSpentAmount()));
        remainingCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getRemainingAmount()));
    }

    private void loadAccounts() {
        List<Account> accounts = accountService.getAll(getCurrentUserId());
        accountCombo.setItems(FXCollections.observableArrayList(accounts));
        accountCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Account account) {
                return account == null ? "" : account.getName();
            }

            @Override
            public Account fromString(String string) {
                return null;
            }
        });
    }

    private void refreshTable() {
        List<Budget> budgets = budgetService.getAll(getCurrentUserId());
        budgetsTable.setItems(FXCollections.observableArrayList(budgets));
    }

    private void onSave(ActionEvent event) {
        if (accountCombo.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Select account first!").show();
            return;
        }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Select start and end date!").show();
            return;
        }
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (end.isBefore(start)) {
            new Alert(Alert.AlertType.WARNING, "End date cannot be before start date.").show();
            return;
        }

        BigDecimal limit;
        try {
            limit = new BigDecimal(limitField.getText().trim());
            if (limit.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("limit must be positive");
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Invalid limit amount").show();
            return;
        }

        Budget selected = budgetsTable.getSelectionModel().getSelectedItem();
        Budget budget = selected != null ? selected : new Budget();
        budget.setUserId(getCurrentUserId());
        budget.setAccountId(accountCombo.getValue().getId());
        budget.setLimitAmount(limit);
        budget.setStartDate(start);
        budget.setEndDate(end);

        budgetService.save(budget);
        refreshTable();
        clearForm();
    }

    private void onDelete(ActionEvent event) {
        Budget selected = budgetsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select budget first!").show();
            return;
        }
        budgetService.delete(selected.getId(), getCurrentUserId());
        refreshTable();
        clearForm();
    }

    private void fillForm(Budget budget) {
        accountCombo.getSelectionModel().select(findAccountById(budget.getAccountId()));
        limitField.setText(budget.getLimitAmount() != null ? budget.getLimitAmount().toPlainString() : "");
        startDatePicker.setValue(budget.getStartDate());
        endDatePicker.setValue(budget.getEndDate());
    }

    private Account findAccountById(Long accountId) {
        return accountCombo.getItems().stream()
                .filter(a -> a.getId().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    private void clearForm() {
        budgetsTable.getSelectionModel().clearSelection();
        accountCombo.getSelectionModel().clearSelection();
        limitField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private Long getCurrentUserId() {
        if (AuthContext.getCurrentUser() == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return AuthContext.getCurrentUser().getId();
    }
}
