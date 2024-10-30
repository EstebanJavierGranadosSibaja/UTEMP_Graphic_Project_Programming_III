package org.una.programmingIII.utemp_app.manager;

import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;

public class FileUploader {
    private static final int CHUNK_SIZE = 512 * 1024; // 512 KB

    public void uploadFile(String filePath, String storagePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("El archivo no existe o no es un archivo válido.");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            int chunkIndex = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                FileMetadatumDTO metadata = createFileMetadata(file, buffer, bytesRead, chunkIndex, storagePath);
                addFileMetadata(metadata);
                chunkIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores adecuado
        }
    }

    private FileMetadatumDTO createFileMetadata(File file, byte[] buffer, int bytesRead, int chunkIndex, String storagePath) {
        return FileMetadatumDTO.builder()
                .id((long) chunkIndex)
                .fileName(file.getName())
                .fileSize(file.length())
                .fileType(getFileExtension(file))
                .storagePath(storagePath)
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .fileChunk(buffer)
                .chunkIndex(chunkIndex)
                .totalChunks((int) Math.ceil((double) file.length() / CHUNK_SIZE))
                .build();
    }

    private void addFileMetadata(FileMetadatumDTO metadata) {
        saveFile(metadata);
    }

    private void saveFile(FileMetadatumDTO metadata) {
        try {
            String storagePath = (metadata.getStoragePath() != null && !metadata.getStoragePath().isEmpty())
                    ? metadata.getStoragePath() : getDefaultDownloadPath();

            Files.createDirectories(Paths.get(storagePath));

            File outputFile = Paths.get(storagePath, metadata.getFileName()).toFile();
            try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
                raf.seek(metadata.getChunkIndex() * CHUNK_SIZE);
                raf.write(metadata.getFileChunk(), 0, Math.min(metadata.getFileChunk().length, metadata.getFileSize().intValue()));
                raf.setLength(metadata.getTotalChunks() * CHUNK_SIZE);
            }
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
