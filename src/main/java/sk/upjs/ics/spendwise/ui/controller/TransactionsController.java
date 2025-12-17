package sk.upjs.ics.spendwise.ui.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.factory.DaoFactory;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.controller.TransactionEditController;
import sk.upjs.ics.spendwise.ui.model.TransactionRow;
import sk.upjs.ics.spendwise.ui.util.Alerts;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class TransactionsController {

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private ComboBox<Account> accountFilterBox;

    @FXML
    private ComboBox<Category> categoryFilterBox;

    @FXML
    private ComboBox<String> typeFilterBox;

    @FXML
    private TableView<TransactionRow> transactionsTable;

    @FXML
    private TableColumn<TransactionRow, Instant> dateCol;

    @FXML
    private TableColumn<TransactionRow, String> accountCol;

    @FXML
    private TableColumn<TransactionRow, String> categoryCol;

    @FXML
    private TableColumn<TransactionRow, String> typeCol;

    @FXML
    private TableColumn<TransactionRow, java.math.BigDecimal> amountCol;

    @FXML
    private TableColumn<TransactionRow, String> noteCol;

    private final AccountDao accountDao;
    private final CategoryDao categoryDao;
    private final TransactionDao transactionDao;

    public TransactionsController() {
        DaoFactory daoFactory = JdbcDaoFactory.getInstance();
        this.accountDao = daoFactory.accountDao();
        this.categoryDao = daoFactory.categoryDao();
        this.transactionDao = daoFactory.transactionDao();
    }

    @FXML
    public void initialize() {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        typeFilterBox.setItems(FXCollections.observableArrayList("ALL", "INCOME", "EXPENSE"));
        typeFilterBox.getSelectionModel().select("ALL");

        List<Account> accounts = accountDao.findAll(currentUser.getId());
        accountFilterBox.setItems(FXCollections.observableArrayList(accounts));

        List<Category> categories = categoryDao.findAll(currentUser.getId());
        categoryFilterBox.setItems(FXCollections.observableArrayList(categories));

        dateCol.setCellValueFactory(new PropertyValueFactory<>("occurredAt"));
        accountCol.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        setDefaultMonthIfEmpty();

        refreshTable();
    }

    @FXML
    private void onApplyFilters(ActionEvent event) {
        refreshTable();
    }

    @FXML
    private void onAdd(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        openTransactionEditor(TransactionEditController::initCreate);
        refreshTable();
    }

    @FXML
    private void onEdit(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        TransactionRow selected = transactionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("No selection", "Please select a transaction to edit.");
            return;
        }

        openTransactionEditor(controller -> controller.initEdit(selected.getId()));
        refreshTable();
    }

    @FXML
    private void onDelete(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        TransactionRow selected = transactionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("No selection", "Please select a transaction to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete transaction");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected transaction?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            transactionDao.delete(currentUser.getId(), selected.getId());
            refreshTable();
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        SceneSwitcher.switchTo("ui/dashboard.fxml");
    }

    private void refreshTable() {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        setDefaultMonthIfEmpty();

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        YearMonth month = YearMonth.from(fromDate);
        if (toDate != null && !YearMonth.from(toDate).equals(month)) {
            // Temporary limitation until TransactionDao supports periods
        }

        Account selectedAccount = accountFilterBox.getValue();
        Category selectedCategory = categoryFilterBox.getValue();

        Long accountId = selectedAccount != null ? selectedAccount.getId() : null;
        Long categoryId = selectedCategory != null ? selectedCategory.getId() : null;

        String selectedType = typeFilterBox.getValue();
        CategoryType categoryType = "ALL".equals(selectedType) ? null : CategoryType.valueOf(selectedType);

        List<Transaction> transactions = transactionDao.findByMonth(
                currentUser.getId(),
                month,
                accountId,
                categoryId,
                categoryType
        );

        Map<Long, String> accountNames = new HashMap<>();
        for (Account account : accountDao.findAll(currentUser.getId())) {
            accountNames.put(account.getId(), account.getName());
        }

        Map<Long, Category> categories = new HashMap<>();
        for (Category category : categoryDao.findAll(currentUser.getId())) {
            categories.put(category.getId(), category);
        }

        List<TransactionRow> rows = transactions.stream()
                .map(t -> {
                    Category category = categories.get(t.getCategoryId());
                    String categoryName = category != null
                            ? category.getName() + " (" + category.getType() + ")"
                            : "-";
                    String typeLabel = category != null ? category.getType().name() : "-";
                    return new TransactionRow(
                            t.getId(),
                            t.getOccurredAt(),
                            accountNames.getOrDefault(t.getAccountId(), "-"),
                            categoryName,
                            typeLabel,
                            t.getAmount(),
                            t.getNote()
                    );
                })
                .toList();

        transactionsTable.setItems(FXCollections.observableArrayList(rows));
    }

    private void setDefaultMonthIfEmpty() {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);

        if (fromDatePicker.getValue() == null) {
            fromDatePicker.setValue(currentMonth.atDay(1));
        }
        if (toDatePicker.getValue() == null) {
            toDatePicker.setValue(currentMonth.atEndOfMonth());
        }
    }

    private void openTransactionEditor(Consumer<TransactionEditController> initializer) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/transaction_edit.fxml"));

        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            TransactionEditController controller = loader.getController();
            controller.setStage(stage);
            initializer.accept(controller);

            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Unable to open transaction editor", e);
        }
    }
}
