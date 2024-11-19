package org.una.programmingIII.utemp_app.dtos.enums;

public enum UserPermission {

    // TOTAL PERMISSIONS
    ALL_PERMISSIONS,

    // MANAGE PERMISSIONS
    MANAGE_USERS,
    MANAGE_UNIVERSITIES,
    MANAGE_FACULTIES,
    MANAGE_DEPARTMENTS,
    MANAGE_COURSES,
    MANAGE_ASSIGNMENTS,
    MANAGE_SUBMISSIONS,
    MANAGE_ENROLLMENTS,
    MANAGE_GRADES,

    //  ----------------------
    //  |  OTHER PERMISSIONS |
    //  ----------------------

    // USER PERMISSIONS
    GET_TEACHER_COURSES,
    ADD_TEACHER_COURSES,
    REMOVE_TEACHER_COURSE,
    GET_STUDENT_ENROLLMENTS,
    ADD_STUDENT_COURSES,
    REMOVE_STUDENT_COURSES,

    // UNIVERSITY PERMISSIONS
    ADD_UNIVERSITY_FACULTIES,
    REMOVE_UNIVERSITY_FACULTIES,

    // FACULTY PERMISSIONS
    GET_UNIVERSITY_FACILITIES,
    ADD_FACULTY_DEPARTMENTS,
    REMOVE_FACULTY_DEPARTMENTS,

    // DEPARTMENT PERMISSIONS
    GET_FACULTY_DEPARTMENTS,
    ADD_DEPARTMENT_COURSES,
    REMOVE_DEPARTMENT_COURSES,

    // COURSE PERMISSIONS
    GET_DEPARTMENT_COURSES,
    ADD_COURSE_ASSIGNMENTS,
    REMOVE_COURSE_ASSIGNMENTS,

    // ASSIGNMENT PERMISSIONS
    GET_COURSE_ASSIGNMENTS,
    ADD_ASSIGNMENT_SUBMISSION,
    REMOVE_ASSIGNMENT_SUBMISSION,

    // SUBMISSION PERMISSIONS
    GET_ASSIGNMENT_SUBMISSIONS,
    ADD_SUBMISSION_FILES,
    ADD_SUBMISSION_GRADES,
    REMOVE_SUBMISSION_FILES,
    REMOVE_SUBMISSION_GRADES,
    EVALUATE_SUBMISSIONS,

    // GRADE PERMISSIONS
    GET_SUBMISSION_GRADES
}
