package org.una.programmingIII.utemp_app.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiResponse<T> {//respuesta enviada
    private T data;
    private int statusCode;
    private String message;

    public ApiResponse() {
        data = null;
        statusCode = 0;
        message = "";

    }

    public ApiResponse(T data) {
        this.data = data;
        this.statusCode = 200; // Código de estado por defecto
        this.message = ""; // No hay mensaje por defecto
    }

    public ApiResponse(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.message = errorMessage;
        this.data = null; // Sin datos
    }

    public static <T> ApiResponse<T> fromException(int statusCode, String errorMessage) {
        return new ApiResponse<>(statusCode, errorMessage);
    }
}
