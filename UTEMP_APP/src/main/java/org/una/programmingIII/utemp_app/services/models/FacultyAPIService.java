package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.dtos.FacultyDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class FacultyAPIService extends BaseApiServiceManager<FacultyDTO> {

    public FacultyAPIService() {
        super(EndPoint.FACULTY, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<FacultyDTO>> getAllFaculties(int page, int size) {
        // Obtiene todas las facultades paginadas
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<FacultyDTO> getFacultyById(Long id) {
        // Obtiene una facultad por ID
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createFaculty(FacultyDTO facultyDTO) {
        // Crea una nueva facultad
        return super.createEntity(facultyDTO);
    }

    public MessageResponse<Void> updateFaculty(Long id, FacultyDTO facultyDTO) {
        // Actualiza una facultad existente por ID
        return super.updateEntity(id, facultyDTO);
    }

    public MessageResponse<Void> deleteFaculty(Long id) {
        // Elimina una facultad por ID (puede ser una eliminación lógica)
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<PageDTO<FacultyDTO>> getFacultiesByUniversityId(Long universityId, int page, int size) {
        String endpoint = ENTITY_ENDPOINT + "/university/" + universityId + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<FacultyDTO>>() {
        });
    }

    public MessageResponse<PageDTO<DepartmentDTO>> getDepartmentsByFacultyId(Long facultyId, int page, int size) {
        // Obtiene los departamentos asociados a una facultad por ID
        String endpoint = ENTITY_ENDPOINT + "/" + facultyId + "/departments?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<DepartmentDTO>>() {
        });
    }

    public MessageResponse<Void> addDepartmentToFaculty(Long facultyId, DepartmentDTO departmentDTO) {
        // Agrega un departamento a una facultad
        String endpoint = ENTITY_ENDPOINT + "/" + facultyId + "/departments";
        return super.executeVoidRequest(endpoint, HttpMethod.POST, departmentDTO);
    }

    public MessageResponse<Void> removeDepartmentFromFaculty(Long facultyId, Long departmentId) {
        // Elimina un departamento de una facultad
        String endpoint = ENTITY_ENDPOINT + "/" + facultyId + "/departments/" + departmentId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }
}
