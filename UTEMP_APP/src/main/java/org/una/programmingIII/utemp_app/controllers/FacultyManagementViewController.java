package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.dtos.FacultyDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UniversityDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.DepartmentAPIService;
import org.una.programmingIII.utemp_app.services.models.FacultyAPIService;
import org.una.programmingIII.utemp_app.services.models.UniversityAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.List;
import java.util.function.Supplier;

public class FacultyManagementViewController extends Controller {

    @FXML
    private MFXTextField facultyIdTxf;

    @FXML
    private MFXTextField universityFacultyTxf;

    @FXML
    private MFXTextField facultyNameTxf;

    @FXML
    private MFXTextField findByNameTxf;

    @FXML
    private TableView<FacultyDTO> facultiesTbv;

    @FXML
    private Label pageNumberLbl;

    @FXML
    private MFXButton prevPageBtn;

    @FXML
    private MFXButton nextPageBtn;

    @FXML
    private TableColumn<FacultyDTO, Long> facultyIdTbc;

    @FXML
    private TableColumn<FacultyDTO, String> facultyNameTbc;

    @FXML
    private TableColumn<UniversityDTO, String> universityNameTbc;

    private boolean initFlag = false;
    private BaseApiServiceManager<FacultyDTO> baseApiServiceManager;
    private FacultyAPIService facultyAPIServiceaculty;
    private UniversityAPIService universityAPIService;
    private int pageNumber = 0, maxPage = 1;

    @Override
    public void initialize() {
        if (!initFlag) {
            universityAPIService = new UniversityAPIService();
            facultyAPIServiceaculty = new FacultyAPIService();
            baseApiServiceManager = new FacultyAPIService();
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {

        facultyIdTbc.prefWidthProperty().bind(facultiesTbv.widthProperty().multiply(0.10));
        facultyNameTbc.prefWidthProperty().bind(facultiesTbv.widthProperty().multiply(0.45));
        universityNameTbc.prefWidthProperty().bind(facultiesTbv.widthProperty().multiply(0.45));

        facultiesTbv.setEditable(false);
        facultyIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        facultyIdTbc.setSortable(true);
        facultyNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        universityNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        facultiesTbv.setPlaceholder(new Label("THERE ARE NO FACULTIES AVAILABLE AT THIS TIME."));
        universityFacultyTxf.setText(AppContext.getInstance().getUniversityDTO().getName());
        facultiesTbv.getSortOrder().add(facultyIdTbc);
        facultyIdTbc.setSortType(TableColumn.SortType.ASCENDING);
        facultiesTbv.sort();
        facultiesTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedFaculty(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedFaculty(FacultyDTO faculty) {
        facultyIdTxf.setText(String.valueOf(faculty.getId()));
        facultyNameTxf.setText(faculty.getName());
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private FacultyDTO getCurrentFaculty() {
        return FacultyDTO.builder()
                .id(parseLong(facultyIdTxf.getText()))
                .name(facultyNameTxf.getText())
                .university(AppContext.getInstance().getUniversityDTO())
                .build();
    }

    @FXML
    public void onActionCreateFacultyBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.createEntity(getCurrentFaculty()));
        onActionClearFieldsBtn(event);
    }

    @FXML
    public void onActionUpdateFacultyBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.updateEntity(getCurrentFaculty().getId(), getCurrentFaculty()));
        onActionClearFieldsBtn(event);
    }

    @FXML
    public void onActionDeleteFacultyBtn(ActionEvent event) {
        if(facultyIdTbc.getText().isEmpty() || facultyIdTbc.getText() == null)
        {
            showNotificationToast("Error", "Please select some faculty.");
            return;
        }
        if (showConfirmationMessage("DELETE FACULTY", "DO YOU CONFIRM THE DELETION OF THIS FACULTY?")) {
            handleUniversityAction(() -> baseApiServiceManager.deleteEntity(parseLong(facultyIdTxf.getText())));
            onActionClearFieldsBtn(event);
        }
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        facultyIdTxf.clear();
        facultyNameTxf.clear();
    }

    @FXML
    public void onActionFindByNameBtn(ActionEvent event) {
        String nameToFind = findByNameTxf.getText().trim();
        if (nameToFind.isEmpty()) {
            showError("Por favor, ingresa un nombre para buscar.");
            return;
        }
        try {
        } catch (Exception e) {
            showError("Error al buscar facultad: " + e.getMessage());
        }
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
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

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.UNIVERSITY_MANAGEMENT);
    }

    @FXML
    public void onActionDepartmentsBtn(ActionEvent event) {
        FacultyDTO selectedFaculty = facultiesTbv.getSelectionModel().getSelectedItem();

        if (selectedFaculty == null) {
            showNotificationToast("Error", "Please select some faculty.");
            return;
        }
        AppContext.getInstance().setFacultyDTO(selectedFaculty);
        ViewManager.getInstance().loadInternalView(Views.DEPARTMENT_MANAGEMENT);
    }

    private Long parseLong(String text) {
        if (text == null || text.trim().isEmpty()) {
            showError("El ID no puede estar vacío.");
            return null;
        }
        if (!text.matches("\\d+")) {
            showError("El ID debe ser un número válido.");
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException e) {
            showError("El ID debe ser un número válido.");
            return null;
        }
    }

    private void showError(String message) {
        System.err.println(message);
        showNotificationToast("Error", message, Alert.AlertType.ERROR);
    }

    private void showInfo(String message) {
        System.out.println(message);
        showNotificationToast("Información", message, Alert.AlertType.INFORMATION);
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<FacultyDTO>> response = facultyAPIServiceaculty.getFacultiesByUniversityId(AppContext.getInstance().getUniversityDTO().getId(), page, 10);
            showReadResponse(response);
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

    protected void loadTable(PageDTO<FacultyDTO> page) {
        maxPage = page.getTotalPages();
        facultiesTbv.getItems().clear();
        facultiesTbv.setItems(listToObservableList(page.getContent()));
    }

    protected ObservableList<FacultyDTO> listToObservableList(List<FacultyDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private void handleUniversityAction(Supplier<MessageResponse<Void>> action) {
        if (!validateFields()) {
            return;
        }
        MessageResponse<Void> response = action.get();
        showReadResponse(response);
        if (response.isSuccess()) {
            loadInitialData();
        }
    }

    private boolean validateFields() {
        if (facultyNameTxf.getText().isEmpty()) {
            showError("Por favor, completa todos los campos requeridos.");
            return false;
        }
        return true;
    }
}