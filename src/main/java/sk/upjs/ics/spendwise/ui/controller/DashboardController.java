package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sk.upjs.ics.spendwise.entity.Account;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.service.AccountService;
import sk.upjs.ics.spendwise.service.TransactionService;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private PieChart expenseChart;
    @FXML private VBox emptyStateBox;
    @FXML private ComboBox<Account> accountSelector;

    private final TransactionService transactionService = new TransactionService();
    private final AccountService accountService = new AccountService();

    private final Long currentUserId = 1L;

    @FXML
    public void initialize() {
        setupAccountSelector();

        // Слушатель выбора
        accountSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadChartData(newVal);
            }
        });

        // Выбираем первый пункт сразу
        accountSelector.getSelectionModel().selectFirst();
    }

    private void setupAccountSelector() {
        List<Account> userAccounts = accountService.getAll(currentUserId);

        Account allAccountsOption = new Account();
        allAccountsOption.setId(-1L);
        allAccountsOption.setName("All Accounts");

        ObservableList<Account> options = FXCollections.observableArrayList();
        options.add(allAccountsOption);
        options.addAll(userAccounts);

        accountSelector.setItems(options);

        // Красивое отображение имен в списке
        accountSelector.setConverter(new StringConverter<Account>() {
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

    private void loadChartData(Account selectedAccount) {
        try {
            System.out.println("--- Loading Chart Data for: " + selectedAccount.getName() + " (ID: " + selectedAccount.getId() + ") ---");

            // 1. Берем ВСЕ транзакции
            List<Transaction> transactions = transactionService.getAll(currentUserId);
            System.out.println("Total transactions found: " + transactions.size());

            // 2. Фильтруем по счету
            if (selectedAccount.getId() != -1L) {
                transactions = transactions.stream()
                        .filter(t -> t.getAccountId().equals(selectedAccount.getId()))
                        .collect(Collectors.toList());
            }
            System.out.println("Transactions for this account: " + transactions.size());

            // 3. Оставляем ТОЛЬКО РАСХОДЫ (EXPENSE)
            List<Transaction> expensesOnly = transactions.stream()
                    .filter(t -> "EXPENSE".equals(t.getType()))
                    .toList();

            System.out.println("Expenses only: " + expensesOnly.size());

            // 4. Отображаем
            if (expensesOnly.isEmpty()) {
                System.out.println("Result: EMPTY (Showing placeholder)");
                expenseChart.setVisible(false);
                emptyStateBox.setVisible(true);
                return;
            }

            System.out.println("Result: SHOW GRAPH");
            expenseChart.setVisible(true);
            emptyStateBox.setVisible(false);

            // 5. Группируем
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

    @FXML
    void showAccounts(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ui/accounts.fxml", "Accounts");
    }

    @FXML
    void logout(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ui/login.fxml", "Login");
    }

    @FXML
    void showCategories(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ui/categories.fxml", "Manage Categories");
    }

    @FXML
    void showTransactions(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ui/transactions.fxml", "Transactions");
    }
}