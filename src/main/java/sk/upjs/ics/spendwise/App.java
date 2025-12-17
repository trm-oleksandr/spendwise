package sk.upjs.ics.spendwise;

import javafx.application.Application;
import javafx.stage.Stage;
import sk.upjs.ics.spendwise.config.AppConfig;
import sk.upjs.ics.spendwise.ui.util.SceneSwitcher;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SpendWise");
        SceneSwitcher.setStage(primaryStage);
        SceneSwitcher.switchTo("ui/login.fxml");
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
