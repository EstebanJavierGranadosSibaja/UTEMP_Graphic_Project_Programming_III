package org.una.programmingIII.utemp_app.services;

public interface HttpConfig {
    // ==============================
    // URL Base
    // ==============================
    String BASE_URL = "http://localhost:8080"; // Cambiar según sea necesario

    // ==============================
    // Cabeceras Comunes
    // ==============================
    String CONTENT_TYPE = "Content-Type";
    String TYPE_JSON = "application/json";
    String ACCEPT = "Accept";

    String CONTENT_ID = "Content-ID";
    String CONTENT_MD5 = "Content-MD5";
    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_ENCODING = "Content-Encoding";

    String AUTHORIZATION = "Authorization";
    String USER_AGENT = "User-Agent";
    String CONNECTION = "Connection";
    String CACHE_CONTROL = "Cache-Control";

    // ==============================
    // Métodos HTTP
    // ==============================
    enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE
    }

    // ==============================
    // Mensajes de Error
    // ==============================
    String ERROR_PREFIX = "Error during operation. Code: ";
    String CREATE_ERROR = ERROR_PREFIX + "CREATE_ERROR";
    String GET_ERROR = ERROR_PREFIX + "GET_ERROR";
    String UPDATE_ERROR = ERROR_PREFIX + "UPDATE_ERROR";
    String DELETE_ERROR = ERROR_PREFIX + "DELETE_ERROR";

    // ==============================
    // Códigos de Error HTTP
    // ==============================
    int HTTP_UNAUTHORIZED = 401;
    int HTTP_FORBIDDEN = 403;
    int HTTP_NOT_FOUND = 404;
    int HTTP_INTERNAL_SERVER_ERROR = 500;

}
