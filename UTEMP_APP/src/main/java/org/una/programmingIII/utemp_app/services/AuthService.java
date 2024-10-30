package org.una.programmingIII.utemp_app.services;

import javafx.scene.control.Alert;
import org.una.programmingIII.utemp_app.exceptions.AccessDeniedException;
import org.una.programmingIII.utemp_app.exceptions.BadRequestException;
import org.una.programmingIII.utemp_app.exceptions.InvalidCredentialsException;
import org.una.programmingIII.utemp_app.exceptions.http.ApiException;
import org.una.programmingIII.utemp_app.services.request.AuthRequest;
import org.una.programmingIII.utemp_app.services.responses.ApiResponse;
import org.una.programmingIII.utemp_app.services.responses.MessageResponse;
import org.una.programmingIII.utemp_app.utils.Message;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Optional;


/**
 * Servicio para la autenticación de clientes.
 */
public class AuthService extends BaseHttpClient<String> {
    Message message = new Message();

    public AuthService() {
        super(BASE_URL, 10000, 30000);
    }

    public MessageResponse<String> login(AuthRequest authRequest) {
        MessageResponse<String> response = new MessageResponse<>();
        HttpURLConnection connection = null;

        try {
            validateAuthRequest(authRequest);
            connection = createUnauthenticatedConnection(EndPoint.getAuth() + "/login", HttpMethod.POST);
            setupConnectionForOutput(connection);
            sendRequest(connection, authRequest);

            Optional<ApiResponse<String>> apiResponseOptional = handleResponse(connection);
            if (apiResponseOptional.isPresent()) {
                response.setData(apiResponseOptional.get().getData());
                response.setMessage("Autenticación exitosa");
                setToken(response.getData());
                response.setSuccess(true);
            } else {
                response.setErrorCode("Token no disponible después de autenticación.");
                throw new ApiException("Token no disponible después de autenticación.");
            }

        } catch (InvalidCredentialsException | AccessDeniedException | BadRequestException e) {
            response.setErrorCode(e.getMessage());
            response.setSuccess(false);
        } catch (ApiException e) {
            response.setErrorCode("Error en el servidor: " + e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            response.setErrorCode("Error al autenticar: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            if (connection != null) {
                disconnectConnection(connection);
            }
        }

        if (!response.isSuccess()) {
            message.showModal(Alert.AlertType.ERROR, response.getMessage(), response.getErrorCode());
        }

        return response;
    }

    private void validateAuthRequest(AuthRequest authRequest) throws InvalidCredentialsException {
        if (authRequest.getIdentificationNumber() == null || authRequest.getPassword() == null) {
            throw new InvalidCredentialsException("El número de identificación y la contraseña son obligatorios.");
        }
        if (authRequest.getIdentificationNumber().length() != 9 || !authRequest.getIdentificationNumber().matches("\\d{9}")) {
            throw new InvalidCredentialsException("El número de identificación debe tener exactamente 9 caracteres numéricos.");
        }
        if (authRequest.getPassword().length() < 5) {
            throw new InvalidCredentialsException("La contraseña debe tener un mínimo de 5 caracteres.");
        }
    }

    private void sendRequest(HttpURLConnection connection, AuthRequest authRequest) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            objectMapper.writeValue(os, authRequest);
            os.flush();
        }
    }

    private RuntimeException mapErrorResponse(int responseCode) {
        return switch (responseCode) {
            case HttpURLConnection.HTTP_UNAUTHORIZED -> new InvalidCredentialsException("Credenciales inválidas.");
            case HttpURLConnection.HTTP_FORBIDDEN -> new AccessDeniedException("Acceso denegado.");
            case HttpURLConnection.HTTP_BAD_REQUEST -> new BadRequestException("Solicitud incorrecta.");
            default -> new ApiException("Error inesperado, código de respuesta: " + responseCode);
        };
    }
}

/*
    clase de mensaje de respuesta para la parte grafica
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse<T> {
    private T data;
    private String message;
    private boolean success;
}
 */


/**
 * Realiza la autenticación del usuario.
 * Se requiere una contraseña de mínimo 8 caracteres y un número de identificación de mínimo 9 caracteres numéricos.
 *
 * @param authRequest Información de autenticación del usuario.
 * @return MessageResponse<String> que contiene el token en caso de éxito.
 */