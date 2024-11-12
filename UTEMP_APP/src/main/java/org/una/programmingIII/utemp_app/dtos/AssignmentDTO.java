package org.una.programmingIII.utemp_app.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.una.programmingIII.utemp_app.dtos.enums.AssignmentState;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {

    private Long id;

    @NotNull(message = "Title must not be null")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotNull(message = "Description must not be null")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Instant deadline;

    @NotNull(message = "Course must not be null")
    @Builder.Default
    @JsonBackReference("course-assignments")
    private CourseDTO course = new CourseDTO();


    @NotNull(message = "State must not be null")
    private AssignmentState state;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdate;

    @Builder.Default
    @JsonManagedReference("assignment-submissions")
    private List<SubmissionDTO> submissions = new ArrayList<>();
}