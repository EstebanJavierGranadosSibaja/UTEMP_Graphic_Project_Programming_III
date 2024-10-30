package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.AssignmentDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class AssignmentService extends BaseService<AssignmentDTO> {//Frontend

    AssignmentService() {
        super(EndPoint.getAssignment(), BASE_URL);
    }

}
