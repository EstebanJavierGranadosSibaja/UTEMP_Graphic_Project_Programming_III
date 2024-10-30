package org.una.programmingIII.utemp_app.exceptions.http;


public class ApiException extends RuntimeException {
  private final int statusCode;

  public ApiException(String message) {
    super(message);
    this.statusCode = 500; // Código de estado por defecto
  }

  public ApiException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
