package sk.upjs.ics.spendwise.ui.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sk.upjs.ics.spendwise.dao.AccountDao;
import sk.upjs.ics.spendwise.dao.CategoryDao;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.entity.Category;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.factory.DaoFactory;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.util.Alerts;

public class TransactionEditController {

    private enum Mode {
        CREATE,
        EDIT
    }

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Account> accountBox;

    @FXML
    private ComboBox<Category> categoryBox;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea noteArea;

    private final AccountDao accountDao;
    private final CategoryDao categoryDao;
    private final TransactionDao transactionDao;

    private Stage stage;
    private Mode mode = Mode.CREATE;
    private Transaction existingTransaction;

    public TransactionEditController() {
        DaoFactory daoFactory = JdbcDaoFactory.getInstance();
        this.accountDao = daoFactory.accountDao();
        this.categoryDao = daoFactory.categoryDao();
        this.transactionDao = daoFactory.transactionDao();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            Alerts.error("Not authenticated", "Please log in to manage transactions.");
            closeWindow();
            return;
        }

        setupConverters();
        loadAccountsAndCategories(currentUser.getId());
        datePicker.setValue(LocalDate.now());
    }

    public void initCreate() {
        this.mode = Mode.CREATE;
        this.existingTransaction = null;
    }

    public void initEdit(long txnId) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            Alerts.error("Not authenticated", "Please log in to manage transactions.");
            closeWindow();
            return;
        }

        Optional<Transaction> transactionOpt = transactionDao.findById(currentUser.getId(), txnId);
        if (transactionOpt.isEmpty()) {
            Alerts.error("Not found", "Transaction could not be loaded.");
            closeWindow();
            return;
        }

        this.mode = Mode.EDIT;
        this.existingTransaction = transactionOpt.get();

        selectAccount(existingTransaction.getAccountId());
        selectCategory(existingTransaction.getCategoryId());
        datePicker.setValue(LocalDate.ofInstant(existingTransaction.getOccurredAt(), ZoneId.systemDefault()));
        amountField.setText(existingTransaction.getAmount().toPlainString());
        noteArea.setText(existingTransaction.getNote());
    }

    @FXML
    private void onSave(ActionEvent event) {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            Alerts.error("Not authenticated", "Please log in to manage transactions.");
            closeWindow();
            return;
        }

        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            Alerts.error("Validation error", "Date is required.");
            return;
        }

        Account selectedAccount = accountBox.getValue();
        if (selectedAccount == null) {
            Alerts.error("Validation error", "Account is required.");
            return;
        }

        Category selectedCategory = categoryBox.getValue();
        if (selectedCategory == null) {
            Alerts.error("Validation error", "Category is required.");
            return;
        }

        String amountText = amountField.getText();
        if (amountText == null || amountText.isBlank()) {
            Alerts.error("Validation error", "Amount is required.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim());
        } catch (Exception e) {
            Alerts.error("Validation error", "Amount must be a valid number.");
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            Alerts.error("Validation error", "Amount must be greater than zero.");
            return;
        }

        Instant occurredAt = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Transaction transaction = existingTransaction != null ? existingTransaction : new Transaction();
        transaction.setUserId(currentUser.getId());
        transaction.setAccountId(selectedAccount.getId());
        transaction.setCategoryId(selectedCategory.getId());
        transaction.setAmount(amount);
        transaction.setOccurredAt(occurredAt);
        transaction.setNote(noteArea.getText());

        if (mode == Mode.CREATE) {
            transactionDao.create(transaction);
        } else {
            transactionDao.update(transaction);
        }

        closeWindow(event.getSource());
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow(event.getSource());
    }

    private void loadAccountsAndCategories(long userId) {
        List<Account> accounts = accountDao.findAll(userId);
        accountBox.setItems(FXCollections.observableArrayList(accounts));

        List<Category> categories = categoryDao.findAll(userId);
        categoryBox.setItems(FXCollections.observableArrayList(categories));
    }

    private void setupConverters() {
        accountBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Account account) {
                return account == null ? "" : account.getName();
            }

            @Override
            public Account fromString(String string) {
                return null;
            }
        });

        categoryBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                if (category == null) {
                    return "";
                }
                return category.getName() + " (" + category.getType() + ")";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
    }

    private void selectAccount(long accountId) {
        for (Account account : accountBox.getItems()) {
            if (account.getId() == accountId) {
                accountBox.getSelectionModel().select(account);
                break;
            }
        }
    }

    private void selectCategory(long categoryId) {
        for (Category category : categoryBox.getItems()) {
            if (category.getId() == categoryId) {
                categoryBox.getSelectionModel().select(category);
                break;
            }
        }
    }

    private void closeWindow() {
        closeWindow(null);
    }

    private void closeWindow(Object eventSource) {
        if (stage != null) {
            stage.close();
            return;
        }

        Node node = null;
        if (eventSource instanceof Node sourceNode) {
            node = sourceNode;
        } else if (accountBox != null) {
            node = accountBox;
        }

        if (node != null && node.getScene() != null && node.getScene().getWindow() != null) {
            node.getScene().getWindow().hide();
        }
    }
}
