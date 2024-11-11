package org.una.programmingIII.utemp_app.utils.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.una.programmingIII.utemp_app.exceptions.CustomException;
import org.una.programmingIII.utemp_app.exceptions.http.*;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class HttpClientConnectionManager {
    protected static final Logger logger = LoggerFactory.getLogger(HttpClientConnectionManager.class);
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String CONTENT_TYPE_HEADER = "Content-Type";
    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String TYPE_JSON = "application/json";
    protected static String token;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final String baseUrl; // "http://localhost:8080/utemp" va a recibir este
    private final int connectTimeout;
    private final int readTimeout;
    protected String message;

    protected HttpClientConnectionManager(String baseUrl, int connectTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    protected static void setToken(String token) {
        HttpClientConnectionManager.token = token;
    }

    // Métodos de conexión
    protected HttpURLConnection createConnection(String endpoint, HttpMethod method, Map<String, String> customHeaders) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URI(baseUrl + endpoint).toURL().openConnection();
        setupConnectionProperties(connection, method);
        validateToken();
        connection.setRequestProperty(AUTHORIZATION_HEADER, "Bearer " + token);

        // Añadir encabezados personalizados
        addCustomHeaders(connection, customHeaders);
        return connection;
    }

    protected HttpURLConnection createLoginConnection() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URI(baseUrl + "/auth/login").toURL().openConnection();
        setupConnectionProperties(connection, HttpMethod.POST);
        return connection;
    }

    protected HttpURLConnection createConnectionWithBody(String endpoint, HttpMethod method, Object body, Map<String, String> customHeaders) throws Exception {
        HttpURLConnection connection = createConnection(endpoint, method, customHeaders);
        if (body != null) {
            setupConnectionForOutput(connection);
            writeDataToConnection(connection, body);
        }
        return connection;
    }

    protected HttpURLConnection createConnectionWithoutBody(String endpoint, HttpMethod method, Map<String, String> customHeaders) throws Exception {
        return createConnection(endpoint, method, customHeaders);
    }

    // Ejecutar conexión para retornar un resultado vacío (solo código de error)
    protected MessageResponse<Void> executeVoidConnection(String endpoint, HttpMethod method, Map<String, String> customHeaders) {
        HttpURLConnection connection = null;
        try {
            connection = createConnectionWithoutBody(endpoint, method, customHeaders);
            boolean success = handleResponseForVoid(connection);
            return new MessageResponse<>(null, success, success ? "Operación exitosa" : "Error en la conexión", null);
        } catch (Exception e) {
            logger.error("Error executing connection: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    // Ejecutar conexión y esperar un tipo de respuesta específico
    protected <C> MessageResponse<C> executeConnectionWithType(String endpoint, HttpMethod method, TypeReference<C> tipoEsperado, Map<String, String> customHeaders) {
        HttpURLConnection connection = null;
        try {
            connection = createConnection(endpoint, method, customHeaders);
            Optional<C> response = handleResponseWithType(connection, tipoEsperado);
            return response.map(c -> new MessageResponse<>(c, true, "Operación exitosa", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "Error en la respuesta", "No se recibió respuesta válida"));
        } catch (Exception e) {
            logger.error("Error executing connection: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    // Métodos auxiliares protegidos para su uso en subclases
    protected void addCustomHeaders(HttpURLConnection connection, Map<String, String> customHeaders) {
        if (customHeaders != null) {
            for (Map.Entry<String, String> header : customHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
    }

    protected void validateToken() {
        if (token == null || token.isEmpty()) {
            logger.info("Token is empty");
            throw new CustomException("No hay token");
        }
    }

    protected <R> void writeDataToConnection(HttpURLConnection connection, R dato) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(dato);
            os.write(input, 0, input.length);
        }
    }

    protected void setupConnectionProperties(HttpURLConnection connection, HttpMethod method) throws IOException {
        connection.setRequestProperty(ACCEPT_HEADER, TYPE_JSON);
        connection.setRequestMethod(method.name());
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        logger.info("Connection properties set: Method = {}, Connect Timeout = {}, Read Timeout = {}", method.name(), connectTimeout, readTimeout);
    }

    protected void setupConnectionForOutput(HttpURLConnection connection) {
        connection.setRequestProperty(CONTENT_TYPE_HEADER, TYPE_JSON);
        connection.setDoOutput(true);
    }

    protected <C> Optional<C> handleResponseWithType(HttpURLConnection connection, TypeReference<C> typeReference) {
        try {
            int responseCode = connection.getResponseCode();
            validateResponseCode(responseCode);
            String responseContent = readResponse(connection);

            return Optional.of(objectMapper.readValue(responseContent, typeReference));
        } catch (Exception e) {
            logger.error("Error handling response: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }


    protected boolean handleResponseForVoid(HttpURLConnection connection) {
        try {
            int responseCode = connection.getResponseCode();
            logger.info("Response code: {}", responseCode);
            validateResponseCode(responseCode);
            return true;
        } catch (Exception e) {
            message = e.getMessage();
            logger.error("Error retrieving response code: {}", e.getMessage());
            return false;
        }
    }

    protected String readResponse(HttpURLConnection connection) throws Exception {
        logger.info("Reading response from connection");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            logger.info("Response received: {}", response);
            return response.toString();
        } catch (IOException e) {
            logger.error("Error reading response: {}", e.getMessage());
            throw e;
        }
    }

    protected void validateResponseCode(int responseCode) throws ApiException {
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_CREATED:
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new UnauthorizedException("Unauthorized access. Response code: " + responseCode);
            case HttpURLConnection.HTTP_FORBIDDEN:
                throw new ForbiddenException("Access denied. Response code: " + responseCode);
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new NotFoundException("Resource not found. Response code: " + responseCode);
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new InternalServerErrorException("Internal server error. Response code: " + responseCode);
            default:
                logger.error("Unexpected response code: {}", responseCode);
                throw new ApiException("Error: " + responseCode);
        }
    }

    protected <C> Optional<C> parseResponse(String responseContent, TypeReference<C> typeReference) {
        try {
            logger.info("Parsing response...");
            return Optional.of(objectMapper.readValue(responseContent, typeReference));
        } catch (Exception e) {
            logger.error("Error parsing response: {}", e.getMessage());
            return Optional.empty();
        }
    }

    protected void disconnectConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }
}