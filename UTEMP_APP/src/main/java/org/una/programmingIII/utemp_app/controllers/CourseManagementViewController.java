package org.una.programmingIII.utemp_app.controllers;

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
import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.dtos.enums.CourseState;
import org.una.programmingIII.utemp_app.dtos.enums.UserRole;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.CourseAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.List;
import java.util.function.Supplier;

public class CourseManagementViewController extends Controller {
    @FXML
    private MFXTextField courseIdTxf, departmentCourseTxf, courseNameTxf, courseDescriptionTxf, teacherIdTxf, findByNameTxf;
    @FXML
    private MFXComboBox<CourseState> courseStateCbx;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;
    @FXML
    private TableView<CourseDTO> coursesTbv;
    @FXML
    private TableColumn<CourseDTO, Long> courseIdTbc;
    @FXML
    private TableColumn<CourseDTO, String> courseNameTbc;
    @FXML
    private TableColumn<CourseDTO, DepartmentDTO> departmentNameTbc;
    @FXML
    private TableColumn<CourseDTO, CourseState> courseStateTbc;
    @FXML
    private Label pageNumberLbl;

    private BaseApiServiceManager<CourseDTO> baseApiServiceManager;
    private CourseAPIService courseAPIService;
    private boolean initFlag = false;
    private int pageNumber = 0, maxPage = 1;
    private UserDTO teacher = new UserDTO();

    @Override
    public void initialize() {

        ObservableList<CourseState> states = FXCollections.observableArrayList(CourseState.ACTIVE, CourseState.INACTIVE, CourseState.ARCHIVED);
        courseStateCbx.setItems(states);

        if (!initFlag) {
            courseAPIService = new CourseAPIService();
            baseApiServiceManager = courseAPIService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {

        courseIdTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.10));
        courseNameTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.30));
        courseStateTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.30));
        departmentNameTbc.prefWidthProperty().bind(coursesTbv.widthProperty().multiply(0.30));

        coursesTbv.setEditable(false);
        courseIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));
        departmentNameTbc.setCellValueFactory(new PropertyValueFactory<>("departmentUniqueName"));
        departmentCourseTxf.setText(AppContext.getInstance().getDepartmentDTO().getName());


        coursesTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedUser(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedUser(CourseDTO course) {
        courseIdTxf.setText(String.valueOf(course.getId()));
        courseNameTxf.setText(course.getName());
        courseDescriptionTxf.setText(course.getDescription());
        teacherIdTxf.setText(course.getUserTeacherUniqueID().toString());
        courseStateCbx.setValue(course.getState());
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private CourseDTO getCurrentCourse() {

        return CourseDTO.builder()
                .id(parseLong(courseIdTxf.getText()))
                .name(courseNameTxf.getText())
                .description(courseDescriptionTxf.getText())
                .state(courseStateCbx.getValue())
                .teacher(AppContext.getInstance().getTeacherOrStudentDTO())
                .userTeacherUniqueID(parseLong(teacherIdTxf.getText()))
                .department(AppContext.getInstance().getDepartmentDTO())
                .build();
    }

    @FXML
    public void onActionCreateCourseBtn(ActionEvent event) {
        CourseDTO courseDTO = getCurrentCourse();
        courseDTO.setId(null);
        handleUserAction(() -> baseApiServiceManager.createEntity(courseDTO));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
    }

    @FXML
    public void onActionUpdateCourseBtn(ActionEvent event) {
        CourseDTO courseDTO = getCurrentCourse();
        courseDTO.setTeacher(teacher);
        handleUserAction(() -> baseApiServiceManager.updateEntity(courseDTO.getId(), courseDTO));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
    }

    @FXML
    public void onActionDeleteCourseBtn(ActionEvent event) {
        handleUserAction(() -> baseApiServiceManager.deleteEntity(parseLong(courseIdTxf.getText())));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
    }

    @FXML
    public void onActionShowTeachersBtn(ActionEvent event) {
        AppContext.getInstance().setLabelTextTitle("TEACHERS VIEW");
        AppContext.getInstance().setUserRole(UserRole.TEACHER);
        ViewManager.getInstance().showModalView(Views.TEACHERS_AND_STUDENTS);
        if (AppContext.getInstance().getTeacherOrStudentDTO() != null) {
            teacherIdTxf.setText(String.valueOf(AppContext.getInstance().getTeacherOrStudentDTO().getId()));
            teacher = AppContext.getInstance().getTeacherOrStudentDTO();
        }
    }

    @FXML
    public void onActionAssigmentsBtn(ActionEvent event) {
        AppContext.getInstance().setCourseDTO(getCurrentCourse());
        ViewManager.getInstance().loadInternalView(Views.ASSIGNMENT);
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        courseIdTxf.clear();
        courseNameTxf.clear();
        courseDescriptionTxf.clear();
        teacherIdTxf.clear();
        courseStateCbx.setValue(null);
        coursesTbv.getSelectionModel().clearSelection();
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
    public void onActionFindByNameBtn(ActionEvent event) {
        String idToFind = findByNameTxf.getText().trim();
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
        ViewManager.getInstance().loadInternalView(Views.DEPARTMENT_MANAGEMENT);
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
        return !courseNameTxf.getText().isEmpty() && !courseDescriptionTxf.getText().isEmpty()
                && !teacherIdTxf.getText().isEmpty() && !courseStateCbx.getText().isEmpty()
                && !departmentCourseTxf.getText().isEmpty();
    }

    private Long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void loadTable(PageDTO<CourseDTO> page) {
        maxPage = page.getTotalPages();
        coursesTbv.setItems(listToObservableList(page.getContent()));
    }

    protected ObservableList<CourseDTO> listToObservableList(List<CourseDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<CourseDTO>> response = courseAPIService.getCoursesByDepartmentId(AppContext.getInstance().getDepartmentDTO().getId(), page, 10);
            showReadResponse(response);
            if (response.isSuccess()) {
                loadTable(response.getData());
                pageNumberLbl.setText(String.valueOf(page + 1));
                prevPageBtn.setDisable(page == 0);
                nextPageBtn.setDisable(page >= maxPage - 1);
                pageNumber = page;
            }
        } catch (Exception e) {
            super.showError("Error al cargar la página: " + e.getMessage());
        }
    }
}
