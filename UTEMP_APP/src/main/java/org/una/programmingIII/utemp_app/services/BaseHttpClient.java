package org.una.programmingIII.utemp_app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.una.programmingIII.utemp_app.exceptions.http.*;
import org.una.programmingIII.utemp_app.services.responses.ApiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

/**
 * Descripción: Esta clase se encarga de la gestión de conexiones HTTP, configurando propiedades de solicitud, enviando peticiones y manejando respuestas.
 * Proporciona métodos reutilizables para realizar operaciones de red de manera eficiente y centralizada.
 * Se encarga de la gestión de la conexión y respuestas de esta.
 * Recibe un transfer object como tipo para saber qué va a recibir (T).
 *
 * @param <T> El tipo de objeto que maneja el cliente.
 */
public class BaseHttpClient<T> implements HttpConfig {
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final String baseUrl;
    private final int connectTimeout;
    private final int readTimeout;
    protected static final Logger logger = LoggerFactory.getLogger(BaseHttpClient.class);
    protected static String token;

    protected static void setToken(String token) {
        BaseHttpClient.token = token;
    }

    public BaseHttpClient(String baseUrl, int connectTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    // Métodos para conexiones autenticadas
    protected HttpURLConnection createAuthenticatedConnection(String endpoint, HttpMethod method) throws Exception {
        HttpURLConnection connection = createConnection(endpoint, method);
        connection.setRequestProperty("Authorization", "Bearer " + token);
        return connection;
    }

    // Métodos para conexiones no autenticadas
    protected HttpURLConnection createUnauthenticatedConnection(String endpoint, HttpMethod method) throws Exception {
        return createConnection(endpoint, method);
    }

    private HttpURLConnection createConnection(String endpoint, HttpMethod method) throws Exception {
        URI uri = new URI(baseUrl + endpoint);
        logger.info("Creating {} connection to: {}", method.name(), uri);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        setupConnectionProperties(connection, method);
        return connection;
    }

    // Configurar propiedades de la conexión
    protected void setupConnectionProperties(HttpURLConnection connection, HttpMethod method) throws IOException {
        connection.setRequestProperty(ACCEPT, TYPE_JSON);
        connection.setRequestMethod(method.name());
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        logger.info("Connection properties set: Method = {}, Connect Timeout = {}, Read Timeout = {}",
                method.name(), connectTimeout, readTimeout);
    }

    protected void setupConnectionForOutput(HttpURLConnection connection) {
        connection.setRequestProperty(CONTENT_TYPE, TYPE_JSON);
        connection.setDoOutput(true);
    }

    // Leer respuesta del servidor
    protected String readResponse(HttpURLConnection connection) throws Exception {
        logger.info("Reading response from connection");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            logger.info("Response received: {}", response.toString());
            return response.toString();
        } catch (IOException e) {
            logger.error("Error reading response: {}", e.getMessage());
            throw e; // Propagar la excepción después de registrar el error
        }
    }

    // Manejar la respuesta de la conexión
    protected Optional<ApiResponse<T>> handleResponse(HttpURLConnection connection) {
        return handleResponse(connection, new TypeReference<ApiResponse<T>>() {
        });
    }

    // Manejar respuesta con tipo genérico
    protected <C> Optional<ApiResponse<C>> handleResponse(HttpURLConnection connection, TypeReference<ApiResponse<C>> typeReference) {
        try {
            int responseCode = connection.getResponseCode();
            logger.info("Response code: {}", responseCode);
            validateResponseCode(responseCode);

            String responseContent = readResponse(connection);
            logger.info("Raw response content: {}", responseContent);

            Optional<ApiResponse<C>> apiResponseOptional = parseApiResponse(responseContent, typeReference);
            if (apiResponseOptional.isPresent()) {
                ApiResponse<C> apiResponse = apiResponseOptional.get();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    logger.error("Error: {}", apiResponse.getMessage());
                }
                return Optional.of(apiResponse);
            }
        } catch (Exception e) {
            logger.error("Error handling response: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    // Validar el código de respuesta
    protected void validateResponseCode(int responseCode) throws ApiException {
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK:
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

    // Parsear la respuesta de la API
    protected <C> Optional<ApiResponse<C>> parseApiResponse(String responseContent, TypeReference<ApiResponse<C>> typeReference) {
        try {
            logger.info("Parsing API response...");
            ApiResponse<C> apiResponse = objectMapper.readValue(responseContent, typeReference);
            return Optional.of(apiResponse);
        } catch (Exception e) {
            logger.error("Error parsing API response: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    // Desconectar la conexión
    protected void disconnectConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }
}