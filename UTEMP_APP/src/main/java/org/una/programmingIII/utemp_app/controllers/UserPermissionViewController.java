package org.una.programmingIII.utemp_app.controllers;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.dtos.enums.UserPermission;
import org.una.programmingIII.utemp_app.utils.view.AppContext;

import java.util.ArrayList;
import java.util.List;

public class UserPermissionViewController extends Controller {

    @FXML
    private MFXCheckbox manageUsersCkb;
    @FXML
    private MFXCheckbox manageEnrollmentsCkb;
    @FXML
    private MFXCheckbox manageUniversitiesCkb;
    @FXML
    private MFXCheckbox manageFacultiesCkb;
    @FXML
    private MFXCheckbox manageDepartmentsCkb;
    @FXML
    private MFXCheckbox manageCoursesCkb;
    @FXML
    private MFXCheckbox manageAssignmentsCkb;
    @FXML
    private MFXCheckbox manageSubmissionsCkb;
    @FXML
    private MFXCheckbox manageGradesCkb;
    @FXML
    private MFXCheckbox addTeacherCoursesCkb;
    @FXML
    private MFXCheckbox addStudentCoursesCkb;
    @FXML
    private MFXCheckbox addUniversityFacultiesCkb;
    @FXML
    private MFXCheckbox addFacultyDepartmentsCkb;
    @FXML
    private MFXCheckbox addDepartmentCoursesCkb;
    @FXML
    private MFXCheckbox addCourseAssignmentsCkb;
    @FXML
    private MFXCheckbox addAssignmentSubmissionCkb;
    @FXML
    private MFXCheckbox addSubmissionFilesCkb;
    @FXML
    private MFXCheckbox addSubmissionGradesCkb;
    @FXML
    private MFXCheckbox removeTeacherCourseCkb;
    @FXML
    private MFXCheckbox removeStudentCoursesCkb;
    @FXML
    private MFXCheckbox removeUniversityFacultiesCkb;
    @FXML
    private MFXCheckbox removeFacultyDepartmentsCkb;
    @FXML
    private MFXCheckbox removeDepartmentCoursesCkb;
    @FXML
    private MFXCheckbox removeCourseAssignmentsCkb;
    @FXML
    private MFXCheckbox evaluateSubmissionsCkb;
    @FXML
    private MFXCheckbox getSubmissionGradesCkb;
    @FXML
    private MFXCheckbox getTeacherCoursesCkb;
    @FXML
    private MFXCheckbox getStudentCoursesCkb;
    @FXML
    private MFXCheckbox getUniversityFacultiesCkb;
    @FXML
    private MFXCheckbox getFacultyDepartmentsCkb;
    @FXML
    private MFXCheckbox getCourseAssignmentsCkb;
    @FXML
    private MFXCheckbox getDepartmentCoursesCkb;
    @FXML
    private MFXCheckbox getAssignmentSubmissionCkb;
    @FXML
    private MFXCheckbox removeAssignmentSubmissionCkb;
    @FXML
    private MFXCheckbox removeSubmissionFilesCkb;
    @FXML
    private MFXCheckbox removeSubmissionGradesCkb;

    private UserDTO currentUser;
    private List<UserPermission> initialPermissions;

    @FXML
    public void initialize() {
        currentUser = AppContext.getInstance().getUserDTO();

        if (currentUser == null) {
            showAlert("No se encontró un usuario en el contexto.");
            return;
        }

        initialPermissions = new ArrayList<>(currentUser.getPermissions());

        initializeCheckboxes();
        addCheckboxListeners();
    }

    private void initializeCheckboxes() {
        List<UserPermission> userPermissions = currentUser.getPermissions();
        manageUsersCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_USERS));
        manageEnrollmentsCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_ENROLLMENTS));
        manageUniversitiesCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_UNIVERSITIES));
        manageFacultiesCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_FACULTIES));
        manageDepartmentsCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_DEPARTMENTS));
        manageCoursesCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_COURSES));
        manageAssignmentsCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_ASSIGNMENTS));
        manageSubmissionsCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_SUBMISSIONS));
        manageGradesCkb.setSelected(userPermissions.contains(UserPermission.MANAGE_GRADES));
        addTeacherCoursesCkb.setSelected(userPermissions.contains(UserPermission.ADD_TEACHER_COURSES));
        addStudentCoursesCkb.setSelected(userPermissions.contains(UserPermission.ADD_STUDENT_COURSES));
        addUniversityFacultiesCkb.setSelected(userPermissions.contains(UserPermission.ADD_UNIVERSITY_FACULTIES));
        addFacultyDepartmentsCkb.setSelected(userPermissions.contains(UserPermission.ADD_FACULTY_DEPARTMENTS));
        addDepartmentCoursesCkb.setSelected(userPermissions.contains(UserPermission.ADD_DEPARTMENT_COURSES));
        addCourseAssignmentsCkb.setSelected(userPermissions.contains(UserPermission.ADD_COURSE_ASSIGNMENTS));
        addAssignmentSubmissionCkb.setSelected(userPermissions.contains(UserPermission.ADD_ASSIGNMENT_SUBMISSION));
        addSubmissionFilesCkb.setSelected(userPermissions.contains(UserPermission.ADD_SUBMISSION_FILES));
        addSubmissionGradesCkb.setSelected(userPermissions.contains(UserPermission.ADD_SUBMISSION_GRADES));
        removeTeacherCourseCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_TEACHER_COURSE));
        removeStudentCoursesCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_STUDENT_COURSES));
        removeUniversityFacultiesCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_UNIVERSITY_FACULTIES));
        removeFacultyDepartmentsCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_FACULTY_DEPARTMENTS));
        removeDepartmentCoursesCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_DEPARTMENT_COURSES));
        removeCourseAssignmentsCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_COURSE_ASSIGNMENTS));
        evaluateSubmissionsCkb.setSelected(userPermissions.contains(UserPermission.EVALUATE_SUBMISSIONS));
        getSubmissionGradesCkb.setSelected(userPermissions.contains(UserPermission.GET_SUBMISSION_GRADES));
        getTeacherCoursesCkb.setSelected(userPermissions.contains(UserPermission.GET_TEACHER_COURSES));
        getStudentCoursesCkb.setSelected(userPermissions.contains(UserPermission.GET_STUDENT_ENROLLMENTS));
        getUniversityFacultiesCkb.setSelected(userPermissions.contains(UserPermission.GET_UNIVERSITY_FACILITIES));
        getFacultyDepartmentsCkb.setSelected(userPermissions.contains(UserPermission.GET_FACULTY_DEPARTMENTS));
        getCourseAssignmentsCkb.setSelected(userPermissions.contains(UserPermission.GET_COURSE_ASSIGNMENTS));
        getDepartmentCoursesCkb.setSelected(userPermissions.contains(UserPermission.GET_DEPARTMENT_COURSES));
        getAssignmentSubmissionCkb.setSelected(userPermissions.contains(UserPermission.GET_ASSIGNMENT_SUBMISSIONS));
        removeAssignmentSubmissionCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_ASSIGNMENT_SUBMISSION));
        removeSubmissionFilesCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_SUBMISSION_FILES));
        removeSubmissionGradesCkb.setSelected(userPermissions.contains(UserPermission.REMOVE_SUBMISSION_GRADES));
    }

    private void addCheckboxListeners() {
        manageUsersCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_USERS, manageUsersCkb.isSelected()));
        manageEnrollmentsCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_ENROLLMENTS, manageEnrollmentsCkb.isSelected()));
        manageUniversitiesCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_UNIVERSITIES, manageUniversitiesCkb.isSelected()));
        manageFacultiesCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_FACULTIES, manageFacultiesCkb.isSelected()));
        manageDepartmentsCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_DEPARTMENTS, manageDepartmentsCkb.isSelected()));
        manageCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_COURSES, manageCoursesCkb.isSelected()));
        manageAssignmentsCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_ASSIGNMENTS, manageAssignmentsCkb.isSelected()));
        manageSubmissionsCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_SUBMISSIONS, manageSubmissionsCkb.isSelected()));
        manageGradesCkb.setOnAction(e -> updatePermissions(UserPermission.MANAGE_GRADES, manageGradesCkb.isSelected()));
        addTeacherCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_TEACHER_COURSES, addTeacherCoursesCkb.isSelected()));
        addStudentCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_STUDENT_COURSES, addStudentCoursesCkb.isSelected()));
        addUniversityFacultiesCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_UNIVERSITY_FACULTIES, addUniversityFacultiesCkb.isSelected()));
        addFacultyDepartmentsCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_FACULTY_DEPARTMENTS, addFacultyDepartmentsCkb.isSelected()));
        addDepartmentCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_DEPARTMENT_COURSES, addDepartmentCoursesCkb.isSelected()));
        addCourseAssignmentsCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_COURSE_ASSIGNMENTS, addCourseAssignmentsCkb.isSelected()));
        addAssignmentSubmissionCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_ASSIGNMENT_SUBMISSION, addAssignmentSubmissionCkb.isSelected()));
        addSubmissionFilesCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_SUBMISSION_FILES, addSubmissionFilesCkb.isSelected()));
        addSubmissionGradesCkb.setOnAction(e -> updatePermissions(UserPermission.ADD_SUBMISSION_GRADES, addSubmissionGradesCkb.isSelected()));
        removeTeacherCourseCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_TEACHER_COURSE, removeTeacherCourseCkb.isSelected()));
        removeStudentCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_STUDENT_COURSES, removeStudentCoursesCkb.isSelected()));
        removeUniversityFacultiesCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_UNIVERSITY_FACULTIES, removeUniversityFacultiesCkb.isSelected()));
        removeFacultyDepartmentsCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_FACULTY_DEPARTMENTS, removeFacultyDepartmentsCkb.isSelected()));
        removeDepartmentCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_DEPARTMENT_COURSES, removeDepartmentCoursesCkb.isSelected()));
        removeCourseAssignmentsCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_COURSE_ASSIGNMENTS, removeCourseAssignmentsCkb.isSelected()));
        evaluateSubmissionsCkb.setOnAction(e -> updatePermissions(UserPermission.EVALUATE_SUBMISSIONS, evaluateSubmissionsCkb.isSelected()));
        getSubmissionGradesCkb.setOnAction(e -> updatePermissions(UserPermission.GET_SUBMISSION_GRADES, getSubmissionGradesCkb.isSelected()));
        getTeacherCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.GET_TEACHER_COURSES, getTeacherCoursesCkb.isSelected()));
        getStudentCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.GET_STUDENT_ENROLLMENTS, getStudentCoursesCkb.isSelected()));
        getUniversityFacultiesCkb.setOnAction(e -> updatePermissions(UserPermission.GET_UNIVERSITY_FACILITIES, getUniversityFacultiesCkb.isSelected()));
        getFacultyDepartmentsCkb.setOnAction(e -> updatePermissions(UserPermission.GET_FACULTY_DEPARTMENTS, getFacultyDepartmentsCkb.isSelected()));
        getCourseAssignmentsCkb.setOnAction(e -> updatePermissions(UserPermission.GET_COURSE_ASSIGNMENTS, getCourseAssignmentsCkb.isSelected()));
        getDepartmentCoursesCkb.setOnAction(e -> updatePermissions(UserPermission.GET_DEPARTMENT_COURSES, getDepartmentCoursesCkb.isSelected()));
        getAssignmentSubmissionCkb.setOnAction(e -> updatePermissions(UserPermission.GET_ASSIGNMENT_SUBMISSIONS, getAssignmentSubmissionCkb.isSelected()));
        removeAssignmentSubmissionCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_ASSIGNMENT_SUBMISSION, removeAssignmentSubmissionCkb.isSelected()));
        removeSubmissionFilesCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_SUBMISSION_FILES, removeSubmissionFilesCkb.isSelected()));
        removeSubmissionGradesCkb.setOnAction(e -> updatePermissions(UserPermission.REMOVE_SUBMISSION_GRADES, removeSubmissionGradesCkb.isSelected()));
    }

    private void updatePermissions(UserPermission permission, boolean isSelected) {
        List<UserPermission> updatedPermissions = new ArrayList<>(currentUser.getPermissions());

        if (isSelected && !updatedPermissions.contains(permission)) {
            updatedPermissions.add(permission);
        } else if (!isSelected) {
            updatedPermissions.remove(permission);
        }

        // Actualizamos los permisos en currentUser
        currentUser.setPermissions(updatedPermissions);

        // Sincronizamos el contexto con el usuario actualizado
        AppContext.getInstance().setUserDTO(currentUser);

        // Verificación
        System.out.println("Permisos actualizados: " + currentUser.getPermissions());
        System.out.println("USUARIO: " + currentUser);

        // Volver a inicializar los checkboxes
        initializeCheckboxes();
    }


    @FXML
    public void onActionConfirmBtn(ActionEvent event) {
        if (currentUser.getId() != null) {
            System.out.println("CON ID");
            AppContext.getInstance().getUserDTO().setPermissions(currentUser.getPermissions());
        } else {
            System.out.println("SIN ID");
            AppContext.getInstance().setUserDTO(currentUser);
        }

        // Verificación
        System.out.println("Permisos en UserManagementViewController: " + currentUser.getPermissions());
        System.out.println("USUARIO: " + currentUser);

        Stage stage = (Stage) manageUsersCkb.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void onActionCancelBtn(ActionEvent event) {
        AppContext.getInstance().getUserDTO().setPermissions(new ArrayList<>(initialPermissions));

        System.out.println("Permisos en UserManagementViewController: " + currentUser.getPermissions());
        System.out.println("USUARIO: " + currentUser);


        initializeCheckboxes();
        Stage stage = (Stage) manageUsersCkb.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Actualización de Permisos");
        alert.setHeaderText("Operación exitosa");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
