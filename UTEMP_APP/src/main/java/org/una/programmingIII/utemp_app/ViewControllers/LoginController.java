package org.una.programmingIII.utemp_app.ViewControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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

    @FXML
    public void initialize() {
        // Configurar el botón de inicio de sesión para que maneje el clic
        loginBtn.setOnAction(event -> handleLogin());

        // Configurar botones adicionales (help, info) si es necesario
        helpBtn.setOnAction(event -> showHelp());
        infoBtn.setOnAction(event -> showInfo());
    }

    private void handleLogin() {
        String numberId = numberIdentificationTxf.getText();
        String password = passwordPwf.getText();

        // Aquí debes implementar la lógica para autenticar al usuario.
        // Este es un ejemplo simple.
        if (numberId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter your identification number and password.");
        } else if (authenticateUser(numberId, password)) {
            showAlert("Success", "Login successful!");
            // Redirigir a la siguiente vista o realizar acción adicional
        } else {
            showAlert("Error", "Invalid identification number or password.");
        }
    }

    private boolean authenticateUser(String numberId, String password) {
        // Implementa aquí la lógica para autenticar al usuario, por ejemplo, comprobando con una base de datos.
        return "user123".equals(numberId) && "pass123".equals(password); // Ejemplo estático
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
