package org.una.programmingIII.utemp_app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.una.programmingIII.utemp_app.exceptions.http.ApiException;
import org.una.programmingIII.utemp_app.exceptions.http.ForbiddenException;
import org.una.programmingIII.utemp_app.exceptions.http.UnauthorizedException;
import org.una.programmingIII.utemp_app.responses.ApiResponse;

import java.net.HttpURLConnection;

public class ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    public static <T> ApiResponse<T> handleException(Exception e) {
        switch (e) {
            case ApiException apiException -> {
                logger.error("API error: {}", apiException.getMessage());
                return ApiResponse.fromException(apiException.getStatusCode(), apiException.getMessage());
            }
            case UnauthorizedException unauthorizedException -> {
                logger.warn("Unauthorized access: {}", unauthorizedException.getMessage());
                return ApiResponse.fromException(HttpURLConnection.HTTP_UNAUTHORIZED, unauthorizedException.getMessage());
            }
            case ForbiddenException forbiddenException -> {
                logger.warn("Access denied: {}", forbiddenException.getMessage());
                return ApiResponse.fromException(HttpURLConnection.HTTP_FORBIDDEN, forbiddenException.getMessage());
            }
            case null, default -> {
                logger.error("Unexpected error: {}", e.getMessage());
                return ApiResponse.fromException(500, "Unexpected error occurred. Please try again later.");
            }
        }
    }

}
