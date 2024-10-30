package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.GradeDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class GradeService extends BaseService<GradeDTO> {//Frontend

    GradeService() {
        super(EndPoint.getGrade(), BASE_URL);
    }
}
