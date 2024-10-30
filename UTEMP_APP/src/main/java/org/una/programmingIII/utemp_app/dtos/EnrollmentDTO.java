package org.una.programmingIII.utemp_app.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.una.programmingIII.utemp_app.dtos.enums.EnrollmentState;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {

    private Long id;

    @NotNull(message = "Course must not be null")
    @Builder.Default
    private CourseDTO course = new CourseDTO();

    @NotNull(message = "Student must not be null")
    @Builder.Default
    private UserDTO student = new UserDTO();

    @NotNull(message = "State must not be null")
    private EnrollmentState state;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdate;
}
