package org.una.programmingIII.utemp_app.utils.view;

import lombok.Getter;
import lombok.Setter;
import org.una.programmingIII.utemp_app.dtos.*;

@Setter
@Getter
public class AppContext {
    private static AppContext instance;

    private UserDTO userDTO;
    private UniversityDTO universityDTO;
    private DepartmentDTO departmentDTO;
    private FacultyDTO facultyDTO;
    private CourseDTO courseDTO;

    private PageDTO<FacultyDTO> pageFaculty;
    private PageDTO<DepartmentDTO> pageDepartment;

    private AppContext() {
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }
}
