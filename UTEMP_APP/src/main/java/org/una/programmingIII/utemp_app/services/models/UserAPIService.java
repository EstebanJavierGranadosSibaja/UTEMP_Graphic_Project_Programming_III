package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.*;
import org.una.programmingIII.utemp_app.dtos.enums.UserRole;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

/**
 * Propósito: Esta clase concreta extiende BaseEntityService
 * Maneja todas las operaciones relacionadas con usuarios, utilizando los métodos y utilidades que hereda de BaseEntityService y HttpClientConnectionManager
 */
public class UserAPIService extends BaseApiServiceManager<UserDTO> {

    public UserAPIService() {
        super(EndPoint.USER, BaseConfig.BASE_URL); // Define el endpoint específico para los usuarios
    }

    // Obtener todos los usuarios con paginación
    public MessageResponse<PageDTO<UserDTO>> getAllUsers(int page, int size) {
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<PageDTO<UserDTO>> getAllUsersByRole(UserRole role, int page, int size) {
        String endpoint = this.ENTITY_ENDPOINT + "/usersByRole/" + role + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<UserDTO>>() {
        });
    }

    // Obtener usuario por ID
    public MessageResponse<UserDTO> getUserByID(Long id) {
        return super.getEntityById(id);
    }

    // Obtener usuario por número de identificación
    public MessageResponse<UserDTO> getUserByIdentificationNumber(String identificationNumber) {
        String endpoint = this.ENTITY_ENDPOINT + "/identification/" + identificationNumber;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<UserDTO>() {
        });
    }

    // Obtener usuario por número de identificación
    public MessageResponse<UserDTO> getUserByRole(UserRole role) {
        String endpoint = this.ENTITY_ENDPOINT + "/role/" + role;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<UserDTO>() {
        });
    }

    // Crear usuario
    public MessageResponse<Void> createUser(UserDTO userDTO) {
        return super.createEntity(userDTO);
    }

    // Actualizar usuario
    public MessageResponse<Void> updateUser(Long id, UserDTO userDTO) {
        return super.updateEntity(id, userDTO);
    }

    public MessageResponse<Void> deleteUser(Long id, Boolean isPermanentDelete) {
        // Añadimos el parámetro `isPermanentDelete` en la URL como `?isPermanentDelete=true` o `false`
        String endpoint = isPermanentDelete
                ? this.ENTITY_ENDPOINT + "/" + id + "?isPermanentDelete=true"
                : this.ENTITY_ENDPOINT + "/" + id + "?isPermanentDelete=false";

        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }


    // Obtener usuario actual
    public MessageResponse<UserDTO> getCurrentUser() {
        String endpoint = this.ENTITY_ENDPOINT + "/me";
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<UserDTO>() {
        });
    }

    // Obtener cursos enseñados por el usuario
    public MessageResponse<PageDTO<CourseDTO>> getCoursesTeachingByUserId(Long teacherId, Pageable pageable) {
        String endpoint = this.ENTITY_ENDPOINT + "/" + teacherId + "/courses?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize();
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<CourseDTO>>() {
        });
    }

    // Asignar curso a un docente
    public MessageResponse<Void> assignCourseToTeacher(Long userId, Long courseId) {
        String endpoint = this.ENTITY_ENDPOINT + "/" + userId + "/courses/" + courseId;
        return super.executeVoidRequest(endpoint, HttpMethod.POST, null);
    }

    // Remover curso de un docente
    public MessageResponse<Void> removeCourseFromTeacher(Long userId, Long courseId) {
        String endpoint = this.ENTITY_ENDPOINT + "/" + userId + "/courses/" + courseId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    // Obtener matrículas de un usuario
    public MessageResponse<PageDTO<EnrollmentDTO>> getEnrollmentsByUserId(Long userId, Pageable pageable) {
        String endpoint = this.ENTITY_ENDPOINT + "/" + userId + "/enrollments?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize();
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<EnrollmentDTO>>() {
        });
    }

    // Matricular usuario en un curso
    public MessageResponse<Void> enrollUserToCourse(Long userId, Long courseId) {
        String endpoint = this.ENTITY_ENDPOINT + "/" + userId + "/enrollments/" + courseId;
        return super.executeVoidRequest(endpoint, HttpMethod.POST, null);
    }

    // Desmatricular usuario de un curso
    public MessageResponse<Void> unrollUserFromCourse(Long userId, Long courseId) {
        String endpoint = this.ENTITY_ENDPOINT + "/" + userId + "/enrollments/" + courseId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }
}
