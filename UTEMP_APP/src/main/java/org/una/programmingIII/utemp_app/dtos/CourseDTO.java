package org.una.programmingIII.utemp_app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.una.programmingIII.utemp_app.dtos.enums.CourseState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Long id;

    @NotNull(message = "Course name must not be null")
    @Size(max = 50, message = "Course name must not exceed 50 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "State must not be null")
    private CourseState state;

    // Timestamps
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime lastUpdate;

    // Relationships
    @NotNull(message = "Teacher must not be null")
    @Builder.Default
    private UserDTO teacher = new UserDTO();

    private Long userTeacherUniqueID;

    @NotNull(message = "Department must not be null")
    @Builder.Default
    private DepartmentDTO department = new DepartmentDTO();

    private Long departmentUniqueID;
    private String departmentUniqueName;

    // Collections
    @Builder.Default
    @JsonIgnore
    private List<AssignmentDTO> assignment = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    private List<EnrollmentDTO> enrollments = new ArrayList<>();
}

