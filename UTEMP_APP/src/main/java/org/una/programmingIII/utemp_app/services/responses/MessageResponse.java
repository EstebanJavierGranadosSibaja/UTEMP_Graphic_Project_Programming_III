package org.una.programmingIII.utemp_app.services.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse<T> {
    private T data;
    private String message;
    private boolean success;
    private String errorCode;
}
