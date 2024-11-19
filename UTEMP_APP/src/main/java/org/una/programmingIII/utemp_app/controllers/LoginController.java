package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.una.programmingIII.utemp_app.requests.AuthRequest;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.responses.TokenResponse;
import org.una.programmingIII.utemp_app.services.AuthService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

public class LoginController extends Controller {
    private final AuthService authService = new AuthService();
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

    @Override
    public void initialize() {
        numberIdentificationTxf.delegateSetTextFormatter(textFormatterOnlyNumbers());

        loginBtn.setOnAction(event -> handleLogin());
        helpBtn.setOnAction(event -> showHelp());
        infoBtn.setOnAction(event -> showInfo());
        numberIdentificationTxf.setOnKeyPressed(this::handleEnterKey);
        passwordPwf.setOnKeyPressed(this::handleEnterKey);

    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    private void handleLogin() {
        String numberId = numberIdentificationTxf.getText();
        String password = passwordPwf.getText();

        if (numberId.isEmpty() || password.isEmpty()) {
            showNotificationToast("Error", "Please enter your identification number and password.");
            return;
        }

        MessageResponse<TokenResponse> response = authService.login(new AuthRequest(numberId, password));

        if (response.isSuccess()) {
            AppContext.getInstance().setUserDTO(response.getData().getUser());
            showNotificationToast("Sesión iniciada", "Bienvenido " + response.getData().getUser().getName());
            ViewManager.getInstance().showMainView(Views.MAIN_BACKGROUND, Views.MENU);
        } else {
            showReadResponse(response);
        }
    }

    private void showHelp() {
        showNotificationToast("Help", "This is the help section.");
    }

    private void showInfo() {
        ViewManager.getInstance().showMainView(Views.MAIN_BACKGROUND, Views.MENU);
    }
}
