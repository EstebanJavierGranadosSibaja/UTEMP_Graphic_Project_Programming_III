package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

public class MenuController extends Controller {

    @FXML
    private MFXButton universityManagementbtn;
    @FXML
    private MFXButton userManagementBtn;
    @FXML
    private MFXButton enrollmentManagementBtn;

    @FXML
    public void initialize() {
        // Este método se ejecuta al inicializar el controlador.
        // Puedes agregar lógica de configuración si es necesario en el futuro.
    }

    @FXML
    public void onActionUniversityManagementbtn() {
        try {
            // Lógica para manejar la acción de gestión de universidades
            ViewManager.getInstance().loadInternalView(Views.UNIVERSITY_MANAGEMENT);
            System.out.println("Vista de gestión de universidades cargada exitosamente.");
        } catch (Exception e) {
            showError("Error al cargar la vista de gestión de universidades: " + e.getMessage());
        }
    }

    @FXML
    public void onActionUserManagementbtn() {
        try {
            // Lógica para manejar la acción de gestión de usuarios
            ViewManager.getInstance().loadInternalView(Views.USER_MANAGEMENT);
            System.out.println("Vista de gestión de usuarios cargada exitosamente.");
        } catch (Exception e) {
            showError("Error al cargar la vista de gestión de usuarios: " + e.getMessage());
        }
    }

    @FXML
    public void onActionEnrollmentManagementbtn() {
        try {
            // Lógica para manejar la acción de gestión de inscripciones
            ViewManager.getInstance().loadInternalView(Views.ENROLLMENT_MANAGEMENT);
            System.out.println("Vista de gestión de inscripciones cargada exitosamente.");
        } catch (Exception e) {
            showError("Error al cargar la vista de gestión de inscripciones: " + e.getMessage());
        }
    }

    private void showError(String message) {
        // Método para mostrar errores
        System.err.println(message);
        // Podrías agregar lógica para mostrar un cuadro de diálogo con el mensaje de error.
    }
}
