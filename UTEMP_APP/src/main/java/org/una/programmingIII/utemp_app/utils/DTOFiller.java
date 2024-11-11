package org.una.programmingIII.utemp_app.utils;

import org.una.programmingIII.utemp_app.dtos.AssignmentDTO;
import org.una.programmingIII.utemp_app.dtos.FileMetadatumDTO;
import org.una.programmingIII.utemp_app.dtos.SubmissionDTO;
import org.una.programmingIII.utemp_app.dtos.UserDTO;
import org.una.programmingIII.utemp_app.dtos.enums.AssignmentState;
import org.una.programmingIII.utemp_app.dtos.enums.SubmissionState;
import org.una.programmingIII.utemp_app.dtos.enums.UserRole;
import org.una.programmingIII.utemp_app.dtos.enums.UserState;

import java.time.LocalDateTime;
import java.util.Arrays;

public class DTOFiller {

    public SubmissionDTO createSubmissionForFirstUser() {
        // Obtener el usuario y asignación de ejemplo desde los métodos de servicio (simulados aquí).
        UserDTO student = findUserById(1L); // Supongamos que "yo" es el usuario con ID 1
        AssignmentDTO assignment = findAssignmentById(1L); // Supongamos que la asignación con ID 1 existe

        // Crear y rellenar el SubmissionDTO
        SubmissionDTO submission = SubmissionDTO.builder()
                .id(1L) // o dejarlo como null si es auto-generado
                .assignment(assignment)
                .student(student)
                .fileName("assignment1.pdf")
                .grade(85.5)
                .comments("Good work, but improve the formatting.")
                .state(SubmissionState.SUBMITTED) // Estado de la entrega
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

        // Agregar datos de FileMetadatumDTO al submission
        FileMetadatumDTO fileMetadata = createFileMetadata(submission, student);
        submission.setFileMetadata(Arrays.asList(fileMetadata)); // Añadir el metadata al submission

        return submission;
    }

    public AssignmentDTO getAssignmentDTO() {
        return findAssignmentById(1L);
    }

    public FileMetadatumDTO createFileMetadata(SubmissionDTO submission, UserDTO student) {
        // Crear y rellenar el FileMetadatumDTO
        return FileMetadatumDTO.builder()
                .id(1L) // Dejarlo como null si es auto-generado
                .submission(submission)
                .student(student)
                .fileName("assignment1_part1.pdf")
                .fileSize(102400L) // Tamaño en bytes
                .fileType("application/pdf")
                .storagePath("/uploads/assignments/")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .fileChunk(new byte[1024]) // Ejemplo de fragmento
                .chunkIndex(0) // Primer fragmento
                .totalChunks(5) // Número total de fragmentos
                .build();
    }

    public UserDTO findUserById(Long id) {
        // Simular la recuperación de datos desde un servicio o base de datos
        return UserDTO.builder()
                .id(id)
                .name("yo")
                .email("yo@example.com")
                .state(UserState.ACTIVE)
                .role(UserRole.ADMIN)
//                .permissions()
                .build();
    }

    private AssignmentDTO findAssignmentById(Long id) {
        // Simular la recuperación de datos desde un servicio o base de datos
        return AssignmentDTO.builder()
                .id(id)
                .title("Assignment 1")
                .description("Introduction assignment")
                .state(AssignmentState.PENDING)
//                .deadline(Instant.from(LocalDateTime.now().plusDays(7)))
                .build();
    }
}
