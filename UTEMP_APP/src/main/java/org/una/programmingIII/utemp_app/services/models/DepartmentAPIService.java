package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class DepartmentAPIService extends BaseApiServiceManager<DepartmentDTO> {

    public DepartmentAPIService() {
        super(EndPoint.DEPARTMENT, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<DepartmentDTO>> getAllDepartments(int page, int size) {
        // Obtiene todos los departamentos paginados
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<DepartmentDTO> getDepartmentById(Long id) {
        // Obtiene un departamento por ID
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createDepartment(DepartmentDTO departmentDTO) {
        // Crea un nuevo departamento
        return super.createEntity(departmentDTO);
    }

    public MessageResponse<Void> updateDepartment(Long id, DepartmentDTO departmentDTO) {
        // Actualiza un departamento existente por ID
        return super.updateEntity(id, departmentDTO);
    }

    public MessageResponse<Void> deleteDepartment(Long id) {
        // Elimina un departamento por ID (puede implementar eliminación suave)
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<PageDTO<DepartmentDTO>> getDepartmentsByFacultyId(Long facultyId, int page, int size) {
        String endpoint = ENTITY_ENDPOINT + "/faculty/" + facultyId + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<DepartmentDTO>>() {
        });
    }

    public MessageResponse<PageDTO<CourseDTO>> getCoursesByDepartmentId(Long departmentId, int page, int size) {
        // Implementa la lógica para obtener cursos por departamento desde la API
        String endpoint = ENTITY_ENDPOINT + "/" + departmentId + "/courses?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<CourseDTO>>() {
        });
    }

    public MessageResponse<Void> addCourseToDepartment(Long departmentId, CourseDTO courseDTO) {
        // Implementa la lógica para añadir un curso a un departamento
        String endpoint = ENTITY_ENDPOINT + "/" + departmentId + "/courses";
        return super.executeVoidRequest(endpoint, HttpMethod.POST, courseDTO);
    }

    public MessageResponse<Void> removeCourseFromDepartment(Long departmentId, Long courseId) {
        // Implementa la lógica para eliminar un curso de un departamento
        String endpoint = ENTITY_ENDPOINT + "/" + departmentId + "/courses/" + courseId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }
}
