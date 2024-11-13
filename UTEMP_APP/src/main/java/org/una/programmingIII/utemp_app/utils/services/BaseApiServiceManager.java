package org.una.programmingIII.utemp_app.utils.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Pageable;
import org.una.programmingIII.utemp_app.dtos.PageDTO;
import org.una.programmingIII.utemp_app.dtos.UniversityDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.HttpMethod;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

public abstract class BaseApiServiceManager<T> extends HttpClientConnectionManager {
    protected final TypeReference<T> typeEntityReference;
    protected String ENTITY_ENDPOINT; // no va a cambiar

    public BaseApiServiceManager(String ENTITY_ENDPOINT, String baseUrl) {
        super(baseUrl, 15000, 30000);
        this.ENTITY_ENDPOINT = ENTITY_ENDPOINT;
        this.typeEntityReference = new TypeReference<T>() {
        };
    }

    // CRUD Operations

    // Crear
    public MessageResponse<Void> createEntity(T entity) {
        try {
            HttpURLConnection connection = createConnectionWithBody(ENTITY_ENDPOINT, HttpMethod.POST, entity, null);
            return executeVoidRequest(connection);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error creando entidad: ", e.getMessage());
        }
    }

    // Leer todos
    public MessageResponse<PageDTO<T>> getAllEntities(Pageable pageable) {
        String endpointWithPagination = ENTITY_ENDPOINT + "?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize();
        try {
            TypeReference<PageDTO<T>> typeReference = new TypeReference<PageDTO<T>>() {
            };
            return executeConnectionWithType(endpointWithPagination, HttpMethod.GET, typeReference, null);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error obteniendo entidades: ", e.getMessage());
        }
    }

    public MessageResponse<PageDTO<T>> getAllEntities(Pageable pageable, TypeReference<PageDTO<T>> typeReference) {
        String endpointWithPagination = ENTITY_ENDPOINT + "?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize();
        try {
            return executeConnectionWithType(endpointWithPagination, HttpMethod.GET, typeReference, null);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error obteniendo entidades: ", e.getMessage());
        }
    }


    public MessageResponse<PageDTO<UniversityDTO>> getAllEntities2(Pageable pageable) {
        String endpointWithPagination = ENTITY_ENDPOINT + "?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize();
        try {
            TypeReference<PageDTO<UniversityDTO>> typeReference = new TypeReference<PageDTO<UniversityDTO>>() {
            };
            return executeConnectionWithType(endpointWithPagination, HttpMethod.GET, typeReference, null);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error obteniendo entidades: ", e.getMessage());
        }
    }


    // Leer por ID
    protected MessageResponse<T> getEntityById(Long id) {
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        try {
            return executeConnectionWithType(endpoint, HttpMethod.GET, typeEntityReference, null);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error obteniendo entidad por ID: ", e.getMessage());
        }
    }

    // Actualizar
    public MessageResponse<Void> updateEntity(Long id, T entity) {
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        try {
            HttpURLConnection connection = createConnectionWithBody(endpoint, HttpMethod.PUT, entity, null);
            return executeVoidRequest(connection);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error actualizando entidad: ", e.getMessage());
        }
    }

    // Eliminar
    public MessageResponse<Void> deleteEntity(Long id) {
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        try {
            return executeVoidConnection(endpoint, HttpMethod.DELETE, null);
        } catch (Exception e) {
            return new MessageResponse<>(null, false, "Error eliminando entidad: ", e.getMessage());
        }
    }

    // Método genérico para ejecutar una conexión sin esperar un cuerpo de respuesta
    protected MessageResponse<Void> executeVoidRequest(HttpURLConnection connection) {
        try {
            boolean success = super.handleResponseForVoid(connection);
            return new MessageResponse<>(null, success, success ? "Operación exitosa" : "Error en la conexión", null);
        } catch (Exception e) {
            logger.error("Error en la conexión: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    // Método genérico para ejecutar una conexión sin esperar un cuerpo de respuesta
    protected MessageResponse<Void> executeVoidRequest(String endpoint, HttpMethod method, Object entity) {
        HttpURLConnection connection;
        try {
            connection = super.createConnectionWithBody(endpoint, method, entity, null);
            return executeVoidRequest(connection);
        } catch (Exception e) {
            logger.error("Error creando conexión para {}: {}", endpoint, e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error al crear conexión: ", e.getMessage());
        }
    }

    // Método genérico para ejecutar una conexión ya creada y esperar un tipo de respuesta específico
    protected <C> MessageResponse<C> executeCustomRequest(HttpURLConnection connection, TypeReference<C> responseType) {
        try {
            Optional<C> response = super.handleResponseWithType(connection, responseType);
            return response.map(c -> new MessageResponse<>(c, true, "Operación exitosa", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "Error en la respuesta", "No se recibió respuesta válida"));
        } catch (Exception e) {
            logger.error("Error en la conexión: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    // Método genérico para ejecutar una conexión ya creada y esperar un tipo de respuesta específico
    protected <C> MessageResponse<C> executeCustomRequest(String endpoint, HttpMethod method, Object entity, Map<String, String> customHeaders, TypeReference<C> responseType) {
        HttpURLConnection connection = null;
        try {
            connection = createConnectionWithBody(endpoint, method, entity, customHeaders);
            Optional<C> response = super.handleResponseWithType(connection, responseType);
            return response.map(c -> new MessageResponse<>(c, true, "Operación exitosa", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "Error en la respuesta", "No se recibió respuesta válida"));
        } catch (Exception e) {
            logger.error("Error en la conexión: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error en la conexión", e.getMessage());
        } finally {
            disconnectConnection(connection);
        }
    }

    // Método para obtener entidades desde un endpoint
    protected <C> MessageResponse<C> getEntitiesByEndpoint(String endpoint, TypeReference<C> responseType) {
        try {
            return super.executeConnectionWithType(endpoint, HttpMethod.GET, responseType, null);
        } catch (Exception e) {
            logger.error("Error obteniendo entidades desde {}: {}", endpoint, e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error obteniendo entidades: ", e.getMessage());
        }
    }
}