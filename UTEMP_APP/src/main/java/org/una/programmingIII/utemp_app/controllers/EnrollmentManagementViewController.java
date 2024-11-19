package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.dtos.EnrollmentDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.dtos.enums.EnrollmentState;
import org.una.programmingIII.utemp_app.dtos.enums.UserRole;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.EnrollmentAPIService;
import org.una.programmingIII.utemp_app.services.models.UserAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.List;
import java.util.function.Supplier;

public class EnrollmentManagementViewController extends Controller {
    @FXML
    private MFXTextField courseIdTxf, studentIdTxf, enrollmentIdTxf, findByParametersTxf;
    @FXML
    private MFXComboBox<EnrollmentState> enrollmentStateCbx;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;
    @FXML
    private TableView<EnrollmentDTO> enrollmentsTbv;
    @FXML
    private TableColumn<EnrollmentDTO, Long> enrollmentIdTbc;
    @FXML
    private TableColumn<EnrollmentDTO, Long> courseIdTbc;
    @FXML
    private TableColumn<EnrollmentDTO, Long> studentIdTbc;
    @FXML
    private TableColumn<EnrollmentDTO, EnrollmentState> enrollmentStateTbc;
    @FXML
    private Label pageNumberLbl;

    private BaseApiServiceManager<EnrollmentDTO> baseApiServiceManager;
    private EnrollmentAPIService enrollmentAPIService;
    private UserAPIService userAPIService;
    private boolean initFlag = false;
    private int pageNumber = 0, maxPage = 1;
    private UserDTO userDTO = new UserDTO();
    private CourseDTO courseDTO = new CourseDTO();

    @Override
    public void initialize() {

        ObservableList<EnrollmentState> states = FXCollections.observableArrayList(EnrollmentState.ENROLLED, EnrollmentState.COMPLETED, EnrollmentState.DROPPED);
        enrollmentStateCbx.setItems(states);

        if (!initFlag) {
            enrollmentAPIService = new EnrollmentAPIService();
            userAPIService = new UserAPIService();
            baseApiServiceManager = enrollmentAPIService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {

        courseIdTbc.prefWidthProperty().bind(enrollmentsTbv.widthProperty().multiply(0.25));
        enrollmentIdTbc.prefWidthProperty().bind(enrollmentsTbv.widthProperty().multiply(0.25));
        studentIdTbc.prefWidthProperty().bind(enrollmentsTbv.widthProperty().multiply(0.25));
        enrollmentStateTbc.prefWidthProperty().bind(enrollmentsTbv.widthProperty().multiply(0.25));

        enrollmentsTbv.setEditable(false);
        courseIdTbc.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        enrollmentIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentIdTbc.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        enrollmentStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));

        enrollmentsTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedUser(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedUser(EnrollmentDTO enrollmentDTO) {
        courseIdTxf.setText(String.valueOf(enrollmentDTO.getCourseId()));
        enrollmentIdTxf.setText(String.valueOf(enrollmentDTO.getId()));
        studentIdTxf.setText(String.valueOf(enrollmentDTO.getStudentId()));
        enrollmentStateCbx.setValue(enrollmentDTO.getState());
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private EnrollmentDTO getCurrentCourse() {

        return EnrollmentDTO.builder()
                .id(parseLong(enrollmentIdTxf.getText()))
                .courseId(parseLong(courseIdTxf.getText()))
                .studentId(parseLong(studentIdTxf.getText()))
                .state(enrollmentStateCbx.getValue())
                .build();
    }

    @FXML
    public void onActionCreateEnrollmentBtn(ActionEvent event) {
        EnrollmentDTO enrollmentDTO = getCurrentCourse();
        enrollmentDTO.setId(null);
        handleUserAction(() -> userAPIService.enrollUserToCourse(enrollmentDTO.getStudentId(), enrollmentDTO.getCourseId()));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
        showNotificationToast("Info", "Emails are being sent.");
    }

    @FXML
    public void onActionUpdateEnrollmentBtn(ActionEvent event) {
        EnrollmentDTO enrollmentDTO = getCurrentCourse();
        handleUserAction(() -> baseApiServiceManager.updateEntity(enrollmentDTO.getId(), enrollmentDTO));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
        showNotificationToast("Info", "Registration has been updated successfully, emails are being sent.");
    }

    @FXML
    public void onActionDeleteEnrollmentBtn(ActionEvent event) {
        handleUserAction(() -> baseApiServiceManager.deleteEntity(parseLong(enrollmentIdTxf.getText())));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
        showNotificationToast("Info", "Registration has been deleted successfully.");
    }

    @FXML
    public void onActionShowCoursesBtn(ActionEvent event) {
        ViewManager.getInstance().showModalView(Views.COURSES_VIEW);
        if (AppContext.getInstance().getCourseDTO() != null) {
            courseIdTxf.setText(String.valueOf(AppContext.getInstance().getCourseDTO().getId()));
            courseDTO = AppContext.getInstance().getCourseDTO();
        }
    }

    @FXML
    public void onActionShowStudentsBtn(ActionEvent event) {
        AppContext.getInstance().setLabelTextTitle("STUDENTS VIEW");
        AppContext.getInstance().setUserRole(UserRole.STUDENT);
        ViewManager.getInstance().showModalView(Views.TEACHERS_AND_STUDENTS);
        if (AppContext.getInstance().getTeacherOrStudentDTO() != null) {
            studentIdTxf.setText(String.valueOf(AppContext.getInstance().getTeacherOrStudentDTO().getId()));
            userDTO = AppContext.getInstance().getTeacherOrStudentDTO();
        }
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        courseIdTxf.clear();
        studentIdTxf.clear();
        enrollmentIdTxf.clear();
        enrollmentStateCbx.clear();
        enrollmentStateCbx.setValue(null);
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
    public void onActionFindByParametersBtn(ActionEvent event) {
        String idToFind = findByParametersTxf.getText().trim();
        if (idToFind.isEmpty()) {
            showError("Por favor, ingresa un número de identificación para buscar.");
            return;
        }
        try {
            System.out.println("Búsqueda exitosa"); // Aquí se podría implementar lógica real
        } catch (Exception e) {
            showError("Error al buscar usuario: " + e.getMessage());
        }
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadPage(pageNumber);
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.MENU);
    }

    private void handleUserAction(Supplier<MessageResponse<Void>> action) {
        if (!validateFields()) {
            showNotificationToast("Warning", "Please complete all required fields.");
            return;
        }
        MessageResponse<Void> response = action.get();
        super.showReadResponse(response);
        if (response.isSuccess()) {
            loadInitialData();
        }
    }

    private boolean validateFields() {
        return !studentIdTxf.getText().isEmpty() && !courseIdTxf.getText().isEmpty()
                && !enrollmentStateCbx.getText().isEmpty();
    }

    private Long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void loadTable(PageDTO<EnrollmentDTO> page) {
        maxPage = page.getTotalPages();
        enrollmentsTbv.setItems(listToObservableList(page.getContent()));
    }

    protected ObservableList<EnrollmentDTO> listToObservableList(List<EnrollmentDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
    }

    private void loadPage(int page) {
        MessageResponse<PageDTO<EnrollmentDTO>> response = baseApiServiceManager.getAllEntities(
                PageRequest.of(page, 10), new TypeReference<PageDTO<EnrollmentDTO>>() {
                });
        super.showReadResponse(response);

        if (response.isSuccess()) {
            loadTable(response.getData());
            pageNumberLbl.setText(String.valueOf(page + 1));
            prevPageBtn.setDisable(page == 0);
            nextPageBtn.setDisable(page >= maxPage - 1);
            pageNumber = page;
        }
    }
}
