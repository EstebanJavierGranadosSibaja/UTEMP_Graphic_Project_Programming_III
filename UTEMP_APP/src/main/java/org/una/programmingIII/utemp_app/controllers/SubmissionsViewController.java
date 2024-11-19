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
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import org.una.programmingIII.utemp_app.dtos.*;
import org.una.programmingIII.utemp_app.dtos.enums.SubmissionState;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.FileAPIService;
import org.una.programmingIII.utemp_app.services.models.SubmissionAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

public class SubmissionsViewController extends Controller {

    /*---------------------------- Services ----------------------------*/
    private final BaseApiServiceManager<SubmissionDTO> submissionService = new SubmissionAPIService();
    private final SubmissionAPIService submissionAPIService = new SubmissionAPIService();
    private final FileAPIService fileAPIService = new FileAPIService();
    /*---------------------------- FXML Elements ----------------------------*/
    @FXML
    private MFXTextField findByIdTxtF, courseAssignmentTxtF, studentTextF, gradeTxtF, commentaryTxtF, fileUploadPathTxtF;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn, backBtn, findByIDBtn, calificarB, deleteBtn, createBtn, updateBtn;
    @FXML
    private TableView<SubmissionDTO> tableView;
    @FXML
    private TableColumn<SubmissionDTO, String> idC, assignmentC, studentC, gradeC, infoC;
    @FXML
    private Label pageNumberLbl;
    /*---------------------------- Page Data ----------------------------*/
    private PageDTO<SubmissionDTO> pagesData;
    private int currentPage = 0, maxPage = 1;

    /*---------------------------- Selected Data ----------------------------*/
    private SubmissionDTO selectedSubmission;
    private AssignmentDTO assignmentDTO;
    private UserDTO userDTO;
    private FileMetadatumDTO fileMetadatumDTO;
    private final String downloadPath = null; // por defecto

    /*---------------------------- Initialization ----------------------------*/
    @FXML
    public void initialize() {
        setGradeFieldFormatter();

        assignmentDTO = AppContext.getInstance().getAssignmentDTO();
        userDTO = AppContext.getInstance().getUserDTO();
        fileMetadatumDTO = new FileMetadatumDTO();

        studentTextF.setText(userDTO.getName());
        courseAssignmentTxtF.setText(assignmentDTO.getTitle());

        setupTable();
        loadSubmissions();


//        calificarB.setDisable(true);
//        gradeTxtF.setEditable(false);
//        commentaryTxtF.setEditable(false);


//        for (UserPermission p : userDTO.getPermissions()) {
//            if (p == UserPermission.
//            //estudiante) {
//                calificarB.setDisable(false);
//                deleteBtn.setDisable(true);
//                updateBtn.setDisable(true);
//                createBtn.setDisable(true);
//                gradeTxtF.setEditable(true);
//                commentaryTxtF.setEditable(true);
//            }
//        }

        calificarB.setOnAction(event -> onActionCalificar());
        backBtn.setOnAction(event -> ViewManager.getInstance().loadInternalView(Views.ASSIGNMENT));
    }

    /*---------------------------- TableView Setup ----------------------------*/
    private void setupTable() {

        idC.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        assignmentC.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        studentC.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        gradeC.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        infoC.prefWidthProperty().bind(tableView.widthProperty().multiply(0.40));

        tableView.setEditable(false);
        idC.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentC.setCellValueFactory(cellData -> createTableCellValue(cellData, SubmissionDTO::getAsignaciontitle));
        studentC.setCellValueFactory(new PropertyValueFactory<>("studentUniqueName"));
        gradeC.setCellValueFactory(cellData -> createTableCellValue(cellData, submission -> Optional.ofNullable(submission.getGrade()).map(Object::toString).orElse("nulo")));
        infoC.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) populateFields(newValue);
        });
    }

    //creador de valores de la tabla
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
            throw new IllegalArgumentException("no se seleccionó asignación");
        }
        loadPageData(currentPage);
    }

    private void loadPageData(int pageNumber) {
        MessageResponse<PageDTO<SubmissionDTO>> response = submissionAPIService.getSubmissionsByAssignmentId(assignmentDTO.getId(), pageNumber, 10);

        if (response.isSuccess()) {
            pagesData = response.getData();
            System.out.println(pagesData.getContent().toString());
            tableView.setItems(FXCollections.observableArrayList(pagesData.getContent()));
            maxPage = pagesData.getTotalPages();
            updatePageNumber();
        }
    }

    private void updatePageNumber() {
        pageNumberLbl.setText(String.valueOf(currentPage));
        prevPageBtn.setDisable(currentPage <= 0);
        nextPageBtn.setDisable(currentPage >= maxPage);
    }

    private void populateFields(SubmissionDTO dto) {
        courseAssignmentTxtF.setText(dto.getAsignaciontitle());
        studentTextF.setText(dto.getStudentUniqueName());
        gradeTxtF.setText(String.valueOf(dto.getGrade()));
        commentaryTxtF.setText(dto.getComments());
        selectedSubmission = dto;
    }

    /*---------------------------- Action Handlers ----------------------------*/
    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        if (currentPage > 0) loadPageData(--currentPage);
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
        selectedSubmission = tableView.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            super.showError("no hay una valor seleccionado");
            return;
        }

        MessageResponse<Void> response = fileAPIService.deleteMetadataById(selectedSubmission.getId());
        super.showReadResponse(response);
    }

    @FXML
    public void onActionUpdateBtn(ActionEvent event) {
        if (validateInfo()) return;
        selectedSubmission = tableView.getSelectionModel().getSelectedItem();

        if (selectedSubmission.getStudent() == null) {
            selectedSubmission.setStudent(userDTO);
        }

        selectedSubmission.setAssignment(assignmentDTO);

        MessageResponse<SubmissionDTO> response = submissionAPIService.updateSubmission(selectedSubmission.getId(), selectedSubmission);
        super.showReadResponse(response);

        if (response.isSuccess()) {
            selectedSubmission = response.getData();
            fileMetadatumDTO.setSubmission(selectedSubmission);
            handleFileUpload();
            loadSubmissions();
        }
    }

    @FXML
    public void onActionCreateBtn(ActionEvent event) {
        if (validateInfo()) return;
        selectedSubmission = createSubmission();
        MessageResponse<SubmissionDTO> response = submissionAPIService.createSubmission(selectedSubmission);
        super.showReadResponse(response);

        if (response.isSuccess()) {
            selectedSubmission = response.getData();
            fileMetadatumDTO.setSubmission(selectedSubmission);
            handleFileUpload();
            loadSubmissions();
            clearFields();
        }
    }

    public void onActionCalificar() {
        if (gradeTxtF.getText() == null) {
            super.showError("no hay una calificacion");
            return;
        }
        selectedSubmission = tableView.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            super.showError("no hay una valores selecionados");
            return;
        }

        selectedSubmission.setStudent(UserDTO.builder().id(selectedSubmission.getStudeId()).build());
        selectedSubmission.setAssignment(assignmentDTO);

        selectedSubmission.setGrade(parseGrade(gradeTxtF.getText()));
        selectedSubmission.setComments(commentaryTxtF.getText());

        MessageResponse<SubmissionDTO> response = submissionAPIService.updateSubmission(selectedSubmission.getId(), selectedSubmission);
        super.showReadResponse(response);
    }

    private boolean validateInfo() {
        if (isAnyFieldEmpty()) {
            super.showError("No están definidos los datos");
            return false;
        }
        if (fileUploadPathTxtF.getText().isEmpty() || validaAndLoadFileInfo()) {
            super.showError("Path no válido");
            return false;
        }
        return true;
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

        selectedSubmission = tableView.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            return;
        }

        MessageResponse<Void> response = fileAPIService.downloadFile(selectedSubmission.getMetadataID());
        super.showReadResponse(response);
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
        System.out.println(fileMetadatumDTO.toString());
        if (fileMetadatumDTO == null) {
            super.showError("El archivo no fue correctamente inicializado.");
            return; // Evita continuar si fileMetadatumDTO es null
        }

        MessageResponse<FileMetadatumDTO> response = fileAPIService.createMetadata(fileMetadatumDTO);

        super.showReadResponse(response);
        if (response.isSuccess()) {
            fileMetadatumDTO = response.getData();
        } else {
            return;
        }

        fileMetadatumDTO.setSubmission(selectedSubmission);
        fileMetadatumDTO.setStudent(userDTO);

        MessageResponse<Void> response2 = fileAPIService.upLoadFile(fileMetadatumDTO, fileUploadPathTxtF.getText());
        if (!response2.isSuccess()) {
            super.showError(response2.getErrorMessage());
        }
    }

    private boolean validaAndLoadFileInfo() {
        String path = fileUploadPathTxtF.getText();

        File selectedFile = new File(path);
        if (selectedFile.exists() && selectedFile.isFile()) {
            seleccionarArchivo(selectedFile);
            return true;
        }
        return false;
    }

    private void seleccionarArchivo(File selectedFile) {
        if (selectedFile != null) {
            fileMetadatumDTO = prepareFileMetadata(selectedFile);
        } else {
            super.showError("No se seleccionó ningún archivo.");
        }
    }

    private FileMetadatumDTO prepareFileMetadata(File selectedFile) {
        if (selectedFile == null || !selectedFile.exists()) {
            throw new IllegalArgumentException("El archivo seleccionado no es válido.");
        }

        // Asegúrate de que el nombre del archivo y el tamaño sean válidos
        if (selectedFile.getName() == null || selectedFile.length() <= 0) {
            throw new IllegalArgumentException("El archivo debe tener un nombre y un tamaño válido.");
        }

        return FileMetadatumDTO.builder()
                .submission(selectedSubmission)
                .student(userDTO)
                .fileName(selectedFile.getName()) // Asegúrate de que esto no sea null
                .fileType(getFileExtension(selectedFile))
                .fileSize(selectedFile.length()) // Asegúrate de que esto no sea 0
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

    private SubmissionDTO createSubmission() {
        if (assignmentDTO == null || userDTO == null)
            throw new IllegalArgumentException("No hay datos definidos para usuario o asignacion");

        double grade = parseGrade(gradeTxtF.getText().trim());
        String comments = Optional.ofNullable(commentaryTxtF.getText())
                .filter(text -> !text.trim().isEmpty())
                .orElse(" ");

        return SubmissionDTO.builder()
                .state(SubmissionState.SUBMITTED)
                .assignment(assignmentDTO)
                .student(userDTO)
                .fileName("Default")
                .comments(comments)
                .grade(grade)
                .build();
    }

    private double parseGrade(String gradeText) {
        if (gradeText != null && !gradeText.isEmpty()) {
            try {
                return Double.parseDouble(gradeText);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0.0;  // Valor por defecto
    }

    private void deleteSubmission(SubmissionDTO submission) {
        var response = submissionAPIService.deleteSubmission(submission.getId());
        if (response.isSuccess()) loadSubmissions();
    }

    //file manage
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

    //formateador
    private void setGradeFieldFormatter() {
        TextFormatter<Double> formatter = new TextFormatter<>(
                new DoubleStringConverter(),
                0.0,
                c -> {
                    String text = c.getControlNewText();
                    if (text.matches("^([0-9]|10)(\\.\\d{0,1})?$")) {
                        return c;
                    }
                    return null;
                });
        gradeTxtF.setTextFormatter(formatter);
    }

}