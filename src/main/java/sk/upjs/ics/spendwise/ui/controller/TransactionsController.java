package sk.upjs.ics.spendwise.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button backBtn;

    private final TransactionDao transactionDao = JdbcDaoFactory.INSTANCE.transactionDao();
    private final AccountDao accountDao = JdbcDaoFactory.INSTANCE.accountDao();
    private final CategoryDao categoryDao = JdbcDaoFactory.INSTANCE.categoryDao();

    // Заглушка пользователя (позже заменим на AuthContext)
    private final Long currentUserId = 1L;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(formatter.format(cell.getValue().getOccurredAt())));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        accountCol.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Загрузка фильтров
        loadFilters();

        // Загрузка данных
        refreshTable();

        // События
        accountFilter.setOnAction(e -> refreshTable());
        categoryFilter.setOnAction(e -> refreshTable());
        resetBtn.setOnAction(e -> {
            accountFilter.getSelectionModel().clearSelection();
            categoryFilter.getSelectionModel().clearSelection();
            refreshTable();
        });

        backBtn.setOnAction(e -> SceneSwitcher.switchScene(e, "/ui/dashboard.fxml", "Dashboard"));

        // Удаление
        deleteBtn.setOnAction(e -> {
            Transaction selected = transactionsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a transaction to delete.").show();
                return;
            }
            transactionDao.delete(selected.getId());
            refreshTable();
        });

        // Заглушки для Add/Edit (сделаем следующим шагом)
        addBtn.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Add Window coming next!").show());
        editBtn.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Edit functionality coming soon.").show());
    }

    private void loadFilters() {
        accountFilter.setItems(FXCollections.observableArrayList(accountDao.getAll(currentUserId)));
        categoryFilter.setItems(FXCollections.observableArrayList(categoryDao.getAll(currentUserId)));
    }

    private void refreshTable() {
        List<Transaction> all = transactionDao.getAll(currentUserId);

        Account selAccount = accountFilter.getValue();
        Category selCategory = categoryFilter.getValue();

        // Фильтрация
        List<Transaction> filtered = all.stream()
                .filter(t -> selAccount == null || t.getAccountId().equals(selAccount.getId()))
                .filter(t -> selCategory == null || t.getCategoryId().equals(selCategory.getId()))
                .collect(Collectors.toList());

        transactionsTable.setItems(FXCollections.observableArrayList(filtered));

        // Подсчет итогов (просто сумма для красоты)
        BigDecimal total = filtered.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText("Total: " + total + " €");
    }
}