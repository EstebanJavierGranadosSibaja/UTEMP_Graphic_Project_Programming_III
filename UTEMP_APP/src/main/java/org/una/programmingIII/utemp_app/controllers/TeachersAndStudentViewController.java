package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.dtos.enums.UserState;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.UserAPIService;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;

import java.util.ArrayList;
import java.util.List;

public class TeachersAndStudentViewController extends Controller {

    @FXML
    private TableView<UserDTO> usersTbv;
    @FXML
    private TableColumn<UserDTO, Long> userIdTbc;
    @FXML
    private TableColumn<UserDTO, String> userIdNumberTbc;
    @FXML
    private TableColumn<UserDTO, String> userNameTbc;
    @FXML
    private TableColumn<UserDTO, String> userEmailTbc;
    @FXML
    private TableColumn<UserDTO, UserState> userStateTbc;
    @FXML
    private MFXTextField findByIdNumberTxf;
    @FXML
    private Label pageNumberLbl, titleLbl;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;

    private BaseApiServiceManager<UserDTO> baseApiServiceManager;
    private UserAPIService userAPIService;
    private boolean initFlag = false;
    private int pageNumber = 0, maxPage = 1;
    private UserDTO teacherOrStudentDTO = new UserDTO();

    @FXML
    public void initialize() {
        if (!initFlag) {
            userAPIService = new UserAPIService();
            baseApiServiceManager = userAPIService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }

        titleLbl.setText(AppContext.getInstance().getLabelTextTitle());
    }

    private void setTableView() {
        userIdTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(0.10));
        userIdNumberTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(0.225));
        userNameTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(0.225));
        userEmailTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(0.225));
        userStateTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(0.225));

        usersTbv.setEditable(false);
        userIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdNumberTbc.setCellValueFactory(new PropertyValueFactory<>("identificationNumber"));
        userNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailTbc.setCellValueFactory(new PropertyValueFactory<>("email"));
        userStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));

        usersTbv.setPlaceholder(new Label("THERE ARE NO USERS AVAILABLE AT THIS TIME."));
        usersTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedTeacher(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedTeacher(UserDTO teacher) {
        teacherOrStudentDTO = teacher;
    }

    @FXML
    public void onActionFindByIdNumberBtn(ActionEvent event) {
        String idToFind = findByIdNumberTxf.getText().trim();
        if (!idToFind.isEmpty()) {
            MessageResponse<UserDTO> response = userAPIService.getUserByIdentificationNumber(idToFind);
            PageDTO<UserDTO> uniqueUser = new PageDTO<>();
            List<UserDTO> users = new ArrayList<>();
            users.add(response.getData());
            uniqueUser.setContent(users);
            uniqueUser.setTotalPages(1);
            uniqueUser.setNumber(1);
            loadTable(uniqueUser);
            return;
        }
        try {
            showNotificationToast("Error", "Please enter some identification number.");
        } catch (Exception e) {
            showError("Error al buscar usuario: " + e.getMessage());
        }
    }

    @FXML
    private void onActionConfirmBtn(ActionEvent event) {
        if (teacherOrStudentDTO == null || teacherOrStudentDTO.getId() == null || teacherOrStudentDTO.getId() <= 0) {
            showNotificationToast("Error", "Please select some user.");
            return;
        }
        showNotificationToast("Success", "The user selection was success.");
        AppContext.getInstance().setTeacherOrStudentDTO(teacherOrStudentDTO);
        Stage stage = (Stage) usersTbv.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onActionCancelBtn(ActionEvent event) {
        showNotificationToast("Success", "No user was selected.");
        AppContext.getInstance().setTeacherOrStudentDTO(null);
        Stage stage = (Stage) usersTbv.getScene().getWindow();
        stage.close();
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        navigatePage(pageNumber - 1);
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        navigatePage(pageNumber + 1);
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadPage(pageNumber);
    }

    protected void loadTable(PageDTO<UserDTO> page) {
        maxPage = page.getTotalPages();
        usersTbv.setItems(listToObservableList(page.getContent()));
    }

    protected ObservableList<UserDTO> listToObservableList(List<UserDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
    }

    private void loadPage(int page) {
        try {
            //TODO agregar un metodo que me obtenga todos los usuarios
            MessageResponse<PageDTO<UserDTO>> response = userAPIService.getAllUsersByRole(AppContext.getInstance().getUserRole(), page, 10);
            super.showReadResponse(response);

            if (response.isSuccess()) {
                loadTable(response.getData());
                pageNumberLbl.setText(String.valueOf(page + 1));
                prevPageBtn.setDisable(page == 0);
                nextPageBtn.setDisable(page >= maxPage - 1);
                pageNumber = page;
            }
        } catch (Exception e) {
            showError("Error al cargar la página: " + e.getMessage());
        }
    }
}
