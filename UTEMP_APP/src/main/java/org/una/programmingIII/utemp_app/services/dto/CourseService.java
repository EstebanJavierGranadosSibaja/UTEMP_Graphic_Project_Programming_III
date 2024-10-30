package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.CourseDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class CourseService extends BaseService<CourseDTO> {//Frontend

    CourseService() {
        super(EndPoint.getCourse(), BASE_URL);
    }
}
