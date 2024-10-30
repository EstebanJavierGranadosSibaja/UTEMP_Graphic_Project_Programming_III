package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.SubmissionDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class SubmissionService extends BaseService<SubmissionDTO> {//Frontend

    SubmissionService() {
        super(EndPoint.getSubmission(), BASE_URL);
    }

}
