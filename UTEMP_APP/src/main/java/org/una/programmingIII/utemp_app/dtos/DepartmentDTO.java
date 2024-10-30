package org.una.programmingIII.utemp_app.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {

    private Long id;

    @NotNull(message = "Department name must not be null")
    @Size(max = 50, message = "Department name must not exceed 50 characters")
    private String name;

    @NotNull(message = "Faculty must not be null")
    @Builder.Default
    private FacultyDTO faculty = new FacultyDTO();

    @Builder.Default
    private List<CourseDTO> courses = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdate;
}