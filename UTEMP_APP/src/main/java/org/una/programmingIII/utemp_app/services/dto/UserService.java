package org.una.programmingIII.utemp_app.services.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.dtos.EnrollmentDTO;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.responses.ApiResponse;
import org.una.programmingIII.utemp_app.services.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.responses.PageResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

/**
 * Propósito: Esta clase concreta extiende BaseService
 * Maneja todas las operaciones relacionadas con usuarios, utilizando los métodos y utilidades que hereda de BaseService y de BaseHttpClient
 */
public class UserService extends BaseService<UserDTO> {

    public UserService() {
        super(EndPoint.getUser(), BASE_URL);
    }

    public MessageResponse<UserDTO> createUser(UserDTO userDTO) {
        return create(userDTO);
    }

    public MessageResponse<UserDTO> getUserById(Long id) {
        return findById(id);
    }

    public MessageResponse<UserDTO> getUserByIdentificationNumber(String identificationNumber) {
        return findByIdentifier(identificationNumber);
    }

    public MessageResponse<PageResponse<UserDTO>> getAllUsers(int page, int size) {
        return findAll(page, size);
    }

    public MessageResponse<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return update(id, userDTO);
    }

    public MessageResponse<Boolean> deleteUser(Long id, boolean isPermanentDelete) {
        return delete(id, isPermanentDelete);
    }

    //
    public MessageResponse<List<CourseDTO>> getCoursesTeachingByUserId(Long userId) {
        return fetchListFromApi("/" + userId + "/courses", new TypeReference<ApiResponse<List<CourseDTO>>>() {
        });
    }

    public MessageResponse<List<EnrollmentDTO>> getEnrollmentsByUserId(Long userId) {
        return fetchListFromApi("/" + userId + "/enrollments", new TypeReference<ApiResponse<List<EnrollmentDTO>>>() {
        });
    }

    private <T> MessageResponse<List<T>> fetchListFromApi(String endpoint, TypeReference<ApiResponse<List<T>>> typeReference) {
        HttpURLConnection connection = null;
        MessageResponse<List<T>> response = new MessageResponse<>();
        try {
            connection = createAuthenticatedConnection(super.endPoint + endpoint, HttpMethod.GET);
            Optional<ApiResponse<List<T>>> apiResponseOptional = handleResponse(connection, typeReference);
            if (apiResponseOptional.isPresent()) {
                response.setData(apiResponseOptional.get().getData());
                response.setMessage("Data retrieved successfully.");
                response.setSuccess(true);
            } else {
                response.setMessage("Error retrieving data.");
                response.setSuccess(false);
            }
        } catch (IOException e) {
            response.setMessage("I/O error: " + e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            disconnectConnection(connection);
        }
        return response;
    }

    public MessageResponse<String> enrollUserToCourse(Long userId, Long courseId) {
        return postToApi("/" + userId + "/enrollments/" + courseId, "User enrolled successfully");
    }

    public MessageResponse<String> unrollUserFromCourse(Long userId, Long courseId) {
        return deleteFromApi("/" + userId + "/enrollments/" + courseId, "User unrolled successfully");
    }

    public MessageResponse<String> assignCourseToTeacher(Long userId, Long courseId) {
        return postToApi("/" + userId + "/courses/" + courseId, "Course assigned successfully");
    }

    public MessageResponse<String> removeCourseFromTeacher(Long userId, Long courseId) {
        return deleteFromApi("/" + userId + "/courses/" + courseId, "Course removed successfully");
    }

    private MessageResponse<String> postToApi(String endpoint, String successMessage) {
        HttpURLConnection connection = null;
        MessageResponse<String> response = new MessageResponse<>();
        try {
            connection = createAuthenticatedConnection(super.endPoint + endpoint, HttpMethod.POST);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                response.setData(successMessage);
                response.setSuccess(true);
            } else {
                response.setMessage("Error: " + connection.getResponseCode());
                response.setSuccess(false);
            }
        } catch (IOException e) {
            response.setMessage("I/O error: " + e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            disconnectConnection(connection);
        }
        return response;
    }

    private MessageResponse<String> deleteFromApi(String endpoint, String successMessage) {
        HttpURLConnection connection = null;
        MessageResponse<String> response = new MessageResponse<>();
        try {
            connection = createAuthenticatedConnection(super.endPoint + endpoint, HttpMethod.DELETE);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                response.setData(successMessage);
                response.setSuccess(true);
            } else {
                response.setMessage("Error: " + connection.getResponseCode());
                response.setSuccess(false);
            }
        } catch (IOException e) {
            response.setMessage("I/O error: " + e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            disconnectConnection(connection);
        }
        return response;
    }
}

// Métodos específicos de usuario

//    public MessageResponse<UserDTO> getCurrentUser() {
//        Long authenticatedUserId = getAuthenticatedUserId();
//        return findById(authenticatedUserId);
//    }

//    private Long getAuthenticatedUserId() {
//        // Lógica para obtener el ID del usuario autenticado.
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
//    }