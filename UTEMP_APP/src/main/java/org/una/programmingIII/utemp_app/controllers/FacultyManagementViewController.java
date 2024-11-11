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
import org.una.programmingIII.utemp_app.responses.MessageResponse;
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
    private TableColumn<FacultyDTO, String> universityNameTbc;

    private boolean initFlag = false;
    private BaseApiServiceManager<FacultyDTO> baseApiServiceManager;
    private FacultyAPIService facultyAPIService;
    private UniversityAPIService universityAPIService;
    private int pageNumber = 0, maxPage = 1;

    @Override
    public void initialize() {
        if (!initFlag) {
            universityAPIService = new UniversityAPIService();
            facultyAPIService = new FacultyAPIService();
            baseApiServiceManager = new FacultyAPIService();
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {
        facultiesTbv.setEditable(false);
        facultyIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        facultyNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        universityNameTbc.setCellValueFactory(new PropertyValueFactory<>("university"));
        facultiesTbv.setPlaceholder(new Label("No hay facultades disponibles."));

        facultiesTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedFaculty(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedFaculty(FacultyDTO faculty) {
        facultyIdTxf.setText(String.valueOf(faculty.getId()));
        facultyNameTxf.setText(faculty.getName());
        universityFacultyTxf.setText(faculty.getUniversity().getName());
        System.out.print(faculty.getUniversity().getName());
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private FacultyDTO getCurrentFaculty() {
        Long facultyId = parseLong(facultyIdTxf.getText());
        Long universityId = parseLong(universityFacultyTxf.getText());
        if (facultyId == null || universityId == null) {
            return null; // No continuar si no es un número válido
        }
        return FacultyDTO.builder()
                .id(facultyId)
                .name(facultyNameTxf.getText())
                .university(universityAPIService.getUniversityById(universityId).getData())
                .build();
    }

    @FXML
    public void onActionCreateFacultyBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.createEntity(getCurrentFaculty()));
    }

    @FXML
    public void onActionUpdateFacultyBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.updateEntity(getCurrentFaculty().getId(), getCurrentFaculty()));
    }

    @FXML
    public void onActionDeleteFacultyBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.deleteEntity(parseLong(facultyIdTxf.getText())));
    }

    private void handleUniversityAction(Supplier<MessageResponse<Void>> action) {
        if (!validateFields()) {
            return;
        }
        MessageResponse<Void> response = action.get();
        showReadResponse(response); // Usar el método readResponse de la clase padre
        if (response.isSuccess()) {
            loadInitialData(); // Recargar la tabla después de las acciones CRUD
        }
    }

    private boolean validateFields() {
        if (facultyNameTxf.getText().isEmpty() || facultyIdTxf.getText().isEmpty() || universityFacultyTxf.getText().isEmpty()) {
            showError("Por favor, completa todos los campos requeridos.");
            return false;
        }
        // Verifica que los campos ID sean números válidos
        if (parseLong(facultyIdTxf.getText()) == null || parseLong(universityFacultyTxf.getText()) == null) {
            showError("El ID de la facultad y la universidad deben ser números válidos.");
            return false;
        }
        return true;
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        facultyIdTxf.clear();
        facultyNameTxf.clear();
        universityFacultyTxf.clear();
    }

    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        navigatePage(pageNumber - 1);
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        navigatePage(pageNumber + 1);
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<FacultyDTO>> response = facultyAPIService.getFacultiesByUniversityId(AppContext.getInstance().getUniversityDTO().getId(), 0, 10);
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

    @FXML
    public void onActionFindByNameBtn(ActionEvent event) {
        String nameToFind = findByNameTxf.getText().trim();
        if (nameToFind.isEmpty()) {
            showError("Por favor, ingresa un nombre para buscar.");
            return;
        }
        try {
//            MessageResponse<PageDTO<FacultyDTO>> response = facultyAPIService.findByName(nameToFind, PageRequest.of(0, 10));
//            readResponse(response); // Usar el método readResponse de la clase padre
//            if (response.isSuccess()) {
//                loadTable(response.getData());
//            }
        } catch (Exception e) {
            showError("Error al buscar facultad: " + e.getMessage());
        }
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadPage(pageNumber); // Recargar la página actual
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.MENU);
    }

    @FXML
    public void onActionDepartmentsBtn(ActionEvent event) {
        FacultyDTO selectedFaculty = facultiesTbv.getSelectionModel().getSelectedItem();
        if (selectedFaculty != null) {
            MessageResponse<PageDTO<DepartmentDTO>> response = facultyAPIService.getDepartmentsByFacultyId(selectedFaculty.getId(), 0, 10);
            showReadResponse(response); // Usar el método readResponse de la clase padre
            if (response.isSuccess()) {
                AppContext.getInstance().setPageDepartment(response.getData());
                AppContext.getInstance().setFacultyDTO(selectedFaculty);
                ViewManager.getInstance().loadInternalView(Views.DEPARTMENT_MANAGEMENT);
            }
        } else {
            showError("Por favor, selecciona una facultad.");
        }
    }

    @FXML
    public void onActionHelpInfoBtn(ActionEvent event) {
        super.showNotificationToast("Ayuda gestión universidad", "Usa la tabla para seleccionar información");
    }

    private Long parseLong(String text) {
        if (text == null || text.trim().isEmpty()) {
            showError("El ID no puede estar vacío.");
            return null;
        }
        if (!text.matches("\\d+")) { // Verifica que la cadena contenga solo números
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

    @FXML
    private void onActionUpdateUniversityBtn(ActionEvent event) {
    }

    @FXML
    private void onActionDeparmentsBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.DEPARTMENT_MANAGEMENT);
    }
}
