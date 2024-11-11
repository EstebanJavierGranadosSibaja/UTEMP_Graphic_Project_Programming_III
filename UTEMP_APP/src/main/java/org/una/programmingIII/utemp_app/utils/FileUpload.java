//package org.una.programmingIII.utemp_app.utils;
//
//import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
//import org.una.programmingIII.utemp_app.responses.MessageResponse;
//import org.una.programmingIII.utemp_app.services.models.FileAPIService;
//
//import java.io.*;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//
//public class FileUpload {
//
//    private static final int CHUNK_SIZE = 512 * 1024; // 512 KB
//
//    public static void uploadFileInChunks(String filePath, FileAPIService fileAPIService, FileMetadatumDTO fileMetadata) throws IOException {
//        // Obtener el archivo a partir de la ruta proporcionada
//        File file = new File(filePath);
//        if (!file.exists()) {
//            throw new FileNotFoundException("El archivo no existe: " + filePath);
//        }
//
//        long fileSize = file.length();
//        String fileName = file.getName();
//        String fileType = getFileExtension(file);
//
//        // Asegurarse de que el FileMetadatumDTO contiene la información necesaria
//        fileMetadata.setFileName(fileName);
//        fileMetadata.setFileSize(fileSize);
//        fileMetadata.setFileType(fileType);
//        fileMetadata.setCreatedAt(LocalDateTime.now());
//        fileMetadata.setLastUpdate(LocalDateTime.now());
//        fileMetadata.setTotalChunks((int) Math.ceil((double) fileSize / CHUNK_SIZE)); // Calcular el número total de fragmentos
//
//        // Leer el archivo en fragmentos y subir cada fragmento
//        try (InputStream fileInputStream = new FileInputStream(file)) {
//            byte[] buffer = new byte[CHUNK_SIZE];
//            long totalBytesRead = 0;
//            int bytesRead;
//            int chunkIndex = 0;
//
//            // Leer el archivo en fragmentos
//            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                // Crear un DTO para el fragmento actual
//                FileMetadatumDTO fileChunkDTO = new FileMetadatumDTO();
//                fileChunkDTO.setFileName(fileMetadata.getFileName());
//                fileChunkDTO.setFileSize(fileMetadata.getFileSize());
//                fileChunkDTO.setFileType(fileMetadata.getFileType());
//                fileChunkDTO.setCreatedAt(fileMetadata.getCreatedAt());
//                fileChunkDTO.setLastUpdate(fileMetadata.getLastUpdate());
//                fileChunkDTO.setTotalChunks(fileMetadata.getTotalChunks());
//                fileChunkDTO.setChunkIndex(chunkIndex);
//                fileChunkDTO.setFileChunk(Arrays.copyOf(buffer, bytesRead));
//
//                // Subir el fragmento
//                MessageResponse<Void> response = fileAPIService.uploadFileChunk(fileChunkDTO);
//
//                // Verificar si la subida del fragmento fue exitosa
//                if (response.isSuccess()) {
//                    System.out.println("Fragmento " + chunkIndex + " subido exitosamente.");
//                } else {
//                    System.err.println("Error al subir el fragmento " + chunkIndex + ": " + response.getErrorMessage());
//                    break; // Detener la carga si hay un error
//                }
//
//                // Actualizar el progreso
//                totalBytesRead += bytesRead;
//                double progress = (totalBytesRead * 100.0) / fileSize; // Calcular porcentaje de progreso
//                System.out.printf("Progreso: %.2f%%\n", progress);
//
//                chunkIndex++;
//            }
//        }
//    }
//
//    // Método para obtener la extensión del archivo (por ejemplo, .zip, .jpg, .pdf)
//    private static String getFileExtension(File file) {
//        String fileName = file.getName();
//        int dotIndex = fileName.lastIndexOf('.');
//        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
//            return fileName.substring(dotIndex + 1); // Retorna la extensión
//        }
//        return "";
//    }
//}
//
///*
//
//FileAPIService fileAPIService = new FileAPIService();  // Suponiendo que este servicio esté configurado correctamente
//
//// Crear el DTO con los metadatos básicos del archivo
//FileMetadatumDTO fileMetadata = FileMetadatumDTO.builder()
//    .submission(new SubmissionDTO())  // Configurar la información relacionada con la sumisión
//    .student(new UserDTO())  // Configurar el usuario (si corresponde)
//    .build();
//
//// Ruta del archivo a subir
//String filePath = "path_to_your_file";
//
//try {
//    FileUpload.uploadFileInChunks(filePath, fileAPIService, fileMetadata);
//} catch (IOException e) {
//    e.printStackTrace();
//}
//
// */