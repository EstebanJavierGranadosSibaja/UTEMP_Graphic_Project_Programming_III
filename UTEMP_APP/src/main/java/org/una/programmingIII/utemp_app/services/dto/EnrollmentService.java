package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.EnrollmentDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class EnrollmentService extends BaseService<EnrollmentDTO> {//Frontend

    EnrollmentService() {
        super(EndPoint.getEnrollment(), BASE_URL);
    }


}
