package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.dtos.*;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.AssignmentAPIService;
import org.una.programmingIII.utemp_app.services.models.FileAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.io.File;
import java.time.LocalDate;

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
    private FileAPIService fileAPIService;
    private BaseApiServiceManager<AssignmentDTO> baseApiServiceManager;
    private PageDTO<AssignmentDTO> assignments;
    private AssignmentDTO assignmentDTO;
    private FileMetadatumDTO fileMetadatumDTO;
    private int currentPage = 1;
    private int maxPage = 1;

    @FXML
    public void initialize() {
        baseApiServiceManager = new AssignmentAPIService();
        assignmentService = new AssignmentAPIService();
        fileAPIService = new FileAPIService();
        setTableView();
        loadAssignments();
    }

    private void setTableView() {
        assignmentTbv.setEditable(false);
        assignmentdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentTitleTbc.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseNameTbc.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        assignmentStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));
        assignmentDeadlineTbc.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        // Listener para llenar los campos cuando una asignación es seleccionada en la tabla
        assignmentTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedAssignment(newValue);
            }
        });
    }

    private void loadAssignments() {
        try {
            MessageResponse<PageDTO<AssignmentDTO>> response = baseApiServiceManager.getAllEntities(
                    PageRequest.of(currentPage, 10), new TypeReference<PageDTO<AssignmentDTO>>() {//TODO
                    });
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
        assignmentTbv.getItems().clear();
        assignmentTbv.setItems(FXCollections.observableArrayList(page.getContent()));
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
            loadAssignments(); // Cargar la página anterior
        }
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        if (currentPage < maxPage) {
            currentPage++;
            loadAssignments(); // Cargar la página siguiente
        }
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadAssignments(); // Recargar las asignaciones de la página actual
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
        if (selectedAssignment != null) {
            if (showConfirmationMessage("Eliminar Asignación", "¿Estás seguro de que deseas eliminar esta asignación?")) {
                try {
                    baseApiServiceManager.deleteEntity(selectedAssignment.getId());
                    loadAssignments(); // Recargar las asignaciones
                    showNotificationToast("Éxito", "Asignación eliminada con éxito.");
                } catch (Exception e) {
                    showError("Error al eliminar la asignación: " + e.getMessage());
                }
            }
        } else {
            showNotificationToast("Advertencia", "Por favor, selecciona una asignación para eliminar.");
        }
    }

    @FXML
    public void onActionUpdateAssignmentBtn(ActionEvent event) {
        AssignmentDTO selectedAssignment = assignmentTbv.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null) {
            fillFieldsFromSelectedAssignment(selectedAssignment);
        } else {
            showNotificationToast("Advertencia", "Por favor, selecciona una asignación para editar.");
        }
    }

    private void fillFieldsFromSelectedAssignment(AssignmentDTO assignment) {
        assigmentIdTxf.setText(String.valueOf(assignment.getId()));
        assignmentTitleTxf.setText(assignment.getTitle());
        assigmentDescriptionTxf.setText(assignment.getDescription());
//        assigmentStateTxf.setSelectedItem(assignment.getState());
//        assignmentDeadlineDtp.setValue(LocalDate.parse(assignment.getDeadline()));

        assignmentDTO = assignment;
    }

    @FXML
    public void onActionCreateAssignmentBtn(ActionEvent event) {
        String title = assignmentTitleTxf.getText();
        String description = assigmentDescriptionTxf.getText();
        String state = assigmentStateTxf.getSelectedItem();
        LocalDate deadline = assignmentDeadlineDtp.getValue();

        if (title.isEmpty() || description.isEmpty() || state == null || deadline == null) {
            showError("Por favor, complete todos los campos.");
            return;
        }

        AssignmentDTO newAssignment = AssignmentDTO.builder().build(); //new AssignmentDTO(title, description, state, deadline.toString());
        try {
            baseApiServiceManager.createEntity(newAssignment);
            loadAssignments(); // Recargar las asignaciones
            showNotificationToast("Éxito", "Asignación creada con éxito.");
        } catch (Exception e) {
            showError("Error al crear la asignación: " + e.getMessage());
        }
    }

    @FXML
    public void onActionFindByIDBtn(ActionEvent event) {
        String idText = findByIdTxf.getText().trim();
        if (!idText.isEmpty()) {
            try {
//                Long assignmentId = Long.parseLong(idText);
//                MessageResponse<AssignmentDTO> response = baseApiServiceManager.getEntity(assignmentId, AssignmentDTO.class);
//                if (response.isSuccess()) {
//                    AssignmentDTO foundAssignment = response.getData();
//                    assignmentTbv.getItems().clear();
//                    assignmentTbv.getItems().add(foundAssignment);
//                } else {
//                    showError("Asignación no encontrada.");
//                }
            } catch (NumberFormatException e) {
                showError("Por favor, ingrese un ID válido.");
            }
        } else {
            showError("Por favor, ingrese un ID para buscar.");
        }
    }

//    @FXML
//    public void onActionFindByCourseIdBtn(ActionEvent event) {
//        String courseId = courseAssigmentTxf.getText().trim();
//        if (!courseId.isEmpty()) {
//            // Lógica para filtrar las asignaciones por el ID del curso
//            // Implementar la lógica según la API, aquí solo es un ejemplo.
////            MessageResponse<PageDTO<AssignmentDTO>> response = baseApiServiceManager.getAssignmentsByCourseId(courseId, PageRequest.of(currentPage - 1, 10));
////            if (response.isSuccess()) {
////                assignments = response.getData();
////                loadTable(assignments);
////                updatePageNumber();
////            } else {
////                showError("No se encontraron asignaciones para este curso.");
////            }
//        } else {
////            showError("Por favor, ingrese un ID de curso para buscar.");
//        }
//    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.COURSES);
    }

    /*-------------------------*/
    long assignmentID;
    String fileID;
    String upLoadPath;
    String downloadPath = null; // ruta por defecto, usar simpre esta

    @FXML
    public void onActionDownloadFileBtn(ActionEvent event) {
        try {
            // Verificar si assignmentID es válido
            if (assignmentID <= 0) {
                showError("ID de asignación inválido.");
                return;
            }

            // Usar la ruta de descarga por defecto
//            String downloadPath = getDefaultDownloadPath();

            // Llamada al servicio para descargar el archivo
            MessageResponse<Void> response = fileAPIService.downloadFileById(assignmentID, downloadPath);

            // Mostrar la respuesta del servicio
            showReadResponse(response);

        } catch (Exception e) {
            showError("Ocurrió un error al intentar descargar el archivo: " + e.getMessage());
        }
    }

    @FXML
    public void onActionUploadFileBtn(ActionEvent event) {
        try {
            // Abrir el selector de archivos de Windows
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos", "*.pdf", "*.docx", "*.txt"));

            // Abrir el diálogo para seleccionar un archivo
            File selectedFile = fileChooser.showOpenDialog(null);

            // Si no se seleccionó archivo, salir
            if (selectedFile == null) {
                showError("No se seleccionó ningún archivo.");
                return;
            }

            // Validar el tamaño del archivo (por ejemplo, no debe ser mayor a 10MB)
            if (selectedFile.length() > 10L * 1024 * 1024) {
                showError("El archivo seleccionado es demasiado grande. El tamaño máximo es 10MB.");
                return;
            }

            // Obtener la ruta del archivo seleccionado
            upLoadPath = selectedFile.getAbsolutePath();

            // Crear los metadatos para el archivo (aquí asumo que tienes alguna forma de llenar estos datos)
            FileMetadatumDTO fileMetadatumDTO = FileMetadatumDTO.builder()
                    .student(get())
                    .id(1L)
//                    .submission()
                    .build(); 
                    //createFileMetadata(selectedFile);

            // Llamar al servicio para subir el archivo
            MessageResponse<Void> response = fileAPIService.uploadFile(upLoadPath, fileMetadatumDTO);

            // Mostrar la respuesta del servicio
            showReadResponse(response);

        } catch (Exception e) {
            showError("Ocurrió un error al intentar subir el archivo: " + e.getMessage());
        }
    }

    UserDTO get(){
         UserDTO user =UserDTO.builder()
                .id(1L)
                .build();
        return user;
    }

    SubmissionDTO getS(){
        SubmissionDTO s =  SubmissionDTO.builder()
//                .id(1L)

                .build();
        return s;
    }

    private FileMetadatumDTO createFileMetadata(File selectedFile) {
        FileMetadatumDTO fileMetadatumDTO = new FileMetadatumDTO();
        fileMetadatumDTO.setFileName(selectedFile.getName());
        fileMetadatumDTO.setFileSize(selectedFile.length());
        fileMetadatumDTO.setStoragePath(selectedFile.getAbsolutePath());
        // Aquí puedes añadir otros metadatos si es necesario
        return fileMetadatumDTO;
    }

    private void showError(String message) {
        showNotificationToast("Error", message, Alert.AlertType.ERROR);
    }

    private String getDefaultDownloadPath() {
        // Definir una ruta por defecto para la descarga
        return System.getProperty("user.home") + "/Downloads";  // Cambiar si es necesario
    }
}
