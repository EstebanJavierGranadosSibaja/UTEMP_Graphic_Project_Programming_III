package org.una.programmingIII.utemp_app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
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
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.dtos.enums.UserRole;
import org.una.programmingIII.utemp_app.dtos.enums.UserState;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.models.UserAPIService;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;
import org.una.programmingIII.utemp_app.utils.view.AppContext;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.util.List;
import java.util.function.Supplier;

public class UserManagementViewController extends Controller {

    @FXML
    private MFXTextField userIdTxf, userIdNumberTxf, userNameTxf, userEmailTxf, findByIdNumberTxf;
    @FXML
    private MFXComboBox<UserState> userStateCbx;
    @FXML
    private MFXButton prevPageBtn, nextPageBtn;
    @FXML
    private TableView<UserDTO> usersTbv;
    @FXML
    private TableColumn<UserDTO, Long> userIdTbc;
    @FXML
    private TableColumn<UserDTO, String> userIdNumberTbc, userNameTbc, emailTbc;
    @FXML
    private TableColumn<UserDTO, UserRole> userRoleTbc;
    @FXML
    private TableColumn<UserDTO, UserState> userStateTbc;
    @FXML
    private Label pageNumberLbl;

    private BaseApiServiceManager<UserDTO> baseApiServiceManager;
    private UserAPIService userAPIService;
    private boolean initFlag = false;
    private int pageNumber = 0, maxPage = 1;

    @Override
    public void initialize() {

        ObservableList<UserState> states = FXCollections.observableArrayList(UserState.ACTIVE, UserState.INACTIVE, UserState.SUSPENDED, UserState.DEPRECATED);
        userStateCbx.setItems(states);

        if (!initFlag) {
            userAPIService = new UserAPIService();
            baseApiServiceManager = userAPIService;
            setTableView();
            loadInitialData();
            initFlag = true;
        }
    }

    private void setTableView() {
        double valor = 0.166666;

        userIdTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));
        userIdNumberTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));
        userIdNumberTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));
        userNameTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));
        emailTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));
        userRoleTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));
        userStateTbc.prefWidthProperty().bind(usersTbv.widthProperty().multiply(valor));

        usersTbv.setEditable(false);
        userIdTbc.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdNumberTbc.setCellValueFactory(new PropertyValueFactory<>("identificationNumber"));
        emailTbc.setCellValueFactory(new PropertyValueFactory<>("email"));
        userNameTbc.setCellValueFactory(new PropertyValueFactory<>("name"));
        userRoleTbc.setCellValueFactory(new PropertyValueFactory<>("role"));
        userStateTbc.setCellValueFactory(new PropertyValueFactory<>("state"));
        usersTbv.setPlaceholder(new Label("No hay usuarios disponibles."));

        usersTbv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsFromSelectedUser(newValue);
            }
        });
    }

    private void fillFieldsFromSelectedUser(UserDTO user) {
        userIdTxf.setText(String.valueOf(user.getId()));
        userNameTxf.setText(user.getName());
        userIdNumberTxf.setText(user.getIdentificationNumber());
        userEmailTxf.setText(user.getEmail());
        userStateCbx.setValue(user.getState());
    }

    private void loadInitialData() {
        loadPage(pageNumber);
    }

    private UserDTO getCurrentUser() {
        System.out.println(userStateCbx.getValue());

        return UserDTO.builder()
                .id(parseLong(userIdTxf.getText()))
                .name(userNameTxf.getText())
                .identificationNumber(userIdNumberTxf.getText())
                .email(userEmailTxf.getText())
                .state(userStateCbx.getValue())
                .build();
    }

    @FXML
    public void onActionCreateUserBtn(ActionEvent event) {
        UserDTO userDTO = getCurrentUser();
        userDTO.setId(null);
        userDTO.setRole(UserRole.ADMIN);
        userDTO.setPassword(userIdNumberTxf.getText());
        userDTO.setPermissions(AppContext.getInstance().getUserDTO().getPermissions());
        handleUserAction(() -> baseApiServiceManager.createEntity(userDTO));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
    }

    @FXML
    public void onActionUpdateUserBtn(ActionEvent event) {
        UserDTO userDTO = getCurrentUser();
        userDTO.setRole(userAPIService.getCurrentUser().getData().getRole());
        userDTO.setPassword(userAPIService.getCurrentUser().getData().getPassword());
        userDTO.setPermissions(AppContext.getInstance().getUserDTO().getPermissions());
        handleUserAction(() -> baseApiServiceManager.updateEntity(userDTO.getId(), userDTO));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
    }

    @FXML
    public void onActionDeleteUserBtn(ActionEvent event) {
        handleUserAction(() -> baseApiServiceManager.deleteEntity(parseLong(userIdTxf.getText())));
        onActionClearFieldsBtn(event);
        loadPage(pageNumber);
    }

    @FXML
    public void onActionPermissionsBtn(ActionEvent event) {
        UserDTO userToShow = usersTbv.getSelectionModel().getSelectedItem();

        if (userIdTxf.getText() == null || userIdTxf.getText().isEmpty()) {
            userToShow = getCurrentUser();
        }

        AppContext.getInstance().setUserDTO(userToShow);
        ViewManager.getInstance().showModalView(Views.PERMISSIONS);
    }

    @FXML
    public void onActionClearFieldsBtn(ActionEvent event) {
        userIdTxf.clear();
        userNameTxf.clear();
        userEmailTxf.clear();
        userIdNumberTxf.clear();
        userStateCbx.getSelectionModel().clearSelection();
        userStateCbx.setValue(null);
        usersTbv.getSelectionModel().clearSelection();
    }


    @FXML
    public void onActionPrevPageBtn(ActionEvent event) {
        navigatePage(pageNumber - 1);
    }

    @FXML
    public void onActionNextPageBtn(ActionEvent event) {
        navigatePage(pageNumber + 1);
    }

    @FXML
    public void onActionFindByIdNumberBtn(ActionEvent event) {
        String idToFind = findByIdNumberTxf.getText().trim();
        if (idToFind.isEmpty()) {
            showError("Por favor, ingresa un número de identificación para buscar.");
            return;
        }
        try {
            System.out.println("Búsqueda exitosa"); // Aquí se podría implementar lógica real
        } catch (Exception e) {
            showError("Error al buscar usuario: " + e.getMessage());
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

    private void handleUserAction(Supplier<MessageResponse<Void>> action) {
        if (!validateFields()) {
            showError("Por favor, completa todos los campos requeridos.");
            return;
        }
        MessageResponse<Void> response = action.get();
        super.readResponse(response);
        if (response.isSuccess()) {
            loadInitialData();
        }
    }

    private boolean validateFields() {
        return !userNameTxf.getText().isEmpty() && !userEmailTxf.getText().isEmpty()
                && !userIdNumberTxf.getText().isEmpty() && !userStateCbx.getText().isEmpty();
    }

    private Long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void loadTable(PageDTO<UserDTO> page) {
        maxPage = page.getTotalPages();
        usersTbv.setItems(listToObservableList(page.getContent()));
    }

    protected ObservableList<UserDTO> listToObservableList(List<UserDTO> list) {
        return FXCollections.observableArrayList(list != null ? list : List.of());
    }

    private void navigatePage(int page) {
        if (page >= 0 && page < maxPage) {
            loadPage(page);
        }
    }

    private void loadPage(int page) {
        try {
            MessageResponse<PageDTO<UserDTO>> response = userAPIService.getAllEntities(PageRequest.of(page, 10), new TypeReference<>() {
            });
            super.readResponse(response);
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

    private void showError(String message) {
        System.err.println(message);
    }
}
