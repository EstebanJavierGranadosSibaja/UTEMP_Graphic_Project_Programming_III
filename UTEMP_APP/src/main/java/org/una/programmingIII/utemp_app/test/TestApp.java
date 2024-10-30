package org.una.programmingIII.utemp_app.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.una.programmingIII.utemp_app.Singleton;
import org.una.programmingIII.utemp_app.manager.ScreenManager;
import org.una.programmingIII.utemp_app.test.services.AuthServiceTest;

public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScreenManager screenManager = new ScreenManager(primaryStage);
//        screenManager.loadScreen("/fxml/MapManager.fxml", "Initial View");
        screenManager.loadScreen("/View/prueba.fxml", "Initial View");

        Singleton singleton = Singleton.getInstance();
        singleton.setScreenManager(screenManager);

        // Ejecutar las pruebas de autenticación
        Platform.runLater(this::runTests);
    }

    public void runTests() {
        AuthServiceTest authServiceTest = new AuthServiceTest();
        authServiceTest.runTests();  // Llama al método de pruebas
    }

    public static void main(String[] args) {
        launch(args);
    }
}
