package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.enums.CourseState;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.CourseAPIService;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;

import java.util.List;

public class CoursesViewController extends Controller {

    @FXML
    private TableView<CourseDTO> coursesTbv;
    @FXML
    private TableColumn<CourseDTO, Long> courseIdTbc;
    @FXML
    private TableColumn<CourseDTO, String> courseNameTbc;
    @FXML
    private TableColumn<CourseDTO, String> courseDepartmentTbc;
    @FXML
    private TableColumn<CourseDTO, CourseState> courseStateTbc;
    @FXML
    private MFXTextField findByNameTxf;
    @FXML
    private Label pageNumberLbl;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;

    private BaseApiServiceManager<CourseDTO> baseApiServiceManager;
    private CourseAPIService courseAPIService;
    private boolean initFlag = false;
    private int pageNumber = 0, maxPage = 1;
    private CourseDTO courseDTO = new CourseDTO();

    @FXML
    public void initialize() {
        if (!initFlag) {
            courseAPIService = new CourseAPIService();
            baseApiServiceManager = courseAPIService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {
        courseIdTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.1000));
        courseNameTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.3000));
        courseDepartmentTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.3000));
        courseStateTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.3000));

        coursesTbv.setEditable(false);
        courseIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseDepartmentTbc.setCellValueFactory(new PropertyValueFactory<>("departmentUniqueName"));
        courseStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));

        coursesTbv.setPlaceholder(new Label("THERE ARE NO COURSE AVAILABLE AT THIS TIME."));
        coursesTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedTeacher(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedTeacher(CourseDTO courseNewDTO) {
        courseDTO = courseNewDTO;
    }

    @FXML
    public void onActionFindByNameBtn(ActionEvent event) {
//        String idToFind = findByNameTxf.getText().trim();
//        if (!idToFind.isEmpty()) {
//            //MessageResponse<CourseDTO> response = userAPIService.getUserByIdentificationNumber(idToFind);
//            PageDTO<CourseDTO> uniqueUser = new PageDTO<>();
//            List<CourseDTO> users = new ArrayList<>();
//            users.add(response.getData());
//            uniqueUser.setContent(users);
//            uniqueUser.setTotalPages(1);
//            uniqueUser.setNumber(1);
//            loadTable(uniqueUser);
//            return;
//        }
//        try {
//            showNotificationToast("Error", "Please enter some identification number.");
//        } catch (Exception e) {
//            showError("Error al buscar usuario: " + e.getMessage());
//        }
    }

    @FXML
    private void onActionConfirmBtn(ActionEvent event) {
        if (courseDTO == null || courseDTO.getId() == null || courseDTO.getId() <= 0) {
            showNotificationToast("Error", "Please select some course.");
            return;
        }
        showNotificationToast("Success", "The course selection was success.");
        AppContext.getInstance().setCourseDTO(courseDTO);
        Stage stage = (Stage) coursesTbv.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onActionCancelBtn(ActionEvent event) {
        showNotificationToast("Success", "No user was selected.");
        AppContext.getInstance().setTeacherOrStudentDTO(null);
        Stage stage = (Stage) coursesTbv.getScene().getWindow();
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

    protected void loadTable(PageDTO<CourseDTO> page) {
        maxPage = page.getTotalPages();
        coursesTbv.setItems(listToObservableListJ(page.getContent()));
    }

    protected ObservableList<CourseDTO> listToObservableListJ(List<CourseDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<CourseDTO>> response = baseApiServiceManager.getAllEntities(
                    PageRequest.of(page, 10), new TypeReference<PageDTO<CourseDTO>>() {
                    });
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
