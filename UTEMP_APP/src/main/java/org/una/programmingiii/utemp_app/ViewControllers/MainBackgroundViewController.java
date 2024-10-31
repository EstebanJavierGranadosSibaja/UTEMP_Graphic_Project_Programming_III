package org.una.programmingiii.utemp_app.ViewControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;
import org.una.programmingiii.utemp_app.Util.Controller;

public class MainBackgroundViewController extends Controller implements Initializable {
    @FXML
    private ImageView imvLogo;

    @FXML
    private MFXButton btnUsers;

    @FXML
    private MFXButton btnCourses;

    @FXML
    private MFXButton btnMyCourses;

    @FXML
    private ImageView imvNotifications;

    @FXML
    private ImageView imvUser;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        initialize();
    }

    @Override
    public void initialize() {

    }
}
