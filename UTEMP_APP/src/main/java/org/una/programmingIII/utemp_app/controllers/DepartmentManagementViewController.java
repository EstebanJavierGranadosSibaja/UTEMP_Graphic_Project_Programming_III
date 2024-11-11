package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.dtos.FacultyDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.DepartmentAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.List;
import java.util.function.Supplier;

public class DepartmentManagementViewController extends Controller {

    @FXML
    private MFXTextField departmentIdTxf;
    @FXML
    private MFXTextField facultyDepartmentTxf;
    @FXML
    private MFXTextField departmentNameTxf;
    @FXML
    private MFXTextField findByNameTxf;
    @FXML
    private TableView<DepartmentDTO> departmentsTbv;

    @FXML
    private Label pageNumberLbl;

    @FXML
    private MFXButton createBtn;
    @FXML
    private MFXButton updateBtn;
    @FXML
    private MFXButton deleteBtn;
    @FXML
    private MFXButton clearFieldsBtn;
    @FXML
    private MFXButton findByNameBtn;
    @FXML
    private MFXButton coursesBtn;
    @FXML
    private MFXButton backBtn;
    @FXML
    private MFXButton helpInfoBtn;
    @FXML
    private MFXButton prevPageBtn;
    @FXML
    private MFXButton nextPageBtn;
    @FXML
    private MFXButton reloadPageBtn;

    private BaseApiServiceManager<DepartmentDTO> baseApiServiceManager;
    private DepartmentAPIService departmentAPIService;
    private int pageNumber = 0, maxPage = 1;

    @FXML
    private TableColumn<DepartmentDTO, Long> departmentIdTbc;
    @FXML
    private TableColumn<DepartmentDTO, String> departmentNameTbc;
    @FXML
    private TableColumn<DepartmentDTO, String> facultyNameTbc;

    private boolean initFlag = false;

    @Override
    public void initialize() {
        if (!initFlag) {
            departmentAPIService = new DepartmentAPIService();
            baseApiServiceManager = new DepartmentAPIService();
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {
        departmentsTbv.setEditable(false);
        departmentIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        departmentNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        facultyNameTbc.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        departmentsTbv.setPlaceholder(new Label("No hay departamentos disponibles."));

        departmentsTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedDepartment(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedDepartment(DepartmentDTO department) {
        departmentIdTxf.setText(String.valueOf(department.getId()));
        departmentNameTxf.setText(department.getName());
        facultyDepartmentTxf.setText(department.getFaculty() != null ? department.getFaculty().getName() : "");
    }

//    @FXML
//    public void onActionCreateDepartmentBtn() {
//        handleDepartmentAction(() -> baseApiServiceManager.createEntity(getCurrentDepartment()));
//    }
//
//    @FXML
//    public void onActionUpdateDepartmentBtn() {
//        handleDepartmentAction(() -> baseApiServiceManager.updateEntity(getCurrentDepartment().getId(), getCurrentDepartment()));
//    }
//
//    @FXML
//    public void onActionDeleteDepartmentBtn() {
//        handleDepartmentAction(() -> baseApiServiceManager.deleteEntity(parseLong(departmentIdTxf.getText())));
//    }

    private void handleDepartmentAction(Supplier<MessageResponse<Void>> action) {
        if (!validateFields()) {
            showError("Por favor, completa todos los campos requeridos.");
            return;
        }
        MessageResponse<Void> response = action.get();
        super.readResponse(response);
        if (response.isSuccess()) {
            loadInitialData();
        }
    }

    private boolean validateFields() {
        return !departmentNameTxf.getText().isEmpty() && !facultyDepartmentTxf.getText().isEmpty();
    }

    @FXML
    public void onActionClearFieldsBtn() {
        departmentIdTxf.clear();
        departmentNameTxf.clear();
        facultyDepartmentTxf.clear();
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<DepartmentDTO>> response = baseApiServiceManager.getAllEntities(PageRequest.of(page, 10), new TypeReference<PageDTO<DepartmentDTO>>() {
            });
            super.readResponse(response);
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

    protected void loadTable(PageDTO<DepartmentDTO> page) {
        maxPage = page.getTotalPages();
        List<DepartmentDTO> list = page.getContent();
        departmentsTbv.getItems().clear();
        departmentsTbv.setItems(listToObservableList(list));
    }

    protected ObservableList<DepartmentDTO> listToObservableList(List<DepartmentDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private Long parseLong(String text) {
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showError(String message) {
        System.err.println(message);
    }

    private DepartmentDTO getCurrentDepartment() {
        return DepartmentDTO.builder()
                .id(parseLong(departmentIdTxf.getText()))
                .name(departmentNameTxf.getText())
                .faculty(new FacultyDTO()) // Ajusta según cómo obtienes la facultad
                .build();
    }

    @FXML
    public void onActionPrevPageBtn() {
        if (pageNumber > 0) {
            loadPage(pageNumber - 1);
        }
    }

    @FXML
    public void onActionNextPageBtn() {
        if (pageNumber < maxPage - 1) {
            loadPage(pageNumber + 1);
        }
    }

    @FXML
    public void onActionReloadPageBtn() {
        loadPage(pageNumber); // Recarga la página actual
    }

    @FXML
    public void onActionFindByNameBtn() {
        String nameToFind = findByNameTxf.getText();
        System.out.println("Finding department by name: " + nameToFind);
    }

    @FXML
    public void onActionCoursesBtn() {
        System.out.println("Opening department courses...");
    }

    @FXML
    public void onActionBackBtn() {
        System.out.println("Going back to faculties...");
        ViewManager.getInstance().loadInternalView(Views.FACULTIES_MANAGEMENT);
    }

    @FXML
    public void onActionHelpInfoBtn() {
        System.out.println("Displaying help info...");
        showAlert("ayuda para administrar", " reinicia el pc");
    }

    @FXML
    public void onActionCreateFacultyBtn() {
        // Lógica para crear un nuevo departamento
        DepartmentDTO newDepartment = getCurrentDepartment();
        if (newDepartment != null) {
            handleDepartmentAction(() -> baseApiServiceManager.createEntity(newDepartment));
        } else {
            showError("No se pudo crear el departamento. Verifica los datos.");
        }
    }

    @FXML
    public void onActionUpdateUniversityBtn() {
        // Lógica para actualizar un departamento existente
        DepartmentDTO updatedDepartment = getCurrentDepartment();
        if (updatedDepartment != null && updatedDepartment.getId() != null) {
            handleDepartmentAction(() -> baseApiServiceManager.updateEntity(updatedDepartment.getId(), updatedDepartment));
        } else {
            showError("No se pudo actualizar el departamento. Verifica los datos.");
        }
    }

    @FXML
    public void onActionDeleteFacultyBtn() {
        // Lógica para eliminar un departamento
        Long departmentId = parseLong(departmentIdTxf.getText());
        if (departmentId != null) {
            handleDepartmentAction(() -> baseApiServiceManager.deleteEntity(departmentId));
        } else {
            showError("No se pudo eliminar el departamento. ID inválido.");
        }
    }

}
