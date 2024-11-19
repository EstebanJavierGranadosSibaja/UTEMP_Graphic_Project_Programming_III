package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class AssignmentManagementViewController extends Controller {

    @FXML
    private MFXTextField findByIdTxf, assigmentIdTxf, courseAssigmentTxf, assignmentTitleTxf, assigmentDescriptionTxf;
    @FXML
    private MFXComboBox<AssignmentState> assignmentStateComboBox;
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
    //    @FXML
//    private TableColumn<AssignmentDTO, Instant> assignmentDeadlineTbc;
    @FXML
    private TableColumn<AssignmentDTO, String> assignmentTitleTbc, courseNameTbc, assignmentStateTbc;

    private AssignmentAPIService assignmentService;
    private BaseApiServiceManager<AssignmentDTO> baseApiServiceManager;
    private PageDTO<AssignmentDTO> assignments;
    private AssignmentDTO selectedAssignment;
    private Boolean initFlag = false;
    private int currentPage = 0;
    private int maxPage = 1;

    @FXML
    public void initialize() {
        ObservableList<AssignmentState> states = FXCollections.observableArrayList(AssignmentState.PENDING, AssignmentState.CANCELLED, AssignmentState.COMPLETED, AssignmentState.ONGOING);
        assignmentStateComboBox.setItems(states);

        if (!initFlag) {
            assignmentService = new AssignmentAPIService();
            baseApiServiceManager = assignmentService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {

        assignmentdTbc.prefWidthProperty().bind(assignmentTbv.widthProperty().multiply(0.10));
        courseNameTbc.prefWidthProperty().bind(assignmentTbv.widthProperty().multiply(0.225));
        assignmentTitleTbc.prefWidthProperty().bind(assignmentTbv.widthProperty().multiply(0.2250));
        assignmentStateTbc.prefWidthProperty().bind(assignmentTbv.widthProperty().multiply(0.225));
//        assignmentDeadlineTbc.prefWidthProperty().bind(assignmentTbv.widthProperty().multiply(0.225));

        assignmentTbv.setEditable(false);
        assignmentdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentTitleTbc.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseNameTbc.setCellValueFactory(cellData -> createTableCellValue(cellData, assignment -> Optional.ofNullable(AppContext.getInstance().getCourseDTO().getName()).orElse("nulo")));
        assignmentStateTbc.setCellValueFactory(cellData -> createTableCellValue(cellData, AssignmentDTO::getState));
//        assignmentDeadlineTbc.setCellValueFactory(cellData -> createTableCellValue(cellData, AssignmentDTO::getDeadline));//correccion
        courseAssigmentTxf.setText(AppContext.getInstance().getCourseDTO().getName());

        assignmentTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedUser(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedUser(AssignmentDTO assignmentDTO) {
        assigmentIdTxf.setText(String.valueOf(assignmentDTO.getId()));
        assignmentTitleTxf.setText(assignmentDTO.getTitle());
        assigmentDescriptionTxf.setText(assignmentDTO.getDescription());
        assignmentStateComboBox.setText(assignmentDTO.getState().toString());
        assignmentDeadlineDtp.setValue(LocalDate.parse(assignmentDTO.getDeadline().toString()));
    }

    private void loadInitialData() {
        loadAssignments();
    }

    private AssignmentDTO getCurrentAssignment() {
        return AssignmentDTO.builder()
                .id(parseLong(assigmentIdTxf.getText()))
                .title(assignmentTitleTxf.getText())
                .description(assigmentDescriptionTxf.getText())
                .state(assignmentStateComboBox.getValue())
                .course(AppContext.getInstance().getCourseDTO())
                .deadline(assignmentDeadlineDtp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())//ver?
                .build();
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        assigmentIdTxf.clear();
        assignmentTitleTxf.clear();
        assigmentDescriptionTxf.clear();
        assignmentStateComboBox.clear();
        assignmentStateComboBox.setValue(null);
        assignmentDeadlineDtp.setValue(null);
    }

    private void loadAssignments() {
        try {
            MessageResponse<PageDTO<AssignmentDTO>> response = assignmentService.getAssignmentsByCourseId(AppContext.getInstance().getCourseDTO().getId(), currentPage, 10);
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
    public void onActionDeleteAssignmentBtn(ActionEvent event) {
        selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null && showConfirmationMessage("Eliminar Asignación", "¿Estás seguro de que deseas eliminar esta asignación?")) {
            handleUserAction(() -> baseApiServiceManager.deleteEntity(parseLong(assigmentIdTxf.getText())));
            onActionClearFieldsBtn(event);
            loadAssignments();
        } else {
            showNotificationToast("Advertencia", "Por favor, selecciona una asignación para eliminar.");
        }
    }

    @FXML
    public void onActionUpdateAssignmentBtn(ActionEvent event) {
        AssignmentDTO assignmentDTO = getCurrentAssignment();
        handleUserAction(() -> baseApiServiceManager.updateEntity(assignmentDTO.getId(), assignmentDTO));
        onActionClearFieldsBtn(event);
        loadAssignments();
    }

    @FXML
    public void onActionCreateAssignmentBtn(ActionEvent event) {
        AssignmentDTO assignmentDTO = getCurrentAssignment();
        handleUserAction(() -> assignmentService.createEntity(assignmentDTO));
        onActionClearFieldsBtn(event);
        loadAssignments();
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
            ViewManager.getInstance().loadInternalView(Views.SUBMISSIONS);
        }
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

    private Long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return null;
        }
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
        return !assignmentTitleTxf.getText().isEmpty()
                && !assigmentDescriptionTxf.getText().isEmpty() && !assignmentDeadlineDtp.getText().isEmpty()
                && !assignmentStateComboBox.getText().isEmpty();
    }
}