package org.una.programmingIII.utemp_app.utils.view;

import lombok.Getter;
import lombok.Setter;
import org.una.programmingIII.utemp_app.dtos.*;

@Setter
@Getter
public class AppContext {
    private static AppContext instance;

    private UserDTO userDTO;
    private UserDTO teacherDTO;

    private UniversityDTO universityDTO;
    private FacultyDTO facultyDTO;
    private DepartmentDTO departmentDTO;
    private CourseDTO courseDTO;
    private AssignmentDTO assignmentDTO;

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
