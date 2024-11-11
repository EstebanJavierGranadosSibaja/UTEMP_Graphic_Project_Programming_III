package org.una.programmingIII.utemp_app.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.exceptions.AccessDeniedException;
import org.una.programmingIII.utemp_app.exceptions.BadRequestException;
import org.una.programmingIII.utemp_app.exceptions.InvalidCredentialsException;
import org.una.programmingIII.utemp_app.exceptions.http.ApiException;
import org.una.programmingIII.utemp_app.requests.AuthRequest;
import org.una.programmingIII.utemp_app.responses.ApiResponse;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.responses.TokenResponse;
import org.una.programmingIII.utemp_app.utils.services.HttpClientConnectionManager;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Optional;


/**
 * Servicio para la autenticación de clientes. del la aplicacion de fronten con el controller de la APi
 */
public class AuthService extends HttpClientConnectionManager {

    public AuthService() {
        super(BaseConfig.BASE_URL, 10000, 30000);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public MessageResponse<TokenResponse> login(AuthRequest authRequest) {
        String title = "Autenticación fallida";
        StringBuilder errorMessages = new StringBuilder();
        HttpURLConnection connection = null;

        try {
            connection = createLoginConnection();
            validateAuthRequest(authRequest);
            setupConnectionForOutput(connection);
            sendAuthRequest(connection, authRequest);

            // Manejo de la respuesta de autenticación
            Optional<ApiResponse<TokenResponse>> apiResponseOptional = handleResponseWithType(
                    connection, new TypeReference<ApiResponse<TokenResponse>>() {
                    }
            );

            // Procesar la respuesta de autenticación
            if (apiResponseOptional.isPresent()) {
                TokenResponse tokenResponse = apiResponseOptional.get().getData();
                setToken(tokenResponse.getToken());
                title = "Autenticación exitosa";
                return new MessageResponse<>(tokenResponse, true, title, null);
            } else {
                throw new ApiException("Token no disponible después de autenticación.");
            }

        } catch (InvalidCredentialsException | AccessDeniedException | BadRequestException e) {
            return createErrorResponse(title, errorMessages, e.getMessage());
        } catch (ApiException e) {
            return createErrorResponse(title, errorMessages, "Error en el servidor: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse(title, errorMessages, "Error al autenticar: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private MessageResponse<TokenResponse> createErrorResponse(String title, StringBuilder errorMessages, String errorMessage) {
        appendErrorMessage(errorMessages, errorMessage);
        return new MessageResponse<>(null, false, title, errorMessages.toString().trim());
    }

    private void appendErrorMessage(StringBuilder errorMessages, String message) {
        if (!errorMessages.isEmpty()) {
            errorMessages.append("\n");
        }
        errorMessages.append(message);
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

    private void sendAuthRequest(HttpURLConnection connection, AuthRequest authRequest) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            objectMapper.writeValue(os, authRequest);
            os.flush();
        }
    }
}