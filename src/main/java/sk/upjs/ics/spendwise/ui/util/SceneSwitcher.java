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

    // Этот метод нужен для старого кода (чтобы запомнить главное окно)
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    // МЕТОД 1: Для кода напарника (Login/RegisterController)
    // Они вызывают switchTo("ui/dashboard.fxml")
    public static void switchTo(String viewPath) {
        try {
            // Исправляем путь. Напарник пишет "ui/dashboard.fxml", но JavaFX ищет от корня.
            // Попробуем сформировать правильный полный путь
            String fullPath = viewPath;
            if (!viewPath.startsWith("/")) {
                // Если путь не начинается с /, предполагаем, что он лежит в scene
                // Проверь этот путь! В твоем проекте это может быть /sk/upjs/ics/spendwise/ui/scene/
                fullPath = "/sk/upjs/ics/spendwise/ui/scene/" + viewPath.replace("ui/", "");

                // Если файл называется просто "dashboard.fxml", а передали "ui/dashboard.fxml"
                if (viewPath.contains("/")) {
                    String filename = viewPath.substring(viewPath.lastIndexOf('/') + 1);
                    fullPath = "/sk/upjs/ics/spendwise/ui/scene/" + filename;
                }
            }

            URL resource = SceneSwitcher.class.getResource(fullPath);
            if (resource == null) {
                System.err.println("ERROR: Cannot find FXML file: " + fullPath + " (Original: " + viewPath + ")");
                // Пробуем последний шанс - загрузить как есть
                resource = SceneSwitcher.class.getResource("/" + viewPath);
            }

            if (resource == null) {
                throw new RuntimeException("FXML file not found: " + viewPath);
            }

            Parent root = FXMLLoader.load(resource);

            if (primaryStage != null) {
                if (primaryStage.getScene() == null) {
                    primaryStage.setScene(new Scene(root));
                } else {
                    primaryStage.getScene().setRoot(root);
                }
                primaryStage.sizeToScene();
                primaryStage.show();
            } else {
                System.err.println("Stage is null! Make sure App.java calls SceneSwitcher.setStage()");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // МЕТОД 2: Для нашего нового кода (с кнопками на Dashboard)
    public static void switchScene(Event event, String fxmlPath, String title) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();

            // Если путь начинается с /, используем его как есть
            URL resource = SceneSwitcher.class.getResource(fxmlPath);
            if (resource == null) {
                throw new RuntimeException("Cannot find FXML file: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("SpendWise - " + title);
            stage.show();

            // Обновляем сохраненный stage
            primaryStage = stage;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}