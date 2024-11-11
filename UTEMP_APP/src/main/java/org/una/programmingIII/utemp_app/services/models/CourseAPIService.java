package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.AssignmentDTO;
import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class CourseAPIService extends BaseApiServiceManager<CourseDTO> {

    public CourseAPIService() {
        super(EndPoint.COURSE, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<CourseDTO>> getAllCourses(int page, int size) {
        return super.getAllEntities(PageRequest.of(page, size)); // Utiliza el método getAllEntities de BaseApiServiceManager
    }

    public MessageResponse<CourseDTO> getCourseById(Long id) {
        return super.getEntityById(id); // Utiliza el método getEntityById de BaseApiServiceManager
    }

    public MessageResponse<Void> createCourse(CourseDTO courseDTO) {
        return super.createEntity(courseDTO); // Utiliza el método createEntity de BaseApiServiceManager
    }

    public MessageResponse<Void> updateCourse(Long id, CourseDTO courseDTO) {
        return super.updateEntity(id, courseDTO); // Utiliza el método updateEntity de BaseApiServiceManager
    }

    public MessageResponse<Void> deleteCourse(Long id) {
        return super.deleteEntity(id); // Utiliza el método deleteEntity de BaseApiServiceManager
    }

    public MessageResponse<PageDTO<CourseDTO>> getCoursesByTeacherId(Long teacherId, int page, int size) {
        String endpointWithPagination = ENTITY_ENDPOINT + "/teacher/" + teacherId + "?page=" + page + "&size=" + size;
        return getEntitiesByEndpoint(endpointWithPagination, new TypeReference<PageDTO<CourseDTO>>() {
        }); // Obtiene cursos por ID de profesor
    }

    public MessageResponse<PageDTO<CourseDTO>> getCoursesByDepartmentId(Long departmentId, int page, int size) {
        String endpointWithPagination = ENTITY_ENDPOINT + "/department/" + departmentId + "?page=" + page + "&size=" + size;
        return getEntitiesByEndpoint(endpointWithPagination, new TypeReference<PageDTO<CourseDTO>>() {
        }); // Obtiene cursos por ID de departamento
    }

    public MessageResponse<Void> addAssignmentToCourse(Long courseId, AssignmentDTO assignmentDTO) {
        String endpoint = ENTITY_ENDPOINT + "/" + courseId + "/assignments";
        return executeVoidRequest(endpoint, HttpMethod.POST, assignmentDTO); // Añade una tarea al curso
    }

    public MessageResponse<Void> removeAssignmentFromCourse(Long courseId, Long assignmentId) {
        String endpoint = ENTITY_ENDPOINT + "/" + courseId + "/assignments/" + assignmentId;
        return executeVoidRequest(endpoint, HttpMethod.DELETE, null); // Elimina una tarea del curso
    }
}
