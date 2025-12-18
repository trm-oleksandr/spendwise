package sk.upjs.ics.spendwise.ui.util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.upjs.ics.spendwise.security.AuthContext;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SceneSwitcher {

    private static Stage primaryStage;
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 650;
    private static final int AUTH_MIN_WIDTH = 520;
    private static final int AUTH_MIN_HEIGHT = 520;

    // По умолчанию Английский
    private static Locale currentLocale = new Locale("en");

    public static void setStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setResizable(true);
    }

    // Метод для смены языка
    public static void switchLanguage(Locale locale) {
        currentLocale = locale;
        // Перезагружаем текущую сцену (если нужно), но пока просто сохраняем
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static Scene getScene() {
        return primaryStage == null ? null : primaryStage.getScene();
    }

    public static void switchScene(Event event, String fxmlPath, String title) {
        switchInternal(fxmlPath, title);
    }

    public static void switchTo(String viewPath) {
        String path = viewPath.startsWith("/") ? viewPath : "/" + viewPath;
        if (!path.contains("/ui/")) {
            String filename = path.substring(path.lastIndexOf('/') + 1);
            path = "/ui/" + filename;
        }
        switchInternal(path, "SpendWise");
    }

    private static void switchInternal(String fxmlPath, String title) {
        try {
            if (requiresAuthentication(fxmlPath) && AuthContext.getCurrentUser() == null) {
                fxmlPath = "/ui/login.fxml";
                title = "Login";
            }

            boolean isAuthScene = fxmlPath.toLowerCase(Locale.ROOT).contains("login.fxml")
                || fxmlPath.toLowerCase(Locale.ROOT).contains("register.fxml");

            URL resource = SceneSwitcher.class.getResource(fxmlPath);
            if (resource == null) {
                String filename = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
                resource = SceneSwitcher.class.getResource("/ui/" + filename);
            }

            if (resource == null) {
                throw new RuntimeException("CRITICAL ERROR: FXML not found: " + fxmlPath);
            }

            // ИЗМЕНЕНИЕ ЗДЕСЬ: Добавили new Utf8Control()
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", currentLocale, new Utf8Control());

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setResources(bundle);

            Parent root = loader.load();

            if (primaryStage != null) {
                Scene scene = primaryStage.getScene();
                if (scene == null) {
                    scene = new Scene(root);
                    primaryStage.setScene(scene);
                } else {
                    scene.setRoot(root);
                }

                ThemeManager.applyTheme(scene);

                updateStageSize(root, isAuthScene);

                primaryStage.setTitle(title.isEmpty() ? "SpendWise" : "SpendWise - " + title);
                primaryStage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean requiresAuthentication(String fxmlPath) {
        String normalized = fxmlPath.toLowerCase(Locale.ROOT);
        return !(normalized.contains("login.fxml") || normalized.contains("register.fxml"));
    }

    private static void updateStageSize(Parent root, boolean isAuthScene) {
        if (primaryStage == null) {
            return;
        }

        if (isAuthScene) {
            double minWidth = root.prefWidth(-1);
            double minHeight = root.prefHeight(-1);

            if (Double.isNaN(minWidth) || minWidth <= 0) {
                minWidth = AUTH_MIN_WIDTH;
            }
            if (Double.isNaN(minHeight) || minHeight <= 0) {
                minHeight = AUTH_MIN_HEIGHT;
            }

            primaryStage.setMinWidth(minWidth);
            primaryStage.setMinHeight(minHeight);
        } else {
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
        }

        primaryStage.sizeToScene();
    }
}