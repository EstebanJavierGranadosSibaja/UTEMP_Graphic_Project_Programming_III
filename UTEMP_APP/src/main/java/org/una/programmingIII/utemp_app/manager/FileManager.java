package org.una.programmingIII.utemp_app.manager;

import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class FileManager {
    private static FileManager instance;
    private static final int CHUNK_SIZE = 512 * 1024; // 512 KB

    private FileManager() {
    }

    public static synchronized FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    public void uploadFile(String filePath, String storagePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("El archivo no existe o no es un archivo válido.");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            int chunkIndex = 0;

            // Cargar y enviar cada fragmento
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Crear metadatos para el fragmento
                FileMetadatumDTO metadata = FileMetadatumDTO.builder()
                        .id((long) chunkIndex) // Puedes usar un ID diferente si es necesario
                        .fileName(file.getName())
                        .fileSize(file.length())
                        .fileType(getFileExtension(file))
                        .storagePath(storagePath)
                        .createdAt(LocalDateTime.now())
                        .lastUpdate(LocalDateTime.now())
                        .fileChunk(buffer) // Cargar el fragmento
                        .chunkIndex(chunkIndex)
                        .totalChunks((int) Math.ceil((double) file.length() / CHUNK_SIZE))
                        .build();

                // Guardar el fragmento
                addFileMetadata(metadata);

                chunkIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores adecuado
        }
    }

    public void addFileMetadata(FileMetadatumDTO metadata) {
        // Aquí puedes manejar la lógica para guardar metadatos y almacenar el archivo
        saveFile(metadata);
    }

    private void saveFile(FileMetadatumDTO metadata) {
        try {
            // Define la ruta de almacenamiento
            String storagePath = (metadata.getStoragePath() != null && !metadata.getStoragePath().isEmpty())
                    ? metadata.getStoragePath() : getDefaultDownloadPath();

            // Asegúrate de que el directorio de almacenamiento existe
            Files.createDirectories(Paths.get(storagePath));

            // Guarda el fragmento del archivo
            File outputFile = Paths.get(storagePath, metadata.getFileName()).toFile();
            try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
                raf.seek((long) metadata.getChunkIndex() * CHUNK_SIZE);
                raf.write(metadata.getFileChunk(), 0, Math.min(metadata.getFileChunk().length, metadata.getFileSize().intValue()));
                raf.setLength((long) metadata.getTotalChunks() * CHUNK_SIZE); // Ajusta el tamaño del archivo
            }

            // Actualiza metadatos si es necesario
            updateMetadata(metadata, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores adecuado
        }
    }

    private String getDefaultDownloadPath() {
        return System.getProperty("user.home") + File.separator + "Downloads";
    }

    private void updateMetadata(FileMetadatumDTO metadata, File file) {
        metadata.setFileSize(file.length());
        metadata.setLastUpdate(LocalDateTime.now());
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
    }
}
