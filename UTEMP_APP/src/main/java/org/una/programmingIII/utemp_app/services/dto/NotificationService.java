package org.una.programmingIII.utemp_app.services.dto;

import org.una.programmingIII.utemp_app.dtos.NotificationDTO;
import org.una.programmingIII.utemp_app.services.BaseService;
import org.una.programmingIII.utemp_app.services.EndPoint;

public class NotificationService extends BaseService<NotificationDTO> {//Frontend

    NotificationService() {
        super(EndPoint.getNotification(), BASE_URL);
    }
}
