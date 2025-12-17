package sk.upjs.ics.spendwise.ui.util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneSwitcher {
    private static Stage stage;

    private SceneSwitcher() {
    }

    public static void setStage(Stage stage) {
        SceneSwitcher.stage = stage;
    }

    public static void switchTo(String fxmlPath) {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been set. Call setStage before switching scenes.");
        }

        URL resource = SceneSwitcher.class.getClassLoader().getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
        }

        try {
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML file: " + fxmlPath, e);
        }
    }
}
