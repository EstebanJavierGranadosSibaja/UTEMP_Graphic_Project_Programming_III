package org.una.programmingIII.utemp_app.exceptions.http;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
