package sk.upjs.ics.spendwise.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import sk.upjs.ics.spendwise.dao.TransactionDao;
import sk.upjs.ics.spendwise.entity.Transaction;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private PieChart expenseChart; // Наш график

    private final TransactionDao transactionDao = JdbcDaoFactory.INSTANCE.transactionDao();
    private final Long currentUserId = 1L;

    @FXML
    public void initialize() {
        loadChartData();
    }

    private void loadChartData() {
        try {
            List<Transaction> transactions = transactionDao.getAll(currentUserId);

            // Группируем транзакции по названию категории и суммируем
            Map<String, BigDecimal> expensesByCategory = transactions.stream()
                    .filter(t -> t.getCategoryName() != null) // только если есть категория
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
            expenseChart.setLabelsVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load chart data");
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