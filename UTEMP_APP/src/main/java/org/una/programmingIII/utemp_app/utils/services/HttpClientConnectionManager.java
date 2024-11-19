package org.una.programmingIII.utemp_app.utils.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.una.programmingIII.utemp_app.exceptions.CustomException;
import org.una.programmingIII.utemp_app.exceptions.http.*;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.Views;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("All")
public class HttpClientConnectionManager {
    protected static final Logger logger = LoggerFactory.getLogger(HttpClientConnectionManager.class);
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String CONTENT_TYPE_HEADER = "Content-Type";
    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String TYPE_JSON = "application/json";
    protected static String token;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final String baseUrl;
    private final int connectTimeout;
    private final int readTimeout;
    protected String message;

    protected HttpClientConnectionManager(String baseUrl, int connectTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected static void setToken(String token) {
        HttpClientConnectionManager.token = token;
    }

    protected void setupConnectionProperties(HttpURLConnection connection, HttpMethod method) throws IOException {
        connection.setRequestProperty(ACCEPT_HEADER, TYPE_JSON);
        connection.setRequestMethod(method.name());
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        logger.info("Connection properties set: Method = {}, Connect Timeout = {}, Read Timeout = {}", method.name(), connectTimeout, readTimeout);
    }

    // Mejor manejo de la excepción cuando no se puede establecer conexión o token es nulo
    protected HttpURLConnection createConnection(String endpoint, HttpMethod method, Map<String, String> customHeaders) throws CustomException, IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URI(baseUrl + endpoint).toURL().openConnection();
            setupConnectionProperties(connection, method);

            validateToken();

            connection.setRequestProperty(AUTHORIZATION_HEADER, "Bearer " + token);
            addCustomHeaders(connection, customHeaders);
        } catch (TokenException e) {
            logger.error("Token no válido o no presente: {}", e.getMessage());
            ViewManager.getInstance().showMainView(Views.LOGIN);  // Mostramos la vista de login si no hay token.
            throw new CustomException("No se pudo establecer la conexión: Token inválido.");
        } catch (IOException e) {
            logger.error("Error creando conexión con el endpoint {}: {}", endpoint, e.getMessage());
            throw new CustomException("No se pudo establecer la conexión con el servidor.");
        } catch (URISyntaxException e) {
            logger.error("Error con la URI: {}", e.getMessage());
            throw new CustomException("Error con la URI del endpoint.");
        }
        return connection;
    }

    protected <R> void writeDataToConnection(HttpURLConnection connection, R dato) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(dato);
            os.write(input);
        } catch (IOException e) {
            logger.error("Error writing data to connection: {}", e.getMessage(), e);
            throw e;
        }
    }

    protected HttpURLConnection createLoginConnection() throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URI(baseUrl + "/auth/login").toURL().openConnection();
            setupConnectionProperties(connection, HttpMethod.POST);
        } catch (IOException e) {
            logger.error("Error creando conexión para login: {}", e.getMessage(), e);
            throw new IOException("Error al establecer la conexión para login.");
        } catch (URISyntaxException e) {
            logger.error("Error con la URI del login: {}", e.getMessage(), e);
            throw new RuntimeException("Error con la URI del login.");
        }
        return connection;
    }

    protected void setupConnectionForOutput(HttpURLConnection connection) {
        connection.setRequestProperty(CONTENT_TYPE_HEADER, TYPE_JSON);
        connection.setDoOutput(true);
    }

    protected HttpURLConnection createConnectionWithoutBody(String endpoint, HttpMethod method, Map<String, String> customHeaders) throws CustomException, IOException {
        return createConnection(endpoint, method, customHeaders);
    }

    protected HttpURLConnection createConnectionWithBody(String endpoint, HttpMethod method, Object body, Map<String, String> customHeaders) throws CustomException, IOException {
        HttpURLConnection connection = createConnection(endpoint, method, customHeaders);
        if (body != null) {
            setupConnectionForOutput(connection);
            writeDataToConnection(connection, body);
        }
        return connection;
    }

    protected MessageResponse<Void> executeVoidConnection(String endpoint, HttpMethod method, Map<String, String> customHeaders) {
        HttpURLConnection connection = null;
        try {
            connection = createConnectionWithoutBody(endpoint, method, customHeaders);
            boolean success = handleResponseForVoid(connection);
            String responseMessage = success ? "Operación exitosa" : "Error en la conexión";
            return new MessageResponse<>(null, success, responseMessage, null);
        } catch (IOException | CustomException e) {
            logger.error("Error al ejecutar conexión para el endpoint {}: {}", endpoint, e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    protected <C> MessageResponse<C> executeConnectionWithType(String endpoint, HttpMethod method, TypeReference<C> tipoEsperado, Map<String, String> customHeaders) {
        HttpURLConnection connection = null;
        try {
            connection = createConnection(endpoint, method, customHeaders);
            Optional<C> response = handleResponseWithType(connection, tipoEsperado);
            return response.map(c -> new MessageResponse<>(c, true, "Operación exitosa", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "Error en la respuesta", "No se recibió respuesta válida"));
        } catch (IOException | CustomException e) {
            logger.error("Error al ejecutar conexión para el endpoint {}: {}", endpoint, e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    protected void addCustomHeaders(HttpURLConnection connection, Map<String, String> customHeaders) {
        if (customHeaders != null) {
            for (Map.Entry<String, String> header : customHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
    }

    protected void validateToken() throws CustomException {
        if (token == null || token.isEmpty()) {
            logger.info("Token no presente o vacío.");
            throw new TokenException("No se proporcionó un token válido.");
        }
    }

    // Manejo de respuestas para tipo específico con mayor claridad en los errores
    protected <C> Optional<C> handleResponseWithType(HttpURLConnection connection, TypeReference<C> typeReference) {
        try {
            int responseCode = connection.getResponseCode();
            validateResponseCode(responseCode);
            String responseContent = readResponse(connection);
            return Optional.of(objectMapper.readValue(responseContent, typeReference));
        } catch (IOException | ApiException e) {
            logger.error("Error al manejar la respuesta de la conexión: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    protected boolean handleResponseForVoid(HttpURLConnection connection) {
        try {
            int responseCode = connection.getResponseCode();
            logger.info("Código de respuesta: {}", responseCode);
            validateResponseCode(responseCode);
            return true;
        } catch (IOException | ApiException e) {
            message = e.getMessage();
            logger.error("Error al obtener el código de respuesta: {}", e.getMessage());
            return false;
        }
    }

    protected String readResponse(HttpURLConnection connection) throws IOException {
        logger.info("Leyendo respuesta de la conexión...");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            logger.info("Respuesta recibida: {}", response);
            return response.toString();
        } catch (IOException e) {
            logger.error("Error al leer la respuesta: {}", e.getMessage(), e);
            throw new IOException("Error al leer la respuesta del servidor.");
        }
    }

    protected void validateResponseCode(int responseCode) throws ApiException {
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_CREATED:
            case HttpURLConnection.HTTP_PARTIAL:
            case HttpURLConnection.HTTP_ACCEPTED:
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new UnauthorizedException("Acceso no autorizado. Código de respuesta: " + responseCode);
            case HttpURLConnection.HTTP_FORBIDDEN:
                throw new ForbiddenException("Acceso prohibido. Código de respuesta: " + responseCode);
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new NotFoundException("Recurso no encontrado. Código de respuesta: " + responseCode);
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new InternalServerErrorException("Error interno del servidor. Código de respuesta: " + responseCode);
            default:
                logger.error("Código de respuesta inesperado: {}", responseCode);
                throw new ApiException("Error: " + responseCode);
        }
    }

    protected <C> Optional<C> parseResponse(String responseContent, TypeReference<C> typeReference) {
        try {
            logger.info("Parsing response...");
            return Optional.of(objectMapper.readValue(responseContent, typeReference));
        } catch (IOException e) {
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