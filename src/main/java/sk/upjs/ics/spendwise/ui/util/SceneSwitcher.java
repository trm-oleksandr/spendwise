package sk.upjs.ics.spendwise.ui.util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneSwitcher {

    private static Stage primaryStage;

    // ЖЕСТКИЙ РАЗМЕР (Стандарт для всего приложения)
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    public static void setStage(Stage stage) {
        primaryStage = stage;
        // Устанавливаем размер сразу при старте
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        // Запрещаем делать окно слишком маленьким
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
    }

    // Метод 1: Переход по кнопке (из контроллера)
    public static void switchScene(Event event, String fxmlPath, String title) {
        switchInternal(fxmlPath, title);
    }

    // Метод 2: Переход без события (для Login/Register)
    public static void switchTo(String viewPath) {
        String path = viewPath.startsWith("/") ? viewPath : "/" + viewPath;
        // Если забыли /ui/, добавляем
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
                // Пытаемся найти файл, если путь неточный
                String filename = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
                resource = SceneSwitcher.class.getResource("/ui/" + filename);
            }

            if (resource == null) {
                throw new RuntimeException("CRITICAL ERROR: FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            if (primaryStage != null) {
                Scene scene = primaryStage.getScene();
                if (scene == null) {
                    scene = new Scene(root, WIDTH, HEIGHT);
                    primaryStage.setScene(scene);
                } else {
                    // Просто меняем содержимое (root), размер окна остается старым!
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