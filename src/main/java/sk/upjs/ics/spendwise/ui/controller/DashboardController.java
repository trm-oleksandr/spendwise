package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.CategoryType;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.service.AccountService;
import sk.upjs.ics.spendwise.service.TransactionService;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;
import sk.upjs.ics.spendwise.ui.util.ThemeManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private PieChart expenseChart;
    @FXML private VBox emptyStateBox;
    @FXML private ComboBox<Account> accountSelector;

    @FXML private Button langEnBtn;
    @FXML private Button langSkBtn;
    @FXML private Button themeToggleBtn;

    private final TransactionService transactionService = new TransactionService();
    private final AccountService accountService = new AccountService();
    private ResourceBundle resources;

    @FXML
    public void initialize() {
        resources = ResourceBundle.getBundle("i18n/messages", SceneSwitcher.getCurrentLocale(), new sk.upjs.ics.spendwise.ui.util.Utf8Control());

        // 1. Обновляем вид кнопок (подсветка активной)
        updateLanguageButtons();
        updateThemeToggle();

        setupAccountSelector();

        accountSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadChartData(newVal);
            }
        });

        accountSelector.getSelectionModel().selectFirst();
    }

    // --- ЛОГИКА ПОДСВЕТКИ КНОПОК ---
    private void updateLanguageButtons() {
        Locale current = SceneSwitcher.getCurrentLocale();

        // Сбрасываем стили (удаляем active класс)
        langEnBtn.getStyleClass().remove("lang-button-active");
        langSkBtn.getStyleClass().remove("lang-button-active");

        // Добавляем active класс нужной кнопке
        if (current.getLanguage().equals("sk")) {
            langSkBtn.getStyleClass().add("lang-button-active");
        } else {
            langEnBtn.getStyleClass().add("lang-button-active");
        }
    }

    @FXML
    void setLangEn(ActionEvent event) {
        changeLanguage(new Locale("en"), event);
    }

    @FXML
    void setLangSk(ActionEvent event) {
        changeLanguage(new Locale("sk"), event);
    }

    @FXML
    void toggleTheme(ActionEvent event) {
        ThemeManager.toggleTheme();
        SceneSwitcher.switchScene(event, "/ui/dashboard.fxml", "Dashboard");
    }

    private void changeLanguage(Locale locale, ActionEvent event) {
        // Если язык уже выбран, ничего не делаем (чтобы не моргало)
        if (SceneSwitcher.getCurrentLocale().getLanguage().equals(locale.getLanguage())) {
            return;
        }

        SceneSwitcher.switchLanguage(locale);
        SceneSwitcher.switchScene(event, "/ui/dashboard.fxml", "Dashboard");
    }
    // --------------------------------

    private void updateThemeToggle() {
        if (ThemeManager.getActiveTheme() == ThemeManager.Theme.DARK) {
            themeToggleBtn.setText("🌙");
            themeToggleBtn.setTooltip(new javafx.scene.control.Tooltip(resources.getString("theme.dark")));
        } else {
            themeToggleBtn.setText("☀️");
            themeToggleBtn.setTooltip(new javafx.scene.control.Tooltip(resources.getString("theme.light")));
        }
    }

    private void setupAccountSelector() {
        List<Account> userAccounts = accountService.getAll(getCurrentUserId());
        Account allAccountsOption = new Account();
        allAccountsOption.setId(-1L);
        allAccountsOption.setName(resources.getString("dashboard.all_accounts"));

        ObservableList<Account> options = FXCollections.observableArrayList();
        options.add(allAccountsOption);
        options.addAll(userAccounts);

        accountSelector.setItems(options);
        accountSelector.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account account) {
                return account == null ? "" : account.getName();
            }
            @Override
            public Account fromString(String string) { return null; }
        });
    }

    private void loadChartData(Account selectedAccount) {
        try {
            List<Transaction> transactions = transactionService.getAll(getCurrentUserId());

            if (selectedAccount.getId() != -1L) {
                transactions = transactions.stream()
                        .filter(t -> t.getAccountId().equals(selectedAccount.getId()))
                        .collect(Collectors.toList());
            }

            List<Transaction> expensesOnly = transactions.stream()
                    .filter(t -> t.getType() == CategoryType.EXPENSE)
                    .toList();

            if (expensesOnly.isEmpty()) {
                expenseChart.setVisible(false);
                emptyStateBox.setVisible(true);
                return;
            }

            expenseChart.setVisible(true);
            emptyStateBox.setVisible(false);

            Map<String, BigDecimal> expensesByCategory = expensesOnly.stream()
                    .filter(t -> t.getCategoryName() != null)
                    .collect(Collectors.toMap(
                            Transaction::getCategoryName,
                            Transaction::getAmount,
                            BigDecimal::add
                    ));

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map.Entry<String, BigDecimal> entry : expensesByCategory.entrySet()) {
                pieData.add(new PieChart.Data(entry.getKey(), entry.getValue().doubleValue()));
            }
            expenseChart.setData(pieData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML void showAccounts(ActionEvent event) { SceneSwitcher.switchScene(event, "/ui/accounts.fxml", "Accounts"); }
    @FXML void logout(ActionEvent event) { AuthContext.clear(); SceneSwitcher.switchScene(event, "/ui/login.fxml", "Login"); }
    @FXML void showCategories(ActionEvent event) { SceneSwitcher.switchScene(event, "/ui/categories.fxml", "Manage Categories"); }
    @FXML void showTransactions(ActionEvent event) { SceneSwitcher.switchScene(event, "/ui/transactions.fxml", "Transactions");}
    @FXML void showBudgets(ActionEvent event) { SceneSwitcher.switchScene(event, "/ui/budgets.fxml", "Budgets"); }

    private Long getCurrentUserId() {
        if (AuthContext.getCurrentUser() == null) {
            throw new IllegalStateException("No authenticated user in context");
        }
        return AuthContext.getCurrentUser().getId();
    }
}
