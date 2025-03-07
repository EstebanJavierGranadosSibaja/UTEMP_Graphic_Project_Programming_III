package org.una.programmingIII.utemp_app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime lastUpdate;

    @Builder.Default
    @JsonIgnore
    private List<CourseDTO> courses = new ArrayList<>();
}

