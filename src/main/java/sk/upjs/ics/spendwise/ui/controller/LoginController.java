package sk.upjs.ics.spendwise.ui.controller;

import java.util.Optional;
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

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final UserDao userDao;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public LoginController() {
        DaoFactory daoFactory = JdbcDaoFactory.getInstance();
        this.userDao = daoFactory.userDao();
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            Alerts.error("Login failed", "Username and password are required.");
            return;
        }

        Optional<AppUser> userOptional = userDao.findByUsername(username);
        if (userOptional.isPresent() && passwordHasher.verify(password, userOptional.get().getPasswordHash())) {
            AuthContext.setCurrentUser(userOptional.get());
            SceneSwitcher.switchTo("ui/dashboard.fxml");
        } else {
            Alerts.error("Login failed", "Invalid username or password.");
        }
    }

    @FXML
    private void onGoToRegister(ActionEvent event) {
        SceneSwitcher.switchTo("ui/register.fxml");
    }
}
