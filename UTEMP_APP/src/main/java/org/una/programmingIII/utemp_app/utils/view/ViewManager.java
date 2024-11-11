package org.una.programmingIII.utemp_app.utils.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.una.programmingIII.utemp_app.UTEMP_Application;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.controllers.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

@Getter
@Setter
public class ViewManager {
    private static ViewManager instance; // Singleton instance
    private final HashMap<String, FXMLLoader> loaders = new HashMap<>(); // Loaders for views
    private Stage mainStage; // Main application window
    private ResourceBundle languageBundle; // Resource bundle for internationalization
    private NotificationToast notificationToast;

    // Private constructor for Singleton pattern
    private ViewManager() {
    }

    public static ViewManager getInstance() {
        return Holder.INSTANCE;
    }

    // Initialize the ViewManager
    public void initialize(Stage stage) {
        initialize(stage, null);
        notificationToast = new NotificationToast();
    }

    public void initialize(Stage stage, ResourceBundle language) {
        this.mainStage = stage;
        this.languageBundle = language;
        mainStage.setOnCloseRequest(event -> {
            event.consume();
            closeApplication(); // Call the close application method
        });
    }

    private void closeApplication() {
        if (mainStage != null) {
            mainStage.close(); // Close the Stage
        }
    }

    // Load the FXML for a view
    private FXMLLoader getLoader(String viewName) {
        FXMLLoader loader = new FXMLLoader(UTEMP_Application.class.getResource("/views/" + viewName + ".fxml"));
        if (languageBundle != null) {
            loader.setResources(languageBundle);
        }
        try {
            loader.load(); // Load the view
        } catch (IOException e) {
            handleError("Error loading FXML for view: " + viewName, e);
        }
        return loader;
    }

    public void showMainView(String viewName, String internal) {//ventan principal no contenida dentro de otro fxml
        showMainView(viewName);
        loadInternalView(internal);
    }

    public void showModalView(String viewName) {
        try {
            FXMLLoader loader = getLoader(viewName);

            Scene modalScene = new Scene(loader.getRoot(), 850, 730);
            applyCSS(modalScene);

            Stage modalStage = new Stage();
            modalStage.initOwner(mainStage);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(modalScene);

            modalStage.setWidth(1000);
            modalStage.setHeight(800);
            modalStage.setResizable(false);

            modalStage.showAndWait();
        } catch (Exception e) {
            handleError("Error showing modal view: " + viewName, e);
            e.printStackTrace(); // Para obtener más detalles del error
        }
    }


    public void showMainView(String viewName) {
        try {
            System.out.println("Attempting to show main view: " + viewName);
            FXMLLoader loader = getLoader(viewName);
            Scene scene = new Scene(loader.getRoot());

            // Cargar y aplicar CSS
            applyCSS(scene);

            // Definir el tamaño máximo de la ventana
            double maxWidth = 1920;  // Ancho máximo en píxeles
            double maxHeight = 1080; // Alto máximo en píxeles

            // Definir el tamaño mínimo como el 65% del tamaño máximo
            double minWidth = maxWidth * 0.55;
            double minHeight = maxHeight * 0.85;

            // Establecer el tamaño máximo y mínimo
            mainStage.setMaxWidth(maxWidth);
            mainStage.setMaxHeight(maxHeight);
            mainStage.setMinWidth(minWidth);
            mainStage.setMinHeight(minHeight);

            // Establecer el tamaño preferido igual al mínimo
            mainStage.setWidth(minWidth);
            mainStage.setHeight(minHeight);

            // Opcional: Ajustar el tamaño preferido en la escena, si es necesario
            scene.getRoot().prefWidth(minWidth);
            scene.getRoot().prefHeight(minHeight);

            // Asegúrate de actualizar la escena
            mainStage.setScene(scene);
            mainStage.show();

        } catch (Exception e) {
            handleError("Error showing main view: " + viewName, e);
        }
    }


    public void loadInternalView(String viewName) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();

        if (controller != null) {
            controller.initialize();
        }

        try {
            if (mainStage != null && mainStage.getScene() != null) {
                BorderPane root = (BorderPane) mainStage.getScene().getRoot();
                VBox targetContainer = (VBox) root.getCenter();

                if (targetContainer != null) {
                    targetContainer.getChildren().clear();
                    targetContainer.getChildren().add(loader.getRoot());
                    applyCSS(targetContainer.getScene());
                } else {
                    handleError("Target container is null.", null);
                }
            } else {
                handleError("Main stage or its scene is not initialized.", null);
            }
        } catch (Exception e) {
            handleError("Error loading internal view: " + viewName, e);
        }
    }


    private void applyCSS(Scene scene) {
        String css = Objects.requireNonNull(getClass().getResource(BaseConfig.GENERAL_CSS)).toExternalForm();
        scene.getStylesheets().add(css);
    }

    private void handleError(String message, Exception e) {
        System.err.println(message);
        if (e != null) {
            System.err.println(e.getMessage());//no modificar
        }
    }

    // Holder pattern for lazy initialization
    private static class Holder {
        private static final ViewManager INSTANCE = new ViewManager();
    }

    public void createNotification(String title, String message) {
        notificationToast.setMessage(message);
        notificationToast.setTitle(title);
        notificationToast.start(mainStage);
    }
}
