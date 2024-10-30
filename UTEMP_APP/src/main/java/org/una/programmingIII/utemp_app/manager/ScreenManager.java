package org.una.programmingIII.utemp_app.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

@Setter
@Getter
public class ScreenManager {
    private Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(ScreenManager.class);

    public ScreenManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void loadScreen(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            Scene scene = new Scene(root);

            String cssFile = "/styles/GeneralCss.css";
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading screen: {}. Exception: {}", fxmlFile, e.getMessage());
        }
    }
}
