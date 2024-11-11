package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

public class MainBackgroundViewController extends Controller {

    private static final String UNIDENTIFIED_USER_MESSAGE = "Usuario no identificado";

    @FXML
    public MFXButton goMenu, assignations, submissionB;
    @FXML
    private ImageView imvLogo, imvNotifications, imvUser;
    @FXML
    private Label usernameLbl;

    private UserDTO user;

    @Override
    public void initialize() {
        loadUserInformation();

        goMenu.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.MENU));
        assignations.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.ASSIGNMENT));
        submissionB.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.SUBMISSIONS));
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

//    private void showError(String message) {
//        showAlert("error", message);
//    }

//    @FXML
//    void onActionCoursesBtn() {
//        System.out.println("Courses button clicked");
//        ViewManager.getInstance().loadInternalView(Views.MENU);
//    }
    //    @FXML
//    void onActionUniversitiesBtn() {
//        try {
//            ViewManager.getInstance().loadInternalView(Views.UNIVERSITY_MANAGEMENT);
//            System.out.println("Universities button clicked");
//        } catch (Exception e) {
//            handleError("Error al cargar la vista de universidades: " + e.getMessage());
//        }
//    }
//    @FXML
//    void onActionUsersBtn() {
//        try {
//            ViewManager.getInstance().loadInternalView(Views.USER_MANAGEMENT);
//            super.message = "Users button clicked";
//        } catch (Exception e) {
//            super.message = ("Error al cargar la vista de usuarios: " + e.getMessage());
//        }
//        showAlert("Info", super.message);
//    }
}
