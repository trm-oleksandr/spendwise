package sk.upjs.ics.spendwise.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sk.upjs.ics.spendwise.dao.UserDao;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.factory.DaoFactory;
import sk.upjs.ics.spendwise.factory.JdbcDaoFactory;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.security.PasswordHasher;
import sk.upjs.ics.spendwise.ui.util.Alerts;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    private final UserDao userDao;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public RegisterController() {
        DaoFactory daoFactory = JdbcDaoFactory.getInstance();
        this.userDao = daoFactory.userDao();
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            Alerts.error("Registration failed", "Username and password are required.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            Alerts.error("Registration failed", "Passwords do not match.");
            return;
        }

        if (userDao.existsByUsername(username)) {
            Alerts.error("Registration failed", "Username already exists.");
            return;
        }

        String passwordHash = passwordHasher.hash(password);
        AppUser createdUser = userDao.create(username, passwordHash);
        AuthContext.setCurrentUser(createdUser);
        SceneSwitcher.switchTo("ui/dashboard.fxml");
    }

    @FXML
    private void onBackToLogin(ActionEvent event) {
        SceneSwitcher.switchTo("ui/login.fxml");
    }
}
