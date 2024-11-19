package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.NotificationDTO;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.EndPoint;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.BaseApiServiceManager;

public class NotificationAPIService extends BaseApiServiceManager<NotificationDTO> {

    public NotificationAPIService() {
        super(EndPoint.NOTIFICATION, BaseConfig.BASE_URL);
    }

    public MessageResponse<PageDTO<NotificationDTO>> getAllNotifications(int page, int size) {
        return super.getAllEntities(PageRequest.of(page, size));
    }

    public MessageResponse<NotificationDTO> getNotificationById(Long id) {
        return super.getEntityById(id);
    }

    public MessageResponse<Void> createNotification(NotificationDTO notificationDTO) {
        return super.createEntity(notificationDTO);
    }

    public MessageResponse<Void> updateNotification(Long id, NotificationDTO notificationDTO) {
        return super.updateEntity(id, notificationDTO);
    }

    public MessageResponse<Void> deleteNotification(Long id) {
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<PageDTO<NotificationDTO>> getNotificationsByUserId(Long userId, int page, int size) {
        String endpoint = ENTITY_ENDPOINT + "/user/" + userId + "?page=" + page + "&size=" + size;
        return super.getEntitiesByEndpoint(endpoint, new TypeReference<PageDTO<NotificationDTO>>() {
        });
    }


    public MessageResponse<Void> addNotificationToUser(Long userId, NotificationDTO notificationDTO) {
        String endpoint = ENTITY_ENDPOINT + "/user/" + userId;
        return super.executeVoidRequest(endpoint, HttpMethod.POST, notificationDTO);
    }

    public MessageResponse<Void> removeNotificationFromUser(Long userId, Long notificationId) {
        String endpoint = ENTITY_ENDPOINT + "/user/" + userId + "/notification/" + notificationId;
        return super.executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }

    public MessageResponse<Void> markAsRead(Long notificationId) {
        String endpoint = ENTITY_ENDPOINT + "/" + notificationId + "/read";
        return super.executeVoidRequest(endpoint, HttpMethod.POST, null);
    }
}
