package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class FileService extends BaseService<FileMetadatumDTO> {//Frontend

    FileService() {
        super(EndPoint.getFile(), BASE_URL);
    }
}
