//package org.una.programmingIII.utemp_app.tests;
//
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.stage.Stage;
//import org.una.programmingIII.utemp_app.utils.view.AppContext;
//import org.una.programmingIII.utemp_app.utils.view.ViewLoader;
//import org.una.programmingIII.utemp_app.tests.services.AuthServiceTest;
//
//public class TestApp extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        ViewLoader viewManager = new ViewLoader(primaryStage);
////      viewManager.loadScreen("/fxml/MapManager.fxml", "Initial views");
//        viewManager.loadScreen("/views/Login.fxml", "Initial views");
//
//        AppContext singleton = AppContext.getInstance();
//        singleton.setViewManager(viewManager);
//
//        // Ejecutar las pruebas de autenticación
//        Platform.runLater(this::runTests);
//    }
//
//    public void runTests() {
//        AuthServiceTest authServiceTest = new AuthServiceTest();
//        authServiceTest.runTests();  // Llama al método de pruebas
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
