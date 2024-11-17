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
import org.una.programmingIII.utemp_app.dtos.enums.UserRole;
import org.una.programmingIII.utemp_app.dtos.enums.UserState;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.UserAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TeachersViewController extends Controller{

    @FXML
    private TableView<UserDTO> teachersTbv;
    @FXML
    private TableColumn<UserDTO, Long> teacherIdTbc;
    @FXML
    private TableColumn<UserDTO, String> teacherIdNumberTbc;
    @FXML
    private TableColumn<UserDTO, String> teacherNameTbc;
    @FXML
    private TableColumn<UserDTO, String> teacherEmailTbc;
    @FXML
    private TableColumn<UserDTO, UserState> teacherStateTbc;
    @FXML
    private MFXTextField findByIdNumberTxf;
    @FXML
    private Label pageNumberLbl;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;

    private BaseApiServiceManager<UserDTO> baseApiServiceManager;
    private UserAPIService userAPIService;
    private boolean initFlag = false;
    private int pageNumber = 0, maxPage = 1;
    private UserDTO teacherDTO = new UserDTO();

    @FXML
    public void initialize() {
        if (!initFlag) {
            userAPIService = new UserAPIService();
            baseApiServiceManager = userAPIService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {
        teacherIdTbc.prefWidthProperty().bind(teachersTbv.widthProperty().multiply(0.10));
        teacherIdNumberTbc.prefWidthProperty().bind(teachersTbv.widthProperty().multiply(0.225));
        teacherNameTbc.prefWidthProperty().bind(teachersTbv.widthProperty().multiply(0.225));
        teacherEmailTbc.prefWidthProperty().bind(teachersTbv.widthProperty().multiply(0.225));
        teacherStateTbc.prefWidthProperty().bind(teachersTbv.widthProperty().multiply(0.225));

        teachersTbv.setEditable(false);
        teacherIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        teacherIdNumberTbc.setCellValueFactory(new PropertyValueFactory<>("identificationNumber"));
        teacherNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        teacherEmailTbc.setCellValueFactory(new PropertyValueFactory<>("email"));
        teacherStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));

        teachersTbv.setPlaceholder(new Label("THERE ARE NO TEACHERS AVAILABLE AT THIS TIME."));
        teachersTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedTeacher(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedTeacher(UserDTO teacher) {
        teacherDTO = teacher;
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
        if (teacherDTO == null || teacherDTO.getId() == null || teacherDTO.getId() <= 0) {
            showNotificationToast("Error", "Please select some teacher.");
            return;
        }
        showNotificationToast("Success", "The user selection was success.");
        AppContext.getInstance().setTeacherDTO(teacherDTO);
        Stage stage = (Stage) teachersTbv.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onActionCancelBtn(ActionEvent event) {
        showNotificationToast("Success", "No user was selected.");
        AppContext.getInstance().setTeacherDTO(null);
        Stage stage = (Stage) teachersTbv.getScene().getWindow();
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
        teachersTbv.setItems(listToObservableList(page.getContent()));
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
            MessageResponse<PageDTO<UserDTO>> response = userAPIService.getAllUsersByRole(UserRole.TEACHER, page, 10);
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
