package sk.upjs.ics.spendwise.ui.util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SceneSwitcher {

    private static Stage primaryStage;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    // По умолчанию Английский
    private static Locale currentLocale = new Locale("en");

    public static void setStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
    }

    // Метод для смены языка
    public static void switchLanguage(Locale locale) {
        currentLocale = locale;
        // Перезагружаем текущую сцену (если нужно), но пока просто сохраняем
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
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
                    scene = new Scene(root, WIDTH, HEIGHT);
                    primaryStage.setScene(scene);
                } else {
                    scene.setRoot(root);
                }

                primaryStage.setTitle(title.isEmpty() ? "SpendWise" : "SpendWise - " + title);
                primaryStage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}