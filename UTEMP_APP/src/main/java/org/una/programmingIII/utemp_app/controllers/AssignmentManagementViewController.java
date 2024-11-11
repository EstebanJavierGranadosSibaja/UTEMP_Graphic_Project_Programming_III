package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.data.domain.Page;
import org.una.programmingIII.utemp_app.dtos.AssignmentDTO;
import org.una.programmingIII.utemp_app.services.models.AssignmentAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

public class AssignmentManagementViewController extends Controller {

    @FXML
    private MFXTextField findByIdTxf, assigmentIdTxf, courseAssigmentTxf, assignmentTitleTxf, assigmentDescriptionTxf;
    @FXML
    private MFXComboBox<String> assigmentStateTxf;
    @FXML
    private MFXDatePicker assignmentDeadlineDtp;
    @FXML
    private MFXButton findByCourseIdBtn, reloadPageBtn, prevPageBtn, nextPageBtn, clearFieldsBtn, deleteBtn, updateBtn, createBtn, backBtn, downloadFilesBtn, loadFilesBtn;
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
    private Page<AssignmentDTO> assignments;


    @FXML
    public void initialize() {
        // Inicialización de la interfaz de usuario
        loadAssignments();

        baseApiServiceManager = new AssignmentAPIService();
        assignmentService = new AssignmentAPIService();
    }

    private void loadAssignments() {
//        List<AssignmentDTO> assignments = assignmentService.getAllAssignments();
        assignmentTbv.getItems().clear();
//        assignmentTbv.getItems().addAll(assignments);
    }

    @FXML
    public void onActionFindByCourseIdBtn(ActionEvent event) {
        String courseId = findByIdTxf.getText();
//        List<AssignmentDTO> assignments = assignmentService.getAssignmentsByCourseId(courseId);
        assignmentTbv.getItems().clear();
//        assignmentTbv.getItems().addAll(assignments);
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadAssignments();
    }

    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        int currentPage = Integer.parseInt(pageNumberLbl.getText());
        if (currentPage > 1) {
            pageNumberLbl.setText(String.valueOf(currentPage - 1));
            loadAssignments(); // Aquí se debería cargar la página anterior de asignaciones.
        }
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        int currentPage = Integer.parseInt(pageNumberLbl.getText());
        pageNumberLbl.setText(String.valueOf(currentPage + 1));
        loadAssignments(); // Aquí se debería cargar la página siguiente de asignaciones.
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        assigmentIdTxf.clear();
        courseAssigmentTxf.clear();
        assignmentTitleTxf.clear();
        assigmentDescriptionTxf.clear();
        assigmentStateTxf.clear();
        assignmentDeadlineDtp.clear();
    }

    @FXML
    public void onActionDeleteAssignmentBtn(ActionEvent event) {
        AssignmentDTO selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
//        if (selectedAssignment != null) {
//            boolean success = assignmentService.deleteAssignment(selectedAssignment.getId());
//            if (success) {
//                loadAssignments();
//                showAlert("Eliminación Exitosa", "La asignación se ha eliminado correctamente.");
//            } else {
//                showAlert("Error", "No se pudo eliminar la asignación.");
//            }
//        } else {
//            showAlert("Advertencia", "Por favor, seleccione una asignación para eliminar.");
//        }
    }

    @FXML
    public void onActionUpdateAssignmentBtn(ActionEvent event) {
        AssignmentDTO selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null) {
            assigmentIdTxf.setText(String.valueOf(selectedAssignment.getId()));
            assignmentTitleTxf.setText(selectedAssignment.getTitle());
            assigmentDescriptionTxf.setText(selectedAssignment.getDescription());
//            assigmentStateTxf.setSelectedItem(selectedAssignment.getState());
//            assignmentDeadlineDtp.setValue(selectedAssignment.getDeadline().toLocalDate());
        } else {
            showNotificationToast("Advertencia", "Por favor, seleccione una asignación para editar.");
        }
    }

    @FXML
    public void onActionCreateAssignmentBtn(ActionEvent event) {
        String title = assignmentTitleTxf.getText();
        String description = assigmentDescriptionTxf.getText();
        String state = assigmentStateTxf.getSelectedItem();
        String deadline = assignmentDeadlineDtp.getValue().toString();

//        AssignmentDTO newAssignment = new AssignmentDTO(title, description, state, deadline);
//        boolean success = assignmentService.createAssignment(newAssignment);
//        if (success) {
//            loadAssignments();
//            showAlert("Creación Exitosa", "La asignación se ha creado correctamente.");
//        } else {
//            showAlert("Error", "No se pudo crear la asignación.");
//        }
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.MENU); // Cambiar a la vista que corresponda
    }


    @FXML
    public void onActiondownLoadFilesBtn(ActionEvent event) {
        showNotificationToast("Descarga", "Descargando archivos...");
    }

    @FXML
    public void onActionLoadFilesBtn(ActionEvent event) {
        showNotificationToast("Carga", "Cargando archivos...");
    }
}