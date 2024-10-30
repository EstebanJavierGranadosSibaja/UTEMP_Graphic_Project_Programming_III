package org.una.programmingIII.utemp_app.view_controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter;
import org.una.programmingIII.utemp_app.Singleton;
import org.una.programmingIII.utemp_app.services.AuthService;
import org.una.programmingIII.utemp_app.services.request.AuthRequest;
import org.una.programmingIII.utemp_app.services.responses.MessageResponse;

public class LoginController {
    @FXML
    private MFXTextField numberIdentificationTxf;
    @FXML
    private MFXPasswordField passwordPwf;
    @FXML
    private MFXButton loginBtn;
    @FXML
    private MFXButton helpBtn;
    @FXML
    private MFXButton infoBtn;

    private final AuthService authService = new AuthService();
    private final Singleton singleton = Singleton.getInstance();

    @FXML
    public void initialize() {
        // Solo permitir letras en el campo de identificación
//        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
//            String newText = change.getControlNewText();
//            if (newText.matches("[a-zA-Z]*")) { // Aceptar solo letras
//                return change;
//            }
//            return null; // Rechazar cambios no válidos
//        });
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Aceptar solo números
                return change;
            }
            return null; // Rechazar cambios no válidos
        });
        numberIdentificationTxf.setTextFormatter(textFormatter);

        // Configurar el botón de inicio de sesión
        loginBtn.setOnAction(event -> handleLogin());

        // Configurar botones adicionales
        helpBtn.setOnAction(event -> showHelp());
        infoBtn.setOnAction(event -> showInfo());
    }

    private void handleLogin() {
        String numberId = numberIdentificationTxf.getText();
        String password = passwordPwf.getText();

        // Validar campos vacíos
        if (numberId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter your identification number and password.");
            return;
        }

        // Lógica de autenticación
        MessageResponse<String> response = authService.login(new AuthRequest(numberId, password));

        if (response.isSuccess()) {
            showAlert("Exitoso", "Bienvenido: usuario ");
            singleton.getScreenManager().loadScreen(Views.MENU, "MyApplication");
        } else {
            showAlert(response.getMessage(), response.getErrorCode());
        }
    }

    private void showHelp() {
        showAlert("Help", "This is the help section.");
    }

    private void showInfo() {
        showAlert("Info", "This is some information about the app.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
