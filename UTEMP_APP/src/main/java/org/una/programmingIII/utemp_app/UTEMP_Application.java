package org.una.programmingIII.utemp_app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.una.programmingIII.utemp_app.manager.ScreenManager;
import org.una.programmingIII.utemp_app.view_controllers.Views;

public class UTEMP_Application extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ScreenManager screenManager = new ScreenManager(primaryStage);
        screenManager.loadScreen(Views.MAIN_VIEW, "Initial views");

        Singleton singleton = Singleton.getInstance();
        singleton.setScreenManager(screenManager);

    }
}
