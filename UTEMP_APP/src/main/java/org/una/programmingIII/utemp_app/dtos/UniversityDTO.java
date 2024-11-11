package org.una.programmingIII.utemp_app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
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
public class UniversityDTO {

    private Long id;

    @NotBlank(message = "University name must not be blank")
    @Size(max = 100, message = "University name must be at most 100 characters long")
    private String name;

    @Size(max = 200, message = "Location must be at most 200 characters long")
    private String location;

    @Builder.Default
    private List<FacultyDTO> faculties = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdate;
}
