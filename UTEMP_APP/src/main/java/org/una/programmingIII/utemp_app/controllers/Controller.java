package org.una.programmingIII.utemp_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.Objects;

@Getter
@Setter
public abstract class Controller {
    protected Stage stage;
    protected MessageResponse<Void> responseVoid = new MessageResponse<>();
    protected AppContext appContext;
    protected int pageNumber = 1, maxPage = 1;

    protected String message;

    public Controller() {
    }

    @FXML
    public abstract void initialize();

    protected void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showNotificationToast(String title, String message, Alert.AlertType alertType) {
        ViewManager.getInstance().createNotification(String.valueOf(alertType), message);
    }

    protected boolean showConfirmationMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED); // Esto elimina la barra de título

        // Obtener los botones y asignarles IDs
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);

        // Asignar un ID para los botones en CSS
        cancelButton.setId("cancelButton");
        okButton.setId("okButton");


        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/alert.css")).toExternalForm());

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    protected void handleError(String message) {
        System.err.println(message);
        showNotificationToast("Error", message, Alert.AlertType.ERROR);
    }

    protected void showNotificationToast(String title, String message) {
        showNotificationToast(title, message, Alert.AlertType.INFORMATION);
    }

    protected void showNotificationToast(String message) {
        showNotificationToast(null, message, Alert.AlertType.INFORMATION);
    }

    protected TextFormatter<String> createTextFormatter(String regex) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches(regex) ? change : null;
        });
    }

    // Formateadores
    protected TextFormatter<String> textFormatterOnlyNumbers() {
        return createTextFormatter("\\d*");
    }

    protected TextFormatter<String> textFormatterOnlyLetters() {
        return createTextFormatter("[a-zA-Z ]*"); // Permite espacios
    }

    protected void showReadResponse(MessageResponse<?> response) {
        if (response.isSuccess()) {
            showNotificationToast("Éxito", "Operación completada con éxito.", Alert.AlertType.INFORMATION);
        } else {
            handleError("Error: " + response.getErrorMessage());
        }
    }

    protected void showError(String errorInfo) {
        showNotificationToast("Error", errorInfo, Alert.AlertType.ERROR);
    }
}
//        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/interrogation_icon.png")));
//        ImageView iconView = new ImageView(icon);
//        alert.getDialogPane().setGraphic(iconView);