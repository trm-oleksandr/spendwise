package sk.upjs.ics.spendwise.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class DashboardController {

    @FXML
    void showAccounts(ActionEvent event) {
        // Указываем путь к файлу accounts.fxml
        // ВАЖНО: Путь должен быть правильным относительно папки resources
        // Обычно это: /sk/upjs/ics/spendwise/ui/scene/accounts.fxml
        SceneSwitcher.switchScene(event, "/ui/accounts.fxml", "Accounts");
    }

    @FXML
    void logout(ActionEvent event) {
        // Исправленный путь: ищем прямо в папке ui
        SceneSwitcher.switchScene(event, "/ui/login.fxml", "Login");
    }

    @FXML
    void showCategories(ActionEvent event) {
        // Указываем путь к нашему новому файлу
        SceneSwitcher.switchScene(event, "/ui/categories.fxml", "Manage Categories");
    }

    @FXML
    void showTransactions(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ui/transactions.fxml", "Transactions");
    }
}