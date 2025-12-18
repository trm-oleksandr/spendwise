package sk.upjs.ics.spendwise.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionsController {

    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, String> categoryCol;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, BigDecimal> amountCol;
    @FXML private TableColumn<Transaction, String> accountCol;
    @FXML private TableColumn<Transaction, String> noteCol;

    @FXML private ComboBox<Account> accountFilter;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private Button resetBtn;
    @FXML private Label totalLabel;

    @FXML private Button addBtn;
    @FXML private Button deleteBtn;
    @FXML private Button backBtn;

    private final TransactionDao transactionDao = JdbcDaoFactory.INSTANCE.transactionDao();
    private final AccountDao accountDao = JdbcDaoFactory.INSTANCE.accountDao();
    private final CategoryDao categoryDao = JdbcDaoFactory.INSTANCE.categoryDao();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    @FXML
    public void initialize() {
        // Настройка таблицы
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(formatter.format(cell.getValue().getOccurredAt())));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        accountCol.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        loadFilters();
        refreshTable();

        // Фильтры
        accountFilter.setOnAction(e -> refreshTable());
        categoryFilter.setOnAction(e -> refreshTable());

        resetBtn.setOnAction(e -> {
            accountFilter.getSelectionModel().clearSelection();
            categoryFilter.getSelectionModel().clearSelection();
            refreshTable();
        });

        // Навигация
        backBtn.setOnAction(e -> SceneSwitcher.switchScene(e, "/ui/dashboard.fxml", "Dashboard"));
        addBtn.setOnAction(e -> SceneSwitcher.switchScene(e, "/ui/transaction_edit.fxml", "New Transaction"));

        // Удаление
        deleteBtn.setOnAction(e -> {
            Transaction selected = transactionsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                transactionDao.delete(selected.getId(), getCurrentUserId());
                refreshTable();
            } else {
                new Alert(Alert.AlertType.WARNING, "Select transaction first!").show();
            }
        });
    }

    private void loadFilters() {
        accountFilter.setItems(FXCollections.observableArrayList(accountDao.getAll(getCurrentUserId())));
        categoryFilter.setItems(FXCollections.observableArrayList(categoryDao.getAll(getCurrentUserId())));
    }

    private void refreshTable() {
        try {
            List<Transaction> all = transactionDao.getAll(getCurrentUserId());

            Account selAccount = accountFilter.getValue();
            Category selCategory = categoryFilter.getValue();

            List<Transaction> filtered = all.stream()
                    .filter(t -> selAccount == null || t.getAccountId().equals(selAccount.getId()))
                    .filter(t -> selCategory == null || t.getCategoryId().equals(selCategory.getId()))
                    .collect(Collectors.toList());

            transactionsTable.setItems(FXCollections.observableArrayList(filtered));

            BigDecimal total = filtered.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalLabel.setText("Total: " + total + " €");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long getCurrentUserId() {
        if (AuthContext.getCurrentUser() == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return AuthContext.getCurrentUser().getId();
    }
}