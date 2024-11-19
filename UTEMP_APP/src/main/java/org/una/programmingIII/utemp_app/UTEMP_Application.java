package org.una.programmingIII.utemp_app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/University.png")));
        primaryStage.getIcons().add(icon);

        ViewManager viewManager = ViewManager.getInstance();
        viewManager.initialize(primaryStage);
        viewManager.showMainView(Views.LOGIN);
    }
}