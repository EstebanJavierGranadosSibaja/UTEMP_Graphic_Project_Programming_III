package org.una.programmingIII.utemp_app.exceptions.http;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
