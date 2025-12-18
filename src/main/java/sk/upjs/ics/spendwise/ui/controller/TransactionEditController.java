package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.time.ZoneId;

public class TransactionEditController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Account> accountComboBox;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField amountField;
    @FXML private TextArea noteArea;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final TransactionDao transactionDao = JdbcDaoFactory.INSTANCE.transactionDao();
    private final AccountDao accountDao = JdbcDaoFactory.INSTANCE.accountDao();
    private final CategoryDao categoryDao = JdbcDaoFactory.INSTANCE.categoryDao();

    @FXML
    public void initialize() {
        accountComboBox.setItems(FXCollections.observableArrayList(accountDao.getAll(getCurrentUserId())));
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryDao.getAll(getCurrentUserId())));
        datePicker.setValue(LocalDate.now());

        cancelBtn.setOnAction(e -> SceneSwitcher.switchScene(e, "/ui/transactions.fxml", "Transactions"));

        saveBtn.setOnAction(e -> {
            try {
                LocalDate date = datePicker.getValue();
                Account account = accountComboBox.getValue();
                Category category = categoryComboBox.getValue();
                String amountStr = amountField.getText().replace(",", ".");

                if (date == null || account == null || category == null || amountStr.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                    return;
                }

                Transaction t = new Transaction();
                t.setUserId(getCurrentUserId());
                t.setAccountId(account.getId());
                t.setCategoryId(category.getId());
                t.setAmount(new BigDecimal(amountStr));
                t.setNote(noteArea.getText());
                t.setOccurredAt(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

                transactionDao.save(t);

                SceneSwitcher.switchScene(e, "/ui/transactions.fxml", "Transactions");
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
            }
        });
    }

    private Long getCurrentUserId() {
        if (AuthContext.getCurrentUser() == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return AuthContext.getCurrentUser().getId();
    }
}