package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.FacultyDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UniversityDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class UniversityAPIService extends BaseApiServiceManager<UniversityDTO> {

    public UniversityAPIService() {
        super(EndPoint.UNIVERSITY, BaseConfig.BASE_URL);
    }

    // Métodos CRUD (heredados de BaseApiServiceManager)

    public MessageResponse<PageDTO<UniversityDTO>> getAllUniversities(int page, int size) {
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<UniversityDTO> getUniversityById(Long id) {
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createUniversity(UniversityDTO universityDTO) {
        return super.createEntity(universityDTO);
    }

    public MessageResponse<Void> updateUniversity(Long id, UniversityDTO universityDTO) {
        return super.updateEntity(id, universityDTO);
    }

    public MessageResponse<Void> deleteUniversity(Long id) {
        return super.deleteEntity(id);
    }

    // Métodos personalizados para gestión de facultades

    public MessageResponse<PageDTO<FacultyDTO>> getFacultiesByUniversityId(Long universityId, int page, int size) {
        String endpoint = String.format("%s/%d/faculties?page=%d&size=%d", EndPoint.UNIVERSITY, universityId, page, size);
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<FacultyDTO>>() {
        });
    }

    public MessageResponse<Void> addFacultyToUniversity(Long universityId, FacultyDTO facultyDTO) {
        String endpoint = String.format("%s/%d/faculties", EndPoint.UNIVERSITY, universityId);
        return super.executeVoidRequest(endpoint, HttpMethod.POST, facultyDTO);
    }

    public MessageResponse<Void> removeFacultyFromUniversity(Long universityId, Long facultyId) {
        String endpoint = String.format("%s/%d/faculties/%d", EndPoint.UNIVERSITY, universityId, facultyId);
        return super.executeVoidConnection(endpoint, HttpMethod.DELETE, null);
    }
}
