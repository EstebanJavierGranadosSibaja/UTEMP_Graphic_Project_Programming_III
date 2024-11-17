package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
import org.una.programmingIII.utemp_app.exceptions.CustomException;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.HttpClientConnectionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Getter
public class FileAPIService extends HttpClientConnectionManager {// no borrar este comentario. esta clase es parte del app frontend que envia solicitudes al api

    private static final String ENTITY_ENDPOINT = "/files";
    private static final TypeReference<FileMetadatumDTO> RESPONSE_TYPE = new TypeReference<FileMetadatumDTO>() {
    };

    private static final Long CHUNK_SIZE = 512L * 1024; // 512 KB en bytes
    private static final Long MAX_SIZE = 10L * 1024 * 1024; // 10 MB en bytes

    public FileAPIService() {
        super(BaseConfig.BASE_URL, 15000, 30000);
    }

    /**
     * Crea metadatos para un archivo.
     */
    public MessageResponse<FileMetadatumDTO> createMetadata(FileMetadatumDTO fileMetadatumDTO) {
        return executeCustomRequest(ENTITY_ENDPOINT, HttpMethod.POST, fileMetadatumDTO, null, RESPONSE_TYPE);
    }

    /**
     * Obtiene los metadatos de un archivo por su ID.
     */
    public MessageResponse<FileMetadatumDTO> findMetadataById(Long id) {
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return executeConnectionWithType(endpoint, HttpMethod.GET, RESPONSE_TYPE, null);
    }

    /**
     * Elimina los metadatos de un archivo por su ID.
     */
    public MessageResponse<Void> deleteMetadataById(Long id) {
        String endpoint = ENTITY_ENDPOINT + "/" + id;
        return executeVoidRequest(endpoint, HttpMethod.DELETE, null);
    }


    public MessageResponse<Void> upLoadFile(FileMetadatumDTO fileMetadatumDTO, String filepath) {
        File file = new File(filepath);
        if (!file.exists() || !file.isFile()) {
            return new MessageResponse<>(null, false, "Archivo no encontrado", "El archivo no existe en la ruta especificada.");
        }

        if (file.length() > MAX_SIZE) {
            return new MessageResponse<>(null, false, "El archivo es demasiado grande", "El tamaño máximo permitido es 10MB.");
        }

        // Usar CompletableFuture para ejecutar la carga en un hilo independiente
        CompletableFuture.runAsync(() -> {
            try (InputStream fileStream = new FileInputStream(file)) {
                // Subir el archivo en fragmentos
                uploadFileInChunks(fileMetadatumDTO, fileStream, file.length());
            } catch (IOException e) {
                logger.error("Error al cargar el archivo: {}", e.getMessage(), e);
            }
        });

        // Responder inmediatamente al cliente que la conexión fue exitosa
        return new MessageResponse<>(null, true, "Conexión establecida, comenzando carga del archivo...", null);
    }

    private void uploadFileInChunks(FileMetadatumDTO fileMetadatumDTO, InputStream fileStream, long fileSize) {
        String endpoint = ENTITY_ENDPOINT + "/receive-chunk";
        Map<String, String> headers = Map.of(
                "Content-Type", "multipart/form-data",
                "File-Metadata", fileMetadatumDTO.toString()
        );

        try {
            byte[] buffer = new byte[CHUNK_SIZE.intValue()];
            int bytesRead;
            long totalBytesSent = 0;
            int chunkIndex = 0;

            // Calcular el total de fragmentos de manera precisa
            int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);

            while ((bytesRead = fileStream.read(buffer)) != -1) {
                byte[] chunkData = Arrays.copyOfRange(buffer, 0, bytesRead);
                totalBytesSent += bytesRead;

                // Enviar el fragmento del archivo
                Map<String, Object> body = Map.of(
                        "fileChunk", chunkData,
                        "chunkIndex", chunkIndex,
                        "totalChunks", totalChunks,
                        "fileSize", fileSize
                );

                // Mostrar progreso en consola
                double progress = (double) totalBytesSent / fileSize * 100;
                System.out.println(String.format("Subiendo archivo: %.2f%% completado", progress));

                // Ejecutar la solicitud para subir el fragmento
                MessageResponse<Void> response = executeCustomRequest(endpoint, HttpMethod.POST, body, headers, new TypeReference<Void>() {
                });
                if (!response.isSuccess()) {
                    System.err.println("Error al subir el fragmento " + chunkIndex);
                    return; // Si alguna parte del archivo falla, retorna el error
                }

                chunkIndex++; // Incrementar el índice del fragmento
            }

            // Confirmación de que el archivo se ha subido completamente
            System.out.println("Archivo subido con éxito.");

        } catch (IOException e) {
            logger.error("Error al subir el archivo en fragmentos: {}", e.getMessage(), e);
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

    /**
     * Ejecuta una solicitud personalizada con respuesta.
     */
    private <T> MessageResponse<T> executeCustomRequest(String endpoint, HttpMethod method, Object body, Map<String, String> headers, TypeReference<T> responseType) {
        try {
            HttpURLConnection connection = createConnectionWithBody(endpoint, method, body, headers);
            return handleResponse(connection, responseType);
        } catch (Exception e) {
            logger.error("Error ejecutando solicitud: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error ejecutando solicitud", e.getMessage());
        }
    }

    /**
     * Ejecuta una solicitud sin cuerpo de respuesta.
     */
    private MessageResponse<Void> executeVoidRequest(String endpoint, HttpMethod method, Object body) {
        try {
            HttpURLConnection connection = createConnectionWithBody(endpoint, method, body, null);
            boolean success = handleResponseForVoid(connection);
            return new MessageResponse<>(null, success, success ? "Operación exitosa" : "Error en la operación", null);
        } catch (Exception e) {
            logger.error("Error ejecutando solicitud: {}", e.getMessage(), e);
            return new MessageResponse<>(null, false, "Error ejecutando solicitud", e.getMessage());
        }
    }

    /**
     * Maneja la respuesta de la conexión.
     */
    private <T> MessageResponse<T> handleResponse(HttpURLConnection connection, TypeReference<T> responseType) throws IOException {
        try {
            Optional<T> response = super.handleResponseWithType(connection, responseType);
            return response.map(data -> new MessageResponse<>(data, true, "Operación exitosa", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "Respuesta inválida", "El servidor no devolvió datos válidos"));
        } catch (Exception e) {
            throw new IOException("Error procesando la respuesta", e);
        } finally {
            disconnectConnection(connection);
        }
    }
}


//public MessageResponse<Void> uploadFile(final File file) {
//    return null;
//}
//private static final int CHUNK_SIZE = 512 * 1024;
//private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

//private void showNotification(String title, String message) {
//    System.out.println(title + ":\n" + message);
//}
//private String getDefaultDownloadPath() {
//    return System.getProperty("user.home") + File.separator + "Downloads";
