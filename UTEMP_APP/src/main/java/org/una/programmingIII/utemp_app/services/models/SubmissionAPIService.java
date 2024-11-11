package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
import org.una.programmingIII.utemp_app.dtos.GradeDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.SubmissionDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class SubmissionAPIService extends BaseApiServiceManager<SubmissionDTO> {

    public SubmissionAPIService() {
        super(EndPoint.SUBMISSION, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<SubmissionDTO>> getAllSubmissions(int page, int size) {
        // Obtiene todas las entidades de tipo SubmissionDTO paginadas
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<SubmissionDTO> getSubmissionById(Long id) {
        // Obtiene una entidad SubmissionDTO por ID
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createSubmission(SubmissionDTO submissionDTO) {
        // Crea una nueva entidad SubmissionDTO
        return super.createEntity(submissionDTO);
    }

    public MessageResponse<Void> updateSubmission(Long id, SubmissionDTO submissionDTO) {
        // Actualiza una entidad SubmissionDTO existente por ID
        return super.updateEntity(id, submissionDTO);
    }

    public MessageResponse<Void> deleteSubmission(Long id) {
        // Elimina una entidad SubmissionDTO por ID
        return super.deleteEntity(id);
    }

    public MessageResponse<FileMetadatumDTO> addFileMetadatumToSubmission(Long submissionId, FileMetadatumDTO fileMetadatumDTO) throws Exception {
        // Usa el método createEntity para agregar metadata de archivo a una submission
        String endpoint = ENTITY_ENDPOINT + "/" + submissionId + "/file-metadata";
        return super.executeCustomRequest(createConnectionWithBody(endpoint, HttpMethod.POST, fileMetadatumDTO, null), new TypeReference<FileMetadatumDTO>() {
        });
    }

    public MessageResponse<GradeDTO> addGradeToSubmission(Long submissionId, GradeDTO gradeDTO) throws Exception {
        // Usa el método createEntity para agregar una calificación a una submission
        String endpoint = ENTITY_ENDPOINT + "/" + submissionId + "/grades";
        return super.executeCustomRequest(endpoint, HttpMethod.POST, gradeDTO, null, new TypeReference<GradeDTO>() {
        });
    }

    public MessageResponse<Void> removeFileMetadatumFromSubmission(Long submissionId, Long fileMetadatumId) {
        // Elimina metadata de archivo de una submission específica
        String endpoint = ENTITY_ENDPOINT + "/" + submissionId + "/file-metadata/" + fileMetadatumId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<Void> removeGradeFromSubmission(Long submissionId, Long gradeId) {
        // Elimina una calificación de una submission específica
        String endpoint = ENTITY_ENDPOINT + "/" + submissionId + "/grades/" + gradeId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<PageDTO<SubmissionDTO>> getSubmissionsByAssignmentId(Long assignmentId, int page, int size) {
        String endpointWithPagination = ENTITY_ENDPOINT + "/" + assignmentId + "/submissions?page=" + page + "&size=" + size;
        return getEntitiesByEndpoint(endpointWithPagination, new TypeReference<PageDTO<SubmissionDTO>>() {
        }); // Obtiene envíos por ID de asignación
    }
}
