package org.una.programmingIII.utemp_app.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FileMetadatumDTO {

    private Long id;// predefinodo

    @Builder.Default
    private SubmissionDTO submission = new SubmissionDTO();//predefinido

    @Builder.Default
    private UserDTO student = new UserDTO(); // predefinido

    @NotNull(message = "File name must not be null")
    @Size(max = 255, message = "File name must be at most 255 characters long")
    private String fileName;// se define durante la subida

    @NotNull(message = "File size must not be null")
    private Long fileSize;// se define durante la subida

    @Size(max = 100, message = "File type must be at most 100 characters long")
    private String fileType;// se define durante la subida

    private LocalDateTime createdAt;// se define durante la subida

    private LocalDateTime lastUpdate;// se define durante la subida

    // Nuevos campos para manejo de fragmentos
    private byte[] fileChunk; // Fragmento del archivo// se define durante la subida
    private int chunkIndex; // Índice del fragmento// se define durante la subida
    private int totalChunks; // Número total de fragmentos// se define durante la subida

    @Size(max = 500, message = "Storage path must be at most 500 characters long")
    private String storagePath;// se define durante la subida
}
