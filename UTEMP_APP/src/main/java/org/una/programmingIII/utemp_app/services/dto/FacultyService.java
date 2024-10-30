package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.FacultyDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class FacultyService extends BaseService<FacultyDTO> {//Frontend

    FacultyService() {
        super(EndPoint.getFaculty(), BASE_URL);
    }
}
