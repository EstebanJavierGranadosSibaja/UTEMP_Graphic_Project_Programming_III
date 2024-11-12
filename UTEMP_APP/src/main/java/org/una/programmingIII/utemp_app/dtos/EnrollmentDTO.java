package org.una.programmingIII.utemp_app.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference("course-enrollments")  // Unique name for course reference
    private CourseDTO course = new CourseDTO();

    @NotNull(message = "Student must not be null")
    @Builder.Default
    @JsonBackReference("user-enrollments")  // Unique name for student reference
    private UserDTO student = new UserDTO();

    @NotNull(message = "State must not be null")
    private EnrollmentState state;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdate;
}

