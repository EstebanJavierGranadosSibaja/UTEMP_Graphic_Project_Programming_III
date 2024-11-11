package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.EnrollmentDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class EnrollmentAPIService extends BaseApiServiceManager<EnrollmentDTO> {

    public EnrollmentAPIService() {
        super(EndPoint.ENROLLMENT, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<EnrollmentDTO>> getAllEnrollments(int page, int size) {
        // Obtiene todas las inscripciones paginadas
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<PageDTO<EnrollmentDTO>> getEnrollmentsByCourseId(Long courseId, int page, int size) {
        // Obtiene inscripciones por ID de curso
        return getEnrollmentsByCourseIdFromApi(courseId, page, size);
    }

    public MessageResponse<PageDTO<EnrollmentDTO>> getEnrollmentsByStudentId(Long studentId, int page, int size) {
        // Obtiene inscripciones por ID de estudiante
        return getEnrollmentsByStudentIdFromApi(studentId, page, size);
    }

    public MessageResponse<EnrollmentDTO> getEnrollmentById(Long id) {
        // Obtiene una inscripción por ID
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createEnrollment(EnrollmentDTO enrollmentDTO) {
        // Crea una nueva inscripción
        return super.createEntity(enrollmentDTO);
    }

    public MessageResponse<Void> updateEnrollment(Long id, EnrollmentDTO enrollmentDTO) {
        // Actualiza una inscripción existente por ID
        return super.updateEntity(id, enrollmentDTO);
    }

    public MessageResponse<Void> deleteEnrollment(Long id) {
        // Elimina una inscripción por ID (se puede implementar eliminación suave)
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    private MessageResponse<PageDTO<EnrollmentDTO>> getEnrollmentsByCourseIdFromApi(Long courseId, int page, int size) {
        // Implementa la lógica para obtener inscripciones por curso desde la API
        String endpoint = ENTITY_ENDPOINT + "/course/" + courseId + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<EnrollmentDTO>>() {
        });
    }

    private MessageResponse<PageDTO<EnrollmentDTO>> getEnrollmentsByStudentIdFromApi(Long studentId, int page, int size) {
        // Implementa la lógica para obtener inscripciones por estudiante desde la API
        String endpoint = ENTITY_ENDPOINT + "/student/" + studentId + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<EnrollmentDTO>>() {
        });
    }
}
