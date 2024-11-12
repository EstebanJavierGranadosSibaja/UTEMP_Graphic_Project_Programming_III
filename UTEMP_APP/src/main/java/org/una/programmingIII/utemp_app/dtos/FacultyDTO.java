package org.una.programmingIII.utemp_app.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class FacultyDTO {

    private Long id;

    @NotNull(message = "Faculty name must not be null")
    @Size(max = 50, message = "Faculty name must be at most 50 characters long")
    private String name;

    @JsonBackReference("university-faculties")  // Unique name for university reference
    @NotNull(message = "University must not be null")
    @Builder.Default
    private UniversityDTO university = new UniversityDTO();

    @JsonManagedReference("faculty-departments")  // Unique name for departments reference
    @Builder.Default
    private List<DepartmentDTO> departments = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdate;
}

