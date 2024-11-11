package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.HttpClientConnectionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Optional;
import java.util.concurrent.Callable;

public class FileAPIService extends HttpClientConnectionManager {

    private static final int CHUNK_SIZE = 512 * 1024; // 512 KB

    @Getter
    private Boolean isDownloading = false;
    @Getter
    private Boolean isUploading = false;

    public FileAPIService() {
        super(BaseConfig.BASE_URL, 15000, 30000);
    }

    // --- Métodos finales ---

    public MessageResponse<Void> receiveFileChunk(FileMetadatumDTO fileChunkDTO) {
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithBody("/api/files/upload", HttpMethod.POST, fileChunkDTO, null);
            handleResponseForVoid(connection);
            return new MessageResponse<>(null, true, "Fragmento de archivo recibido exitosamente", null);
        }, "Error al enviar el fragmento de archivo");
    }

    public MessageResponse<FileMetadatumDTO> fetchFileMetadatumById(Long id) {
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithoutBody("/api/files/" + id, HttpMethod.GET, null);
            Optional<FileMetadatumDTO> response = handleResponseWithType(connection, new TypeReference<FileMetadatumDTO>() {
            });
            return response.map(data -> new MessageResponse<>(data, true, "Metadatos de archivo obtenidos exitosamente", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "No se encontraron metadatos de archivo", "No se recibió respuesta válida"));
        }, "Error al recuperar metadatos de archivo");
    }

    public MessageResponse<Void> removeFileMetadatum(Long id) {
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithoutBody("/api/files/" + id, HttpMethod.DELETE, null);
            handleResponseForVoid(connection);
            return new MessageResponse<>(null, true, "Metadatos de archivo eliminados exitosamente", null);
        }, "Error al eliminar metadatos de archivo");
    }

    public MessageResponse<Void> downloadFileById(Long fileId, String downloadPath) {
        isDownloading = true; // Indicar que se está descargando
        HttpURLConnection connection = null;
        try {
            connection = createConnectionWithoutBody("/api/files/download/" + fileId, HttpMethod.GET, null);
            MessageResponse<FileMetadatumDTO> metadataResponse = fetchFileMetadatumById(fileId);
            if (!metadataResponse.isSuccess()) {
                return new MessageResponse<>(null, false, "No se pudo obtener metadatos de archivo", metadataResponse.getErrorMessage());
            }

            String fileName = metadataResponse.getData().getFileName() + "." + metadataResponse.getData().getFileType();
            String fullPath = downloadPath.isEmpty() ? getDefaultDownloadPath() : downloadPath + File.separator + fileName;

            return downloadFileWithProgress(connection, fullPath);
        } catch (Exception e) {
            logger.error("Error al descargar el archivo: {}", e.getMessage());
            return new MessageResponse<>(null, false, "Error al descargar el archivo", e.getMessage());
        } finally {
            disconnectConnection(connection);
            isDownloading = false; // Restablecer el estado
        }
    }

    private MessageResponse<Void> downloadFileWithProgress(HttpURLConnection connection, String fullPath) {
        try (InputStream inputStream = connection.getInputStream(); FileOutputStream outputStream = new FileOutputStream(fullPath)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = connection.getContentLengthLong();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                System.out.printf("Descargando: %.2f%%\n", (totalBytesRead * 100.0 / fileSize)); // Indica el progreso
            }

            return new MessageResponse<>(null, true, "Archivo descargado exitosamente en " + fullPath, null);
        } catch (Exception e) {
            logger.error("Error durante la descarga: {}", e.getMessage());
            return new MessageResponse<>(null, false, "Error durante la descarga del archivo", e.getMessage());
        }
    }

    public MessageResponse<Void> uploadFileChunk(FileMetadatumDTO fileChunkDTO) {
        isUploading = true; // Indicar que se está subiendo
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithBody("/api/files/upload", HttpMethod.POST, fileChunkDTO, null);
            handleResponseForVoid(connection);
            return new MessageResponse<>(null, true, "Archivo subido exitosamente", null);
        }, "Error al subir el archivo");
    }

    public MessageResponse<FileMetadatumDTO> updateFileMetadatum(Long id, FileMetadatumDTO fileChunkDTO) {
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithBody("/api/files/" + id, HttpMethod.PUT, fileChunkDTO, null);
            Optional<FileMetadatumDTO> response = handleResponseWithType(connection, new TypeReference<FileMetadatumDTO>() {
            });
            return response.map(data -> new MessageResponse<>(data, true, "Metadatos de archivo actualizados exitosamente", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "No se pudo actualizar los metadatos de archivo", "No se recibió respuesta válida"));
        }, "Error al actualizar metadatos de archivo");
    }

    // --- Lógica ---

    private <T> MessageResponse<T> executeWithResponseHandling(Callable<MessageResponse<T>> callable, String errorMessage) {
        try {
            return callable.call();
        } catch (Exception e) {
            logger.error("{}: {}", errorMessage, e.getMessage());
            return new MessageResponse<>(null, false, errorMessage, e.getMessage());
        }
    }

    private String getDefaultDownloadPath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads"; // Ruta por defecto de descargas en Windows
    }
}
