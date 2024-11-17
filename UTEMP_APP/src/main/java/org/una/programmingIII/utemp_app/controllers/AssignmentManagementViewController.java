package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.dtos.AssignmentDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.enums.AssignmentState;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.AssignmentAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class AssignmentManagementViewController extends Controller {

    // 1. Campos privados: UI y servicios
    @FXML
    private MFXTextField findByIdTxf, assigmentIdTxf, courseAssigmentTxf, assignmentTitleTxf, assigmentDescriptionTxf;
    @FXML
    private MFXComboBox<String> assignmentStateComboBox;
    @FXML
    private MFXDatePicker assignmentDeadlineDtp;
    @FXML
    private MFXButton findByCourseIdBtn, prevPageBtn, nextPageBtn;
    @FXML
    private Label pageNumberLbl;
    @FXML
    private TableView<AssignmentDTO> assignmentTbv;
    @FXML
    private TableColumn<AssignmentDTO, Long> assignmentdTbc;
    @FXML
    private TableColumn<AssignmentDTO, String> assignmentTitleTbc, courseNameTbc, assignmentStateTbc, assignmentDeadlineTbc;

    private AssignmentAPIService assignmentService;
    private BaseApiServiceManager<AssignmentDTO> baseApiServiceManager;
    private PageDTO<AssignmentDTO> assignments;
    private AssignmentDTO selectedAssignment;
    private int currentPage = 1;
    private int maxPage = 1;

    // 2. Métodos de inicialización y configuración
    @FXML
    public void initialize() {
        baseApiServiceManager = new AssignmentAPIService();
        assignmentService = new AssignmentAPIService();
        setTableView();
        loadAssignments();

        // Inicializar el ComboBox de estados de asignación
        assignmentStateComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(AssignmentState.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        ));
    }

    private void setTableView() {
        // Configurar las columnas de la tabla
        assignmentTbv.setEditable(false);
        assignmentdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentTitleTbc.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseNameTbc.setCellValueFactory(cellData -> createTableCellValue(cellData, assignment -> Optional.ofNullable(assignment.getCourse().getName()).orElse("nulo")));
        assignmentStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));
        assignmentDeadlineTbc.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        // Listener para actualizar los campos cuando se selecciona una fila
        assignmentTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) fillFieldsFromSelectedAssignment(newValue);
        });
    }

    // 3. Métodos de operaciones (CRUD)
    private void loadAssignments() {
        try {
            MessageResponse<PageDTO<AssignmentDTO>> response = baseApiServiceManager.getAllEntities(
                    PageRequest.of(currentPage, 10), new TypeReference<PageDTO<AssignmentDTO>>() {
                    }
            );
            if (response.isSuccess()) {
                assignments = response.getData();
                loadTable(assignments);
                updatePageNumber();
            } else {
                showError("Error al cargar asignaciones.");
            }
        } catch (Exception e) {
            showError("Error al cargar las asignaciones: " + e.getMessage());
        }
    }

    private void loadTable(PageDTO<AssignmentDTO> page) {
        maxPage = page.getTotalPages();
        assignmentTbv.getItems().setAll(page.getContent());
    }

    private void updatePageNumber() {
        pageNumberLbl.setText(String.valueOf(currentPage));
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= maxPage);
    }

    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            loadAssignments();
        }
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        if (currentPage < maxPage) {
            currentPage++;
            loadAssignments();
        }
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadAssignments();
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        // Limpiar todos los campos de manera eficiente
        Arrays.asList(assigmentIdTxf, courseAssigmentTxf, assignmentTitleTxf, assigmentDescriptionTxf).forEach(MFXTextField::clear);
        assignmentDeadlineDtp.clear();
    }

    @FXML
    public void onActionDeleteAssignmentBtn(ActionEvent event) {
        selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null && showConfirmationMessage("Eliminar Asignación", "¿Estás seguro de que deseas eliminar esta asignación?")) {
            MessageResponse<Void> response = baseApiServiceManager.deleteEntity(selectedAssignment.getId());
            super.showReadResponse(response);
            if (response.isSuccess()) {
                loadAssignments();
            }
        } else {
            showNotificationToast("Advertencia", "Por favor, selecciona una asignación para eliminar.");
        }
    }

    @FXML
    public void onActionUpdateAssignmentBtn(ActionEvent event) {
        selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null) {
            setData();
            MessageResponse<Void> response = baseApiServiceManager.createEntity(selectedAssignment);
            super.showReadResponse(response);
            if (response.isSuccess()) {
                loadAssignments();
            }
        } else {
            showNotificationToast("Advertencia", "Por favor, selecciona una asignación para editar.");
        }
    }

    @FXML
    public void onActionCreateAssignmentBtn(ActionEvent event) {
        String title = assignmentTitleTxf.getText();
        String description = assigmentDescriptionTxf.getText();
        String state = assignmentStateComboBox.getSelectedItem();
        LocalDate deadline = assignmentDeadlineDtp.getValue();

        if (title.isEmpty() || description.isEmpty() || state == null || deadline == null) {
            showError("Por favor, complete todos los campos.");
            return;
        }

        selectedAssignment = new AssignmentDTO();
        setData();
        selectedAssignment.setId(null);

        MessageResponse<Void> response = baseApiServiceManager.createEntity(selectedAssignment);
        super.showReadResponse(response);
        if (response.isSuccess()) {
            loadAssignments();
        }
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.COURSES);
    }

    @FXML
    public void onActionGoSubmissions() {
        selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null) {
            AppContext.getInstance().setAssignmentDTO(selectedAssignment);
            ViewManager.getInstance().loadInternalView(Views.ASSIGNMENT);
        }
    }

    // 4. Métodos auxiliares y utilitarios
    private void setData() {
        String title = assignmentTitleTxf.getText();
        String description = assigmentDescriptionTxf.getText();
        String state = assignmentStateComboBox.getSelectedItem();
        LocalDate deadline = assignmentDeadlineDtp.getValue();

        selectedAssignment.setTitle(title);
        selectedAssignment.setDescription(description);
        selectedAssignment.setState(AssignmentState.valueOf(state));
        selectedAssignment.setDeadline(Instant.from(deadline));
    }

    private void fillFieldsFromSelectedAssignment(AssignmentDTO assignment) {
        assigmentIdTxf.setText(String.valueOf(assignment.getId()));
        assignmentTitleTxf.setText(assignment.getTitle());
        assigmentDescriptionTxf.setText(assignment.getDescription());
        assignmentStateComboBox.setText(assignment.getState().name());

        LocalDate deadlineDate = Instant.ofEpochMilli(assignment.getDeadline().toEpochMilli())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        assignmentDeadlineDtp.setValue(deadlineDate);
    }

    private <T> SimpleStringProperty createTableCellValue(TableColumn.CellDataFeatures<AssignmentDTO, String> cellData, Function<AssignmentDTO, T> getter) {
        return new SimpleStringProperty(Optional.ofNullable(cellData.getValue())
                .map(getter)
                .map(Object::toString)
                .orElse("nulo"));
    }
}
