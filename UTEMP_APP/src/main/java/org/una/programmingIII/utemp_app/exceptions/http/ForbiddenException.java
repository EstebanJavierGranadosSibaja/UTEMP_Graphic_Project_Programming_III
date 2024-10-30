package org.una.programmingIII.utemp_app.exceptions.http;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
