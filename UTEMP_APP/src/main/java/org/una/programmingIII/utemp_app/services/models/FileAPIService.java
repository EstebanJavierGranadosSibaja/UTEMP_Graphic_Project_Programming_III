package org.una.programmingIII.utemp_app.services.models;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import lombok.Getter;
import org.una.programmingIII.utemp_app.configs.BaseConfig;
import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
import org.una.programmingIII.utemp_app.responses.MessageResponse;
import org.una.programmingIII.utemp_app.services.HttpMethod;
import org.una.programmingIII.utemp_app.utils.services.HttpClientConnectionManager;
import org.una.programmingIII.utemp_app.utils.view.ViewManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;

@Getter
public class FileAPIService extends HttpClientConnectionManager {

    private static final int CHUNK_SIZE = 512 * 1024;
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private Boolean isDownloading = false;
    private Boolean isUploading = false;

    public FileAPIService() {
        super(BaseConfig.BASE_URL, 15000, 30000);
    }

    public MessageResponse<FileMetadatumDTO> fetchFileMetadataById(Long fileId) {
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithoutBody("/api/files/" + fileId, HttpMethod.GET, null);
            Optional<FileMetadatumDTO> response = handleResponseWithType(connection, new TypeReference<FileMetadatumDTO>() {
            });
            return response.map(data -> new MessageResponse<>(data, true, "Metadatos obtenidos exitosamente", null))
                    .orElseGet(() -> new MessageResponse<>(null, false, "No se encontraron metadatos", "No se recibió respuesta válida"));
        }, "Error al obtener los metadatos del archivo");
    }

    public MessageResponse<Void> uploadFile(String filePath, FileMetadatumDTO fileMetadata) {
        new Thread(() -> {
            isUploading = true;
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    showNotification("Error", "El archivo especificado no existe.");
                    return;
                }
                if (file.length() > MAX_FILE_SIZE) {
                    showNotification("Error", "El archivo es demasiado grande.");
                    return;
                }

                prepareFileMetadata(file, fileMetadata);
                MessageResponse<Void> response = uploadFileChunks(file, fileMetadata);
                showNotification(response.isSuccess() ? "Éxito" : "Error",
                        response.isSuccess() ? "Archivo subido exitosamente." : response.getErrorMessage());
            } catch (Exception e) {
                handleError("Error al subir el archivo", e);
            } finally {
                isUploading = false;
            }
        }).start();
        return new MessageResponse<>(null, true, "Conexión establecida, subiendo archivo en segundo plano.", null);
    }

    private void handleError(String errorMessage, Exception e) {
        logger.error("{}: {}", errorMessage, e.getMessage());
        showNotification("Error", errorMessage + ": " + e.getMessage());
    }

    private void prepareFileMetadata(File file, FileMetadatumDTO fileMetadata) {
        fileMetadata.setFileSize(file.length());
        fileMetadata.setStoragePath(file.getAbsolutePath());
        fileMetadata.setTotalChunks((int) Math.ceil((double) file.length() / CHUNK_SIZE));
    }

    private MessageResponse<Void> uploadFileChunks(File file, FileMetadatumDTO fileMetadata) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead, chunkIndex = 0;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileMetadata.setFileChunk(Arrays.copyOfRange(buffer, 0, bytesRead));
                fileMetadata.setChunkIndex(chunkIndex++);
                MessageResponse<Void> uploadResponse = uploadFileChunk(fileMetadata);
                if (!uploadResponse.isSuccess()) {
                    return new MessageResponse<>(null, false, "Error al subir fragmento", uploadResponse.getErrorMessage());
                }
            }
        } catch (IOException e) {
            return new MessageResponse<>(null, false, "Error", e.getMessage());
        }
        return new MessageResponse<>(null, true, "Archivo subido exitosamente", null);
    }

    private MessageResponse<Void> uploadFileChunk(FileMetadatumDTO fileChunkDTO) {
        return executeWithResponseHandling(() -> {
            HttpURLConnection connection = createConnectionWithBody("/api/files/upload", HttpMethod.POST, fileChunkDTO, null);
            handleResponseForVoid(connection);
            return new MessageResponse<>(null, true, "Fragmento de archivo subido exitosamente", null);
        }, "Error al subir el fragmento");
    }

    public MessageResponse<Void> downloadFileById(Long fileId, String downloadPath) {
        new Thread(() -> {
            isDownloading = true;
            try {
                MessageResponse<FileMetadatumDTO> metadataResponse = fetchFileMetadataById(fileId);
                if (!metadataResponse.isSuccess()) {
                    showNotification("Error", metadataResponse.getErrorMessage());
                    return;
                }

                String fileName = metadataResponse.getData().getFileName() + "." + metadataResponse.getData().getFileType();
                String fullPath = downloadPath.isEmpty() ? getDefaultDownloadPath() : downloadPath + File.separator + fileName;

                MessageResponse<Void> downloadResponse = downloadFileWithProgress(fileId, fullPath);
                showNotification(downloadResponse.isSuccess() ? "Éxito" : "Error",
                        downloadResponse.isSuccess() ? "Archivo descargado exitosamente." : downloadResponse.getErrorMessage());
            } catch (Exception e) {
                logger.error("Error al descargar el archivo: {}", e.getMessage());
                showNotification("Error", "Error al descargar el archivo: " + e.getMessage());
            } finally {
                isDownloading = false;
            }
        }).start();
        return new MessageResponse<>(null, true, "Conexión establecida, descargando archivo en segundo plano.", null);
    }

    private MessageResponse<Void> downloadFileWithProgress(Long fileId, String fullPath) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            connection = createConnectionWithoutBody("/api/files/download/" + fileId, HttpMethod.GET, null);
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(fullPath);

            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            long totalBytesRead = 0, fileSize = connection.getContentLengthLong();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                System.out.printf("Descargando: %.2f%%\n", (totalBytesRead * 100.0 / fileSize));
            }

            return new MessageResponse<>(null, true, "Archivo descargado exitosamente en " + fullPath, null);

        } catch (Exception e) {
            logger.error("Error durante la descarga: {}", e.getMessage());
            return new MessageResponse<>(null, false, "Error durante la descarga del archivo", e.getMessage());

        } finally {
            // Asegurarse de cerrar los recursos
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                logger.error("Error cerrando recursos: {}", ex.getMessage());
            }

            // Asegurarse de desconectar la conexión
            if (connection != null) {
                disconnectConnection(connection);
            }
        }
    }

    private <T> MessageResponse<T> executeWithResponseHandling(Callable<MessageResponse<T>> callable, String errorMessage) {
        try {
            return callable.call();
        } catch (Exception e) {
            logger.error("{}: {}", errorMessage, e.getMessage());
            return new MessageResponse<>(null, false, errorMessage, e.getMessage());
        }
    }

    private void showNotification(String title, String message) {
        // Asegurarse de que se ejecute en el hilo de la interfaz gráfica de JavaFX
        Platform.runLater(() -> {
            ViewManager.getInstance().createNotification(title, message);
        });
    }

    private String getDefaultDownloadPath() {
        return System.getProperty("user.home") + File.separator + "Downloads";
    }
}
