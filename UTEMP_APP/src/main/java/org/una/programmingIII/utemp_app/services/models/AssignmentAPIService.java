package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.AssignmentDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.SubmissionDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class AssignmentAPIService extends BaseApiServiceManager<AssignmentDTO> {

    public AssignmentAPIService() {
        super(EndPoint.ASSIGNMENT, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<AssignmentDTO>> getAllAssignments(int page, int size) {
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<AssignmentDTO> getAssignmentById(Long id) {
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createAssignment(AssignmentDTO assignmentDTO) {
        return super.createEntity(assignmentDTO);
    }

    public MessageResponse<Void> updateAssignment(Long id, AssignmentDTO assignmentDTO) {
        return super.updateEntity(id, assignmentDTO);
    }

    public MessageResponse<Void> deleteAssignment(Long id, boolean isPermanentDelete) {
        // Eliminar de manera permanente o lógica según el flag isPermanentDelete
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        HttpMethod method = isPermanentDelete ? HttpMethod.DELETE : HttpMethod.PATCH; // Suponiendo que PATCH es para eliminación lógica
        return executeVoidRequest(endpoint, method, null);
    }

    public MessageResponse<PageDTO<AssignmentDTO>> getAssignmentsByCourseId(Long courseId, int page, int size) {
        String endpointWithPagination = ENTITY_ENDPOINT + "/course/" + courseId + "?page=" + page + "&size=" + size;
        return getEntitiesByEndpoint(endpointWithPagination, new TypeReference<PageDTO<AssignmentDTO>>() {
        }); // Obtiene asignaciones por ID de curso
    }

    public MessageResponse<PageDTO<SubmissionDTO>> getSubmissionsByAssignmentId(Long assignmentId, int page, int size) {
        String endpointWithPagination = ENTITY_ENDPOINT + "/" + assignmentId + "/submissions?page=" + page + "&size=" + size;
        return getEntitiesByEndpoint(endpointWithPagination, new TypeReference<PageDTO<SubmissionDTO>>() {
        }); // Obtiene envíos por ID de asignación
    }

    public MessageResponse<SubmissionDTO> addSubmissionToAssignment(Long assignmentId, SubmissionDTO submissionDTO) {
        String endpoint = ENTITY_ENDPOINT + "/" + assignmentId + "/submissions"; // Endpoint para agregar envío a la asignación
        return executeCustomRequest(endpoint, HttpMethod.POST, submissionDTO, null, new TypeReference<SubmissionDTO>() {
        }); // Crea la conexión para agregar un envío
    }

    public MessageResponse<Void> deleteSubmissionFromAssignment(Long assignmentId, Long submissionId) {
        String endpoint = ENTITY_ENDPOINT + "/" + assignmentId + "/submissions/" + submissionId; // Endpoint para eliminar un envío
        return executeVoidRequest(endpoint, HttpMethod.DELETE, null); // Elimina el envío
    }
}
