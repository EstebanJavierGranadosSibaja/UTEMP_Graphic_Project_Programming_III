package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.una.programmingIII.utemp_app.dtos.*;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.FileAPIService;
import org.una.programmingIII.utemp_app.services.models.SubmissionAPIService;
import org.una.programmingIII.utemp_app.utils.DTOFiller;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

public class SubmissionsViewController extends Controller {

    /*---------------------------- FXML Elements ----------------------------*/
    @FXML
    private MFXTextField findByIdTxtF, courseAssignmentTxtF, studentTextF, gradeTxtF, commentaryTxtF, fileUploadPathTxtF;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn, backBtn;
    @FXML
    private TableView<SubmissionDTO> tableView;
    @FXML
    private TableColumn<SubmissionDTO, String> idC, assignmentC, studentC, gradeC, infoC;
    @FXML
    private Label pageNumberLbl;

    /*---------------------------- Services ----------------------------*/
    private final BaseApiServiceManager<SubmissionDTO> submissionService = new SubmissionAPIService();
    private final FileAPIService fileAPIService = new FileAPIService();
    private final SubmissionAPIService submissionAPIService = new SubmissionAPIService();

    /*---------------------------- Page Data ----------------------------*/
    private PageDTO<SubmissionDTO> pagesData;
    private int currentPage = 1, maxPage = 1;

    /*---------------------------- Selected Data ----------------------------*/
    private SubmissionDTO selectedSubmission;
    private AssignmentDTO assignmentDTO;
    private UserDTO userDTO;
    private FileMetadatumDTO fileMetadatumDTO;
    private String downloadPath = null; // por defecto

    /*---------------------------- Initialization ----------------------------*/
    @FXML
    public void initialize() {
        assignmentDTO = AppContext.getInstance().getAssignmentDTO();
        userDTO = AppContext.getInstance().getUserDTO();

        DTOFiller dto = new DTOFiller();
        assignmentDTO = dto.getAssignmentDTO();

        studentTextF.setText(userDTO.getName());
        courseAssignmentTxtF.setText(assignmentDTO.getTitle());

        setupTable();
        loadSubmissions();
    }

    /*---------------------------- TableView Setup ----------------------------*/
    private void setupTable() {
        tableView.setEditable(false);
        idC.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentC.setCellValueFactory(cellData -> createTableCellValue(cellData, submission -> submission.getAssignment().getTitle()));
        studentC.setCellValueFactory(cellData -> createTableCellValue(cellData, submission -> submission.getStudent().getName()));
        gradeC.setCellValueFactory(cellData -> createTableCellValue(cellData, submission -> Optional.ofNullable(submission.getGrade()).map(Object::toString).orElse("nulo")));
        infoC.setCellValueFactory(cellData -> createTableCellValue(cellData, SubmissionDTO::getComments));

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) populateFields(newValue);
        });
    }

    private <T> SimpleStringProperty createTableCellValue(TableColumn.CellDataFeatures<SubmissionDTO, String> cellData, Function<SubmissionDTO, T> getter) {
        return new SimpleStringProperty(Optional.ofNullable(cellData.getValue())
                .map(getter)
                .map(Object::toString)
                .orElse("nulo"));
    }

    /*---------------------------- Data Management ----------------------------*/
    private void loadSubmissions() {
        if (assignmentDTO == null) {
            showError("Assignment not selected");
            return;
        }
        loadPageData(currentPage);
    }

    private void loadPageData(int pageNumber) {
        var response = submissionAPIService.getSubmissionsByAssignmentId(assignmentDTO.getId(), pageNumber, 10);
        if (response.isSuccess()) {
            pagesData = response.getData();
            tableView.setItems(FXCollections.observableArrayList(pagesData.getContent()));
            maxPage = pagesData.getTotalPages();
            updatePageNumber();
        }
    }

    private void updatePageNumber() {
        pageNumberLbl.setText(String.valueOf(currentPage));
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= maxPage);
    }

    private void populateFields(SubmissionDTO dto) {
        courseAssignmentTxtF.setText(dto.getAssignment().getTitle());
        studentTextF.setText(dto.getStudent().getName());
        gradeTxtF.setText(String.valueOf(dto.getGrade()));
        commentaryTxtF.setText(dto.getComments());
        selectedSubmission = dto;
    }

    /*---------------------------- Action Handlers ----------------------------*/
    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        if (currentPage > 1) loadPageData(--currentPage);
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        if (currentPage < maxPage) loadPageData(++currentPage);
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        clearFields();
    }

    @FXML
    public void onActionDeleteBtn(ActionEvent event) {
        Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                .filter(submission -> super.showConfirmationMessage("Borrar", "Seguro de eliminar la tarea?"))
                .ifPresent(this::deleteSubmission);
    }

    @FXML
    public void onActionUpdateBtn(ActionEvent event) {
        if (selectedSubmission != null) {
            updateSubmissionData();
            var response = submissionService.updateEntity(selectedSubmission.getId(), selectedSubmission);
            super.showReadResponse(response);
            if (response.isSuccess()) {
                handleFileUpload();
                loadSubmissions();
            }
        }
    }

    @FXML
    public void onActionCreateBtn(ActionEvent event) {
        if (isAnyFieldEmpty()) {
            super.showError("Por favor, complete todos los campos.");
        } else {
            selectedSubmission = createSubmission();
            var response = submissionAPIService.createSubmission(selectedSubmission);
            super.showReadResponse(response);
            if (response.isSuccess()) {
                handleFileUpload();
                loadSubmissions();
            }
        }
        fileMetadatumDTO = null;
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.COURSES);
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent actionEvent) {
        loadPageData(currentPage);
    }

    /*---------------------------- File Upload/Download ----------------------------*/
    @FXML
    public void onActionDownloadFileBtn(ActionEvent event) {
        try {
            MessageResponse<Void> response = fileAPIService.downloadFileById(assignmentDTO.getId(), downloadPath);
            if (!response.isSuccess()) {
                super.showError(response.getErrorMessage());
            }
        } catch (Exception e) {
            super.showError("Error al descargar el archivo: " + e.getMessage());
        }
    }

    @FXML
    public void onActionLoadPathB(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos", "*.pdf", "*.docx", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileUploadPathTxtF.setText(selectedFile.getAbsolutePath());
        } else {
            super.showError("No se seleccionó ningún archivo.");
        }
    }

    private void handleFileUpload() {
        loadFIleInfo();
        fileMetadatumDTO.setId(null);
        fileMetadatumDTO.setStudent(userDTO);
        selectedSubmission.setId(1L);
        fileMetadatumDTO.setSubmission(selectedSubmission);

        var response = fileAPIService.uploadFile(fileUploadPathTxtF.getText(), fileMetadatumDTO);
        if (!response.isSuccess()) {
            super.showError(response.getErrorMessage());
        }
    }

    private void loadFIleInfo() {
        String path = fileUploadPathTxtF.getText();

        if (path.isEmpty()) {
            super.showError("La ruta del archivo no puede estar vacía.");
            return;
        }

        File selectedFile = new File(path);
        if (selectedFile.exists() && selectedFile.isFile()) {
            seleccionarArchivo(selectedFile);
        } else {
            super.showError("La ruta proporcionada no es válida o el archivo no existe.");
        }
    }

    private void seleccionarArchivo(File selectedFile) {
        if (selectedFile != null) {
            fileMetadatumDTO = prepareFileMetadata(selectedFile);
            fileUploadPathTxtF.setText(selectedFile.getAbsolutePath());
        } else {
            super.showError("No se seleccionó ningún archivo.");
        }
    }

    private FileMetadatumDTO prepareFileMetadata(File selectedFile) {
        return FileMetadatumDTO.builder()
                .submission(selectedSubmission)
                .student(userDTO)
                .fileName(getFileNameWithoutExtension(selectedFile))
                .fileSize(selectedFile.length())
                .fileType(getFileExtension(selectedFile))
                .build();
    }

    /*---------------------------- Helper Methods ----------------------------*/
    private boolean isAnyFieldEmpty() {
        return courseAssignmentTxtF.getText().isEmpty() || studentTextF.getText().isEmpty();
    }

    private void clearFields() {
        courseAssignmentTxtF.clear();
        studentTextF.clear();
        gradeTxtF.clear();
        commentaryTxtF.clear();

        studentTextF.setText(userDTO.getName());
        courseAssignmentTxtF.setText(assignmentDTO.getTitle());
    }

    private void updateSubmissionData() {
        selectedSubmission.setAssignment(assignmentDTO);
        selectedSubmission.setStudent(userDTO);
        selectedSubmission.setGrade(Double.parseDouble(gradeTxtF.getText()));
        selectedSubmission.setComments(commentaryTxtF.getText());
    }

    private SubmissionDTO createSubmission() {
        return SubmissionDTO.builder()
                .id(10L)
                .assignment(assignmentDTO)
                .student(userDTO)
                .grade(Double.parseDouble(gradeTxtF.getText()))
                .comments(commentaryTxtF.getText())
                .build();
    }

    private void deleteSubmission(SubmissionDTO submission) {
        var response = submissionAPIService.deleteSubmission(submission.getId());
        if (response.isSuccess()) loadSubmissions();
    }

    private String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }

}



/*
referencia
@Getter
@Setter
@Builder
public class MessageResponse<T> {
    private T data;
    private boolean success;
    private String titleMessage;
    private String errorMessage;

    public MessageResponse() {
    }

    public MessageResponse(T data, boolean success, String titleMessage, String errorMessage) {
        this.data = data;
        this.success = success;
        this.titleMessage = titleMessage;
        this.errorMessage = errorMessage;
    }
}


package org.una.programmingIII.utemp_app.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FileMetadatumDTO {

    private Long id;// predefinodo

    @Builder.Default
    private SubmissionDTO submission = new SubmissionDTO();//predefinido

    @Builder.Default
    private UserDTO student = new UserDTO(); // predefinido

    @NotNull(message = "File name must not be null")
    @Size(max = 255, message = "File name must be at most 255 characters long")
    private String fileName;// se define durante la subida

    @NotNull(message = "File size must not be null")
    private Long fileSize;// se define durante la subida

    @Size(max = 100, message = "File type must be at most 100 characters long")
    private String fileType;// se define durante la subida

    private LocalDateTime createdAt;// se define durante la subida

    private LocalDateTime lastUpdate;// se define durante la subida

    // Nuevos campos para manejo de fragmentos
    private byte[] fileChunk; // Fragmento del archivo// se define durante la subida
    private int chunkIndex; // Índice del fragmento// se define durante la subida
    private int totalChunks; // Número total de fragmentos// se define durante la subida

    @Size(max = 500, message = "Storage path must be at most 500 characters long")
    private String storagePath;// se define durante la subida
}

@Setter
@Getter
public class PageDTO<T> {
    // Getters y setters
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;

    // Constructor

    public PageDTO() {

    }

    public PageDTO(List<T> content, int totalPages, long totalElements, int number, int size) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.number = number;
        this.size = size;
    }
}
}


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDTO {

    private Long id;

    @NotNull(message = "Assignment must not be null")
    @Builder.Default
    private AssignmentDTO assignment = new AssignmentDTO();

    @NotNull(message = "Student must not be null")
    @Builder.Default
    private UserDTO student = new UserDTO();

    @NotBlank(message = "File name must not be blank")
    @Size(max = 255, message = "File name must be at most 255 characters long")
    private String fileName;

    private Double grade;

    @Size(max = 500, message = "Comments must be at most 500 characters long")
    private String comments;

    @Builder.Default
    private List<GradeDTO> grades = new ArrayList<>();

    @Builder.Default
    private List<FileMetadatumDTO> fileMetadata = new ArrayList<>();

    @NotNull(message = "State must not be null")
    private SubmissionState state;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdate;
}

*/