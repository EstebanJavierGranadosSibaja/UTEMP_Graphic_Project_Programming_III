package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

public class MainBackgroundViewController extends Controller {

    private static final String UNIDENTIFIED_USER_MESSAGE = "Usuario no identificado";

    @FXML
    public MFXButton goMenu;
    @FXML
    private Label usernameLbl;

    private UserDTO user;

    @Override
    public void initialize() {
        loadUserInformation();

        goMenu.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.MENU));
//        assignations.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.ASSIGNMENT));
//        submissionB.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.SUBMISSIONS));
    }

    private void loadUserInformation() {
        user = AppContext.getInstance().getUserDTO();
        if (user != null && user.getName() != null) {
            usernameLbl.setText(user.getName());
        } else {
            usernameLbl.setText(UNIDENTIFIED_USER_MESSAGE);
            showNotificationToast("Advertencia", "No se ha podido cargar el nombre de usuario.");
        }
    }

    @FXML
    void onActionMyCoursesBtn() {
        if (user != null)
            ViewManager.getInstance().loadInternalView(Views.MY_COURSES);
    }

    @FXML
    void onActionUserNotificationsBtn() {
    }

    @FXML
    void onActionLogoutBtn() {
        if (showConfirmationMessage("Logout", "Cerrar sesion?"))
            ViewManager.getInstance().showMainView(Views.LOGIN);
    }


    @FXML
    void onActionUserInformationBtn() {
        super.showNotificationToast("info", "No se ha podido cargar el nombre de usuario.");
    }
}
