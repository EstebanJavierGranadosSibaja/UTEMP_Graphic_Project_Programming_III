package org.una.programmingIII.utemp_app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.Objects;

public class UTEMP_Application extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UTEMP Application");

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/University.png"))); // Asegúrate de que el ícono esté en esta ruta
        primaryStage.getIcons().add(icon);

        // Inicializar y mostrar la vista principal
        ViewManager viewManager = ViewManager.getInstance();
        viewManager.initialize(primaryStage);
        viewManager.showMainView(Views.LOGIN);
    }
}
