package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.DepartmentDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class DepartmentService extends BaseService<DepartmentDTO> {//Frontend

    DepartmentService() {
        super(EndPoint.getDepartment(), BASE_URL);
    }

}