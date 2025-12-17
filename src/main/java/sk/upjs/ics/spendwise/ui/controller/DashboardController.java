package sk.upjs.ics.spendwise.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sk.upjs.ics.spendwise.entity.AppUser;
import sk.upjs.ics.spendwise.security.AuthContext;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class DashboardController {

    @FXML
    private Label helloLabel;

    @FXML
    public void initialize() {
        AppUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            SceneSwitcher.switchTo("ui/login.fxml");
            return;
        }

        helloLabel.setText("Hello, " + currentUser.getUsername());
    }

    @FXML
    private void onLogout(ActionEvent event) {
        AuthContext.clear();
        SceneSwitcher.switchTo("ui/login.fxml");
    }
}
