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

        try {
            byte[] buffer = new byte[CHUNK_SIZE.intValue()];
            int bytesRead;
            long totalBytesSent = 0;
            int chunkIndex = 0;

            // Calcular el total de fragmentos de manera precisa
            int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);
            fileMetadatumDTO.setFileSize(fileSize);  // Establecer el tamaño total del archivo
            fileMetadatumDTO.setTotalChunks(totalChunks); // deberia ser fijo

            while ((bytesRead = fileStream.read(buffer)) != -1) {
                byte[] chunkData = Arrays.copyOfRange(buffer, 0, bytesRead);

                // Establecer los parámetros del fragmento
                fileMetadatumDTO.setFileChunk(chunkData); // Establecer el fragmento de archivo
                fileMetadatumDTO.setChunkIndex(chunkIndex); // Establecer el índice del fragmento

                // Mostrar progreso en consola
                totalBytesSent += bytesRead;
                double progress = (double) totalBytesSent / fileSize * 100;
                System.out.printf("Subiendo archivo: %.2f%% completado%n", progress);

                // Ejecutar la solicitud para subir el fragmento
                MessageResponse<Void> response = executeVoidRequest(endpoint, HttpMethod.POST, fileMetadatumDTO);
                if (!response.isSuccess()) {
                    System.err.println("Error al subir el fragmento " + chunkIndex);
                    return; // Si alguna parte del archivo falla, retorna el error
                }
                chunkIndex++; // Incrementar el índice del fragmento
            }

            // Confirmación de que el archivo se ha subido completamente
            System.out.println("Archivo subido con éxito.");

            Platform.runLater(() -> {
                ViewManager.getInstance().createNotification("Archivo subido", "Archivo subido con éxito.");
            });

        } catch (IOException e) {
            logger.error("Error al subir el archivo en fragmentos: {}", e.getMessage(), e);
        }
    }

    public MessageResponse<Void> downloadFile(Long id) {
        String endpoint = ENTITY_ENDPOINT + "/download/" + id;

        // Inicia la descarga en un hilo separado
        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            try {
                // Crear la conexión al endpoint
                connection = createConnection(endpoint, HttpMethod.GET, null);

                // Validar el estado de la respuesta
                int statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    // Obtener el nombre del archivo desde los encabezados
                    String contentDisposition = connection.getHeaderField("Content-Disposition");
                    String fileName = contentDisposition != null && contentDisposition.contains("filename=")
                            ? contentDisposition.split("filename=")[1].replace("\"", "")
                            : "archivo_descargado";

                    // Ruta donde se guardará el archivo en la carpeta Descargas del usuario
//                    File downloadsDir = FileSystemView.getFileSystemView().getDefaultDirectory();

                    String downloadDirPath = System.getProperty("user.home") + File.separator + "Downloads";
                    String downloadPath = downloadDirPath + File.separator + fileName;

                    // Comprobar si el archivo ya existe
                    File downloadFile = new File(downloadPath);
                    if (downloadFile.exists()) {
                        // Si el archivo existe, cambiar el nombre añadiendo un sufijo numérico
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.')); // Nombre sin la extensión
                        String extension = fileName.substring(fileName.lastIndexOf('.')); // Extensión del archivo
                        int counter = 1;

                        // Generar un nuevo nombre con sufijo si el archivo ya existe
                        while (downloadFile.exists()) {
                            String newFileName = baseName + "_" + counter + extension;
                            fileName = newFileName;
                            downloadPath = downloadDirPath + File.separator + newFileName;
                            downloadFile = new File(downloadPath);

                            counter++;
                        }
                    }

                    // Descargar el archivo
                    try (InputStream inputStream = connection.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(downloadPath)) {

                        byte[] buffer = new byte[512 * 1024]; // Tamaño del fragmento: 512 KB
                        int bytesRead;
                        long totalBytesRead = 0;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            double progress = (double) totalBytesRead / connection.getContentLengthLong() * 100;
                            System.out.printf("Descarga en progreso: %.2f%% completada%n", progress);
                        }

                        Platform.runLater(() -> {
                            ViewManager.getInstance().createNotification("Descargado", "Archivo descargado con éxito: ");
                        });
                    }
                } else {
                    logger.error("Error al descargar el archivo. Código HTTP: {}", statusCode);
                }
            } catch (IOException e) {
                logger.error("Error al descargar el archivo: {}", e.getMessage(), e);
            } finally {
                if (connection != null) {
                    disconnectConnection(connection);
                }
            }
        });

        // Responder inmediatamente que la descarga ha iniciado
        return new MessageResponse<>(null, true, "Descarga en curso, revisa la carpeta de Descargas de tu sistema", null);
    }


    public String getDownloadPath(String fileName) {
        // Obtener la ruta de la carpeta de descargas de Windows
        String downloadDirPath = System.getProperty("user.home") + File.separator + "Downloads";

        // Crear la ruta completa al archivo
        String downloadPath = downloadDirPath + File.separator + fileName;

        // Comprobar si el archivo ya existe
        File downloadFile = new File(downloadPath);
        if (downloadFile.exists()) {
            // Si el archivo existe, cambiar el nombre añadiendo un sufijo numérico
            String baseName = fileName.substring(0, fileName.lastIndexOf('.')); // Nombre sin la extensión
            String extension = fileName.substring(fileName.lastIndexOf('.')); // Extensión del archivo
            int counter = 1;

            // Generar un nuevo nombre con sufijo si el archivo ya existe
            while (downloadFile.exists()) {
                String newFileName = baseName + "_" + counter + extension;
                downloadPath = downloadDirPath + File.separator + newFileName;
                downloadFile = new File(downloadPath);
                counter++;
            }
        }

        return downloadPath; // Retorna la ruta con el nombre correcto
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