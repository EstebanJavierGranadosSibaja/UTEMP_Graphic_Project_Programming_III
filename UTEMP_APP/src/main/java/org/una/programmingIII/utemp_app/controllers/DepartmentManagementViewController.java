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
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.dtos.FacultyDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.DepartmentAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
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
        departmentIdTbc.prefWidthProperty().bind(departmentsTbv.widthProperty().multiply(0.10));
        facultyNameTbc.prefWidthProperty().bind(departmentsTbv.widthProperty().multiply(0.45));
        departmentNameTbc.prefWidthProperty().bind(departmentsTbv.widthProperty().multiply(0.45));

        departmentsTbv.setEditable(false);
        departmentIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        departmentNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        facultyNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        facultyDepartmentTxf.setText(AppContext.getInstance().getFacultyDTO().getName());
        departmentsTbv.setPlaceholder(new Label("THERE ARE NO DEPARTMENTS AVAILABLE AT THIS TIME"));

        departmentsTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedDepartment(newValue);
            }
        });
    }

    @FXML
    public void onActionCreateDepartmentBtn() {
        DepartmentDTO newDepartment = getCurrentDepartment();
        if (newDepartment != null) {
            handleDepartmentAction(() -> baseApiServiceManager.createEntity(newDepartment));
        } else {
            showError("No se pudo crear el departamento. Verifica los datos.");
        }
        onActionClearFieldsBtn();
    }

    @FXML
    public void onActionUpdateDepartmentBtn() {
        DepartmentDTO updatedDepartment = getCurrentDepartment();
        if (updatedDepartment != null && updatedDepartment.getId() != null) {
            handleDepartmentAction(() -> baseApiServiceManager.updateEntity(updatedDepartment.getId(), updatedDepartment));
        } else {
            showError("No se pudo actualizar el departamento. Verifica los datos.");
        }
        onActionClearFieldsBtn();
    }

    @FXML
    public void onActionDeleteDepartmentBtn() {
        if(departmentIdTxf.getText().isEmpty() || departmentIdTxf.getText() == null)
        {
            System.out.println(departmentIdTxf.getText());
            showNotificationToast("Error", "Please select some department.");
            return;
        }
        if (showConfirmationMessage("DELETE FACULTY", "DO YOU CONFIRM THE DELETION OF THIS FACULTY?")) {
            handleDepartmentAction(() -> baseApiServiceManager.deleteEntity(parseLong(departmentIdTxf.getText())));
            onActionClearFieldsBtn();
            loadPage(pageNumber);
        }
    }

    private void fillFieldsFromSelectedDepartment(DepartmentDTO department) {
        departmentIdTxf.setText(String.valueOf(department.getId()));
        departmentNameTxf.setText(department.getName());
    }

    private void handleDepartmentAction(Supplier<MessageResponse<Void>> action) {
        if (!validateFields()) {
            showError("Por favor, completa todos los campos requeridos.");
            return;
        }
        MessageResponse<Void> response = action.get();
        super.showReadResponse(response);
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
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<DepartmentDTO>> response = departmentAPIService.getDepartmentsByFacultyId(AppContext.getInstance().getFacultyDTO().getId(), page, 10);
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

    protected void loadTable(PageDTO<DepartmentDTO> page) {
        maxPage = page.getTotalPages();
        departmentsTbv.getItems().clear();
        departmentsTbv.setItems(listToObservableList(page.getContent()));
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
                .faculty(AppContext.getInstance().getFacultyDTO())
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
        showNotificationToast("ayuda para administrar", " reinicia el pc");
    }

}
