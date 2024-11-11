package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.GradeDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class GradeAPIService extends BaseApiServiceManager<GradeDTO> {

    public GradeAPIService() {
        super(EndPoint.GRADE, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<GradeDTO>> getAllGrades(int page, int size) {
        // Obtiene todas las entidades de tipo GradeDTO paginadas
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<GradeDTO> getGradeById(Long id) {
        // Obtiene una entidad GradeDTO por ID
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createGrade(GradeDTO gradeDTO) {
        // Crea una nueva entidad GradeDTO
        return super.createEntity(gradeDTO);
    }

    public MessageResponse<Void> updateGrade(Long id, GradeDTO gradeDTO) {
        // Actualiza una entidad GradeDTO existente por ID
        return super.updateEntity(id, gradeDTO);
    }

    public MessageResponse<Void> deleteGrade(Long id) {
        // Elimina una entidad GradeDTO por ID
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<PageDTO<GradeDTO>> getGradesBySubmissionId(Long submissionId, int page, int size) {
        // Obtiene las calificaciones asociadas a una submission específica
        String endpoint = ENTITY_ENDPOINT + "/submissions/" + submissionId + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<GradeDTO>>() {
        });
    }
}
