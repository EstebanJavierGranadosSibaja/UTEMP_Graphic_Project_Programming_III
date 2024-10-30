package org.una.programmingIII.utemp_app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.una.programmingIII.utemp_app.services.responses.ApiResponse;
import org.una.programmingIII.utemp_app.services.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.responses.PageResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Optional;

/**
 * Propósito: Esta clase actúa como una clase base para manejar las interacciones comunes con un API.
 * Proporciona métodos para crear conexiones HTTP, leer respuestas y parsear datos de las respuestas en
 * formatos específicos (como ApiResponse y PageResponse).
 * Descripción: Clase abstracta que define operaciones CRUD básicas para entidades.
 * Proporciona una capa de abstracción que facilita la implementación de servicios específicos, promoviendo la reutilización de lógica común y la separación de responsabilidades.
 *
 * @param <T> El tipo de entidad que maneja el servicio.
 */
public abstract class BaseService<T> extends BaseHttpClient<T> {

    protected final String endPoint;

    // Constructor
    public BaseService(String endPoint, String baseUrl) {
        super(baseUrl, 15000, 30000);
        this.endPoint = endPoint;
    }

    // Métodos CRUD

    public MessageResponse<T> create(T entity) {
        return executeHttpMethod(HttpMethod.POST, entity, null);
    }

    public MessageResponse<T> findById(Long id) {
        return executeHttpMethod(HttpMethod.GET, null, id);
    }

    public MessageResponse<T> findByIdentifier(String identifier) {
        return executeHttpMethod(HttpMethod.GET, null, identifier);
    }

    public MessageResponse<PageResponse<T>> findAll(int page, int size) {
        HttpURLConnection connection = null;
        MessageResponse<PageResponse<T>> response = new MessageResponse<>();

        try {
            connection = createAuthenticatedConnection(endPoint + "?page=" + page + "&size=" + size, HttpMethod.GET);
            Optional<ApiResponse<PageResponse<T>>> apiResponseOptional = handleResponse(connection, new TypeReference<ApiResponse<PageResponse<T>>>() {});
            if (apiResponseOptional.isPresent()) {
                ApiResponse<PageResponse<T>> apiResponse = apiResponseOptional.get();
                response.setData(apiResponse.getData());
                response.setMessage(apiResponse.getMessage());
                response.setSuccess(true);
            } else {
                response.setMessage("Error retrieving data.");
                response.setSuccess(false);
            }
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            disconnectConnection(connection);
        }

        return response;
    }

    public MessageResponse<T> update(Long id, T entity) {
        return executeHttpMethod(HttpMethod.PUT, entity, id);
    }

    public MessageResponse<Boolean> delete(Long id, boolean isPermanentDelete) {
        HttpURLConnection connection = null;
        MessageResponse<Boolean> response = new MessageResponse<>();

        try {
            connection = createAuthenticatedConnection(endPoint + "/" + id + "?isPermanentDelete=" + isPermanentDelete, HttpMethod.DELETE);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                response.setData(true);
                response.setSuccess(true);
                response.setMessage("Resource deleted successfully.");
            } else {
                response.setData(false);
                response.setSuccess(false);
                response.setMessage("Error deleting resource: " + responseCode);
            }
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            disconnectConnection(connection);
        }

        return response;
    }

    // Método auxiliar para ejecutar las solicitudes HTTP
    private MessageResponse<T> executeHttpMethod(HttpMethod method, T entity, Object identifier) {
        HttpURLConnection connection = null;
        MessageResponse<T> response = new MessageResponse<>();

        try {
            String url = (identifier != null) ? endPoint + (identifier instanceof Long ? "/" + identifier : "/identification/" + identifier) : endPoint;
            connection = createAuthenticatedConnection(url, method);

            // Añadir el token a la solicitud
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            if (entity != null) {
                setupConnectionForOutput(connection);
                try (OutputStream os = connection.getOutputStream()) {
                    objectMapper.writeValue(os, entity);
                }
            }

            Optional<ApiResponse<T>> apiResponseOptional = handleResponse(connection);
            if (apiResponseOptional.isPresent()) {
                ApiResponse<T> apiResponse = apiResponseOptional.get();
                response.setData(apiResponse.getData());
                response.setMessage(apiResponse.getMessage());
                response.setSuccess(true);
            } else {
                response.setMessage("Error processing response.");
                response.setSuccess(false);
            }
        } catch (IOException e) {
            response.setMessage("I/O error: " + e.getMessage());
            response.setSuccess(false);
        } catch (Exception e) {
            response.setMessage("Unexpected error: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            disconnectConnection(connection);
        }

        return response;
    }
}
