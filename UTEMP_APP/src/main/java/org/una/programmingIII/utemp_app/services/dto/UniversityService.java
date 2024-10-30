package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.UniversityDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class UniversityService extends BaseService<UniversityDTO> {//Frontend

    UniversityService() {
        super(EndPoint.getUniversity(), BASE_URL);
    }
}
