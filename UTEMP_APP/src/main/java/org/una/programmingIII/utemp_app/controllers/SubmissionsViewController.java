package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import org.una.programmingIII.utemp_app.services.models.SubmissionAPIService;
import org.una.programmingIII.utemp_app.services.models.UserAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.io.File;
import java.util.Optional;

public class SubmissionsViewController extends Controller {

    // FXML Elements
    @FXML
    private MFXTextField findByIdTxtF, courseAssigmentTxtF, studentTextF, gradeTxtF, commentaryTxtF;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn, backBtn;
    @FXML
    private TableView<SubmissionDTO> table;
    @FXML
    private TableColumn<SubmissionDTO, String> idC, assignmentC, studentC, gradeC, infoC;
    @FXML
    private Label pageNumberLbl;

    // Services
    private final BaseApiServiceManager<SubmissionDTO> baseApiServiceManager = new SubmissionAPIService();
    private final AssignmentAPIService assignmentAPIService = new AssignmentAPIService();
    private final SubmissionAPIService submissionAPIService = new SubmissionAPIService();
    private final UserAPIService userAPIService = new UserAPIService();
    private final FileAPIService fileAPIService = new FileAPIService();

    private PageDTO<SubmissionDTO> pagesData;
    private int currentPage = 1, maxPage = 1, assignmentID = 0;
    TypeReference<PageDTO<SubmissionDTO>> datoEsperado = new TypeReference<PageDTO<SubmissionDTO>>() {
    };

    SubmissionDTO selectsubmissionDTO;
    AssignmentDTO assignmentDTO;
    UserDTO userDTO;


    @FXML
    public void initialize() {
        setTableView();
        loadSubmissions();
    }

    private void setTableView() {
        table.setEditable(false);
        idC.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentC.setCellValueFactory(new PropertyValueFactory<>("assignment"));
        studentC.setCellValueFactory(new PropertyValueFactory<>("student"));
        gradeC.setCellValueFactory(new PropertyValueFactory<>("grade"));
        infoC.setCellValueFactory(new PropertyValueFactory<>("info"));

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) fillFieldsFromSelectedSubmission(newValue);
        });
    }

    private void loadSubmissions() {
        var response = baseApiServiceManager.getAllEntities(PageRequest.of(currentPage, 10), datoEsperado);
        super.showReadResponse(response);//siempre se devuelve algo solo se debe leer la respuesta
        if (response.isSuccess()) {
            pagesData = response.getData();
            table.setItems(FXCollections.observableArrayList(pagesData.getContent()));
            maxPage = pagesData.getTotalPages();
            updatePageNumber();
        }
    }

    private void updatePageNumber() {
        pageNumberLbl.setText(String.valueOf(currentPage));
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= maxPage);
    }

    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        if (currentPage > 1) loadPage(--currentPage);
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        if (currentPage < maxPage) loadPage(++currentPage);
    }

    private void loadPage(int page) {
        currentPage = page;
        loadSubmissions();
    }

    @FXML
    public void onActionReloadPageBtn(ActionEvent event) {
        loadSubmissions();
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        courseAssigmentTxtF.clear();
        studentTextF.clear();
        gradeTxtF.clear();
        commentaryTxtF.clear();
    }

    @FXML
    public void onActionDeleteBtn(ActionEvent event) {
        Optional.ofNullable(table.getSelectionModel().getSelectedItem())
                .filter(submission -> confirmDelete())
                .ifPresent(this::deleteSubmission);
    }

    @FXML
    public void onActionUpdateBtn(ActionEvent event) {
        Optional.ofNullable(table.getSelectionModel().getSelectedItem())
                .ifPresentOrElse(this::fillFieldsFromSelectedSubmission, () -> showError("Por favor, selecciona una entrega para editar."));
    }

    private void fillFieldsFromSelectedSubmission(SubmissionDTO dto) {
        courseAssigmentTxtF.setText(dto.getAssignment().getTitle());
        studentTextF.setText(dto.getStudent().getName());
        gradeTxtF.setText(String.valueOf(dto.getGrade()));
        commentaryTxtF.setText(dto.getComments());

        selectsubmissionDTO = dto;
    }

    @FXML
    public void onActionCreateBtn(ActionEvent event) {
        if (isAnyFieldEmpty()) {
            showError("Por favor, complete todos los campos.");
            return;
        }

        Optional<SubmissionDTO> existingSubmission = pagesData.getContent().stream()
                .filter(submission -> submission.getId().equals(selectsubmissionDTO.getId()))
                .findFirst();

        if (existingSubmission.isPresent()) {
            SubmissionDTO updatedSubmission = existingSubmission.get();
            updatedSubmission.setAssignment(new AssignmentDTO());//courseAssigmentTxtF.getText())
            updatedSubmission.setStudent(new UserDTO());//studentTextF.getText()
            updatedSubmission.setGrade(Double.parseDouble(gradeTxtF.getText()));
            updatedSubmission.setComments(commentaryTxtF.getText());

            var response = baseApiServiceManager.updateEntity(updatedSubmission.getId(), updatedSubmission);
            super.showReadResponse(response);
            loadSubmissions();
        }
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
        ViewManager.getInstance().loadInternalView(Views.COURSES);
    }

    private boolean isAnyFieldEmpty() {
        return courseAssigmentTxtF.getText().isEmpty() ||
                studentTextF.getText().isEmpty() ||
                gradeTxtF.getText().isEmpty() ||
                commentaryTxtF.getText().isEmpty();
    }

    private void showError(String message) {
        showNotificationToast("Error", message, Alert.AlertType.ERROR);
    }

    private boolean confirmDelete() {
        return showConfirmationMessage("Eliminar Entrega", "¿Estás seguro de que deseas eliminar esta entrega?");
    }

    private void deleteSubmission(SubmissionDTO submission) {
        MessageResponse<Void> response = baseApiServiceManager.deleteEntity(submission.getId());
        loadSubmissions();
        super.showReadResponse(response);
    }

    @FXML
    public void onActionFindByIDBtn(ActionEvent event) {
        String idToFind = findByIdTxtF.getText();
        if (idToFind.isEmpty()) {
            showError("Por favor, ingresa un ID para buscar.");
            return;
        }

        try {
            long id = Long.parseLong(idToFind);
            table.getItems().stream()
                    .filter(submission -> submission.getId().equals(id))
                    .findFirst()
                    .ifPresentOrElse(this::fillFieldsFromSelectedSubmission, () -> showError("No se encontró la entrega con el ID proporcionado."));
        } catch (NumberFormatException e) {
            showError("Por favor, ingresa un ID válido.");
        }
    }

    /*-------------------------*/

    private String upLoadPath;
    private final String downloadPath = System.getProperty("user.home") + "/Downloads";

    @FXML
    public void onActionDownloadFileBtn(ActionEvent event) {
        try {
            if (assignmentID <= 0) {
                showError("ID de asignación inválido.");
                return;
            }

            MessageResponse<Void> response = fileAPIService.downloadFileById((long) assignmentID, downloadPath);
            showReadResponse(response);

        } catch (Exception e) {
            showError("Ocurrió un error al intentar descargar el archivo: " + e.getMessage());
        }
    }

    @FXML
    public void onActionUploadFileBtn(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos", "*.pdf", "*.docx", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile == null) {
                showError("No se seleccionó ningún archivo.");
                return;
            }

            upLoadPath = selectedFile.getAbsolutePath();
            FileMetadatumDTO fileMetadatumDTO = new FileMetadatumDTO();
            Optional<SubmissionDTO> existingSubmission = pagesData.getContent().stream()
                    .filter(submission -> submission.getId().equals(selectsubmissionDTO.getId()))
                    .findFirst();

            if (existingSubmission.isPresent()) {
                SubmissionDTO dto = existingSubmission.get();
                fileMetadatumDTO.setSubmission(dto);
                SubmissionDTO updatedSubmission = existingSubmission.get();
                updatedSubmission.setAssignment(new AssignmentDTO());//courseAssigmentTxtF.getText())
                updatedSubmission.setStudent(new UserDTO());//studentTextF.getText()
                
                updatedSubmission.setGrade(Double.parseDouble(gradeTxtF.getText()));
                updatedSubmission.setComments(commentaryTxtF.getText());
            }

            MessageResponse<Void> response = fileAPIService.uploadFile(upLoadPath, fileMetadatumDTO);
            showReadResponse(response);
        } catch (Exception e) {
            showError("Ocurrió un error al intentar subir el archivo: " + e.getMessage());
        }
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

otra referencia la clase padre:
 @Getter
@Setter
public abstract class Controller {
    protected Stage stage;
    protected MessageResponse<Void> responseVoid = new MessageResponse<>();
    protected AppContext appContext;
    protected int pageNumber = 1, maxPage = 1;

    protected String message;

    public Controller() {
    }

    @FXML
    public abstract void initialize();

    protected void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showNotificationToast(String title, String message, Alert.AlertType alertType) {
        ViewManager.getInstance().createNotification(String.valueOf(alertType), message);
    }

    protected boolean showConfirmationMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED); // Esto elimina la barra de título

        // Obtener los botones y asignarles IDs
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);

        // Asignar un ID para los botones en CSS
        cancelButton.setId("cancelButton");
        okButton.setId("okButton");


        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/alert.css")).toExternalForm());

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    protected void handleError(String message) {
        System.err.println(message);
        showNotificationToast("Error", message, Alert.AlertType.ERROR);
    }

    protected void showNotificationToast(String title, String message) {
        showNotificationToast(title, message, Alert.AlertType.INFORMATION);
    }

    protected TextFormatter<String> createTextFormatter(String regex) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches(regex) ? change : null;
        });
    }

    // Formateadores
    protected TextFormatter<String> textFormatterOnlyNumbers() {
        return createTextFormatter("\\d*");
    }

    protected TextFormatter<String> textFormatterOnlyLetters() {
        return createTextFormatter("[a-zA-Z ]*"); // Permite espacios
    }

    protected void showReadResponse(MessageResponse<?> response) {
        if (response.isSuccess()) {
            showNotificationToast("Éxito", "Operación completada con éxito.", Alert.AlertType.INFORMATION);
        } else {
            handleError("Error: " + response.getErrorMessage());
        }


    }
}
*/

//@FXML
//public void onActionFindByIdBtn(ActionEvent event) {
//    String id = findByIdTxtF.getText();
//    if (id != null && !id.isEmpty()) {
//        try {
//            SubmissionDTO foundSubmission = submissionService.(Long.parseLong(id));
//            if (foundSubmission != null) {
//                fillFieldsFromSelectedSubmission(foundSubmission);
//            } else {
//                showError("No se encontró la entrega con el ID proporcionado.");
//            }
//        } catch (NumberFormatException e) {
//            showError("Por favor, ingresa un ID válido.");
//        }
//    } else {
//        showError("Por favor, ingresa un ID para buscar.");
//    }
//}
