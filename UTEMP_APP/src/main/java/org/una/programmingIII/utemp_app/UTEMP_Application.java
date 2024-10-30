package org.una.programmingIII.utemp_app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.una.programmingIII.utemp_app.manager.ScreenManager;

public class UTEMP_Application extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ScreenManager screenManager = new ScreenManager(primaryStage);
//        screenManager.loadScreen("/fxml/MapManager.fxml", "Initial views");
        screenManager.loadScreen("/views/Login.fxml", "Initial views");

        Singleton singleton = Singleton.getInstance();
        singleton.setScreenManager(screenManager);

    }
}
