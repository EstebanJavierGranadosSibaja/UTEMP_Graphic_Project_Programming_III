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
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UniversityDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.UniversityAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.List;
import java.util.function.Supplier;

public class UniversityManagementViewController extends Controller {

    // FXML UI Elements
    @FXML
    private MFXTextField universityIdTxf;
    @FXML
    private MFXTextField universityNameTxf;
    @FXML
    private MFXTextField universityLocationTxf;
    @FXML
    private MFXTextField findByNameTxf;
    @FXML
    private TableView<UniversityDTO> table;
    @FXML
    private Label pageNumberLbl;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;
    @FXML
    private TableColumn<UniversityDTO, Long> idTbc;
    @FXML
    private TableColumn<UniversityDTO, String> nameTbc;
    @FXML
    private TableColumn<UniversityDTO, String> locationTbc;

    // Service and State Management
    private BaseApiServiceManager<UniversityDTO> baseApiServiceManager;
    private UniversityAPIService universityAPIService;
    private int pageNumber = 0, maxPage = 1;
    private boolean initFlag = false;

    @Override
    public void initialize() {
        if (!initFlag) {
            universityAPIService = new UniversityAPIService();
            baseApiServiceManager = new UniversityAPIService();
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {
        idTbc.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        nameTbc.prefWidthProperty().bind(table.widthProperty().multiply(0.40));
        locationTbc.prefWidthProperty().bind(table.widthProperty().multiply(0.40));

        table.setEditable(false);
        idTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationTbc.setCellValueFactory(new PropertyValueFactory<>("location"));
        table.setPlaceholder(new Label("No hay universidades disponibles."));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedUniversity(newValue);
            }
        });
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private void loadPage(int page) {

            MessageResponse<PageDTO<UniversityDTO>> response = baseApiServiceManager.getAllEntities(
                    PageRequest.of(page, 10), new TypeReference<PageDTO<UniversityDTO>>() {
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

    protected void loadTable(PageDTO<UniversityDTO> page) {
        maxPage = page.getTotalPages();
        table.getItems().clear();
        table.setItems(listToObservableList(page.getContent()));
    }

    private void fillFieldsFromSelectedUniversity(UniversityDTO university) {
        universityIdTxf.setText(String.valueOf(university.getId()));
        universityNameTxf.setText(university.getName());
        universityLocationTxf.setText(university.getLocation());
    }

    private UniversityDTO getCurrentUniversity() {
        return UniversityDTO.builder()
                .id(parseLong(universityIdTxf.getText()))
                .name(universityNameTxf.getText())
                .location(universityLocationTxf.getText())
                .build();
    }

    private boolean validateFields() {
        return !universityNameTxf.getText().isEmpty() && !universityLocationTxf.getText().isEmpty();
    }

    private Long parseLong(String text) {
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Event Handlers
    @FXML
    public void onActionCreateUniversityBtn(ActionEvent event) {
        UniversityDTO universityDTO = getCurrentUniversity();
        universityDTO.setId(null);
        handleUniversityAction(() -> baseApiServiceManager.createEntity(universityDTO));
    }

    @FXML
    public void onActionUpdateUniversityBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.updateEntity(getCurrentUniversity().getId(), getCurrentUniversity()));
    }

    @FXML
    public void onActionDeleteUniversityBtn(ActionEvent event) {
        handleUniversityAction(() -> baseApiServiceManager.deleteEntity(parseLong(universityIdTxf.getText())));
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        universityIdTxf.clear();
        universityNameTxf.clear();
        universityLocationTxf.clear();
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

    @FXML
    public void onActionFindByNameBtn(ActionEvent event) {
        String nameToFind = findByNameTxf.getText().trim();
        if (nameToFind.isEmpty()) {
            showError("Por favor, ingresa un nombre para buscar.");
            return;
        }
        try {
            System.out.println("buscado exitoso"); // Actualizar lógica de búsqueda
        } catch (Exception e) {
            showError("Error al buscar universidad: " + e.getMessage());
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

    @FXML
    public void onActionFacultiesBtn(ActionEvent event) {
        AppContext.getInstance().setUniversityDTO(getCurrentUniversity());
        ViewManager.getInstance().loadInternalView(Views.FACULTIES_MANAGEMENT);
    }

    @FXML
    public void onActionHelpInfoBtn(ActionEvent event) {
        super.showNotificationToast("Ayuda gestion universidad", "usa la tabla para seleccionar información");
    }

    // Helper Methods
    private void handleUniversityAction(Supplier<MessageResponse<Void>> action) {
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

    private void showError(String message) {
        System.err.println(message);
    }

    private void showInfo(String message) {
        System.out.println(message);
    }

    protected ObservableList<UniversityDTO> listToObservableList(List<UniversityDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }
}