package sk.upjs.ics.spendwise.ui; // <--- Исправлено: папка ui

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.upjs.ics.spendwise.config.AppConfig;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            SceneSwitcher.setStage(primaryStage);

            // Ищем login.fxml в папке ресурсов ui
            // Если файл лежит в src/main/resources/ui/login.fxml, то путь "/ui/login.fxml"
            String path = "/ui/login.fxml";
            URL resource = getClass().getResource(path);

            if (resource == null) {
                // Запасной вариант, если лежит в корне
                path = "/login.fxml";
                resource = getClass().getResource(path);
            }

            if (resource == null) {
                throw new IllegalStateException("CRITICAL: Cannot find login.fxml! Make sure it is in src/main/resources/ui/");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("SpendWise - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        AppConfig.getInstance().close();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}