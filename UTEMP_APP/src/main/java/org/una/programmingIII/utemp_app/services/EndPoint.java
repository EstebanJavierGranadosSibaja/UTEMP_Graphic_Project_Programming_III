package org.una.programmingIII.utemp_app.services;

public class EndPoint {
    private static final String API_ENDPOINT = "/api";
    private static final String USER_ENDPOINT = "/users";
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String COURSE_ENDPOINT = "/courses";
    private static final String DEPARTMENT_ENDPOINT = "/departments";
    private static final String ENROLLMENT_ENDPOINT = "/enrollments";
    private static final String FACULTY_ENDPOINT = "/faculties";
    private static final String FILE_ENDPOINT = "/files";
    private static final String GRADE_ENDPOINT = "/grades";
    private static final String NOTIFICATION_ENDPOINT = "/notifications";
    private static final String SUBMISSION_ENDPOINT = "/submissions";
    private static final String UNIVERSITY_ENDPOINT = "/universities";
    private static final String ASSIGNMENT_ENDPOINT = "/assignments";

    public static String getUser() { return API_ENDPOINT + USER_ENDPOINT; }
    public static String getAuth() { return API_ENDPOINT + AUTH_ENDPOINT; }
    public static String getCourse() { return API_ENDPOINT + COURSE_ENDPOINT; }
    public static String getDepartment() { return API_ENDPOINT + DEPARTMENT_ENDPOINT; }
    public static String getEnrollment() { return API_ENDPOINT + ENROLLMENT_ENDPOINT; }
    public static String getFaculty() { return API_ENDPOINT + FACULTY_ENDPOINT; }
    public static String getFile() { return API_ENDPOINT + FILE_ENDPOINT; }
    public static String getGrade() { return API_ENDPOINT + GRADE_ENDPOINT; }
    public static String getNotification() { return API_ENDPOINT + NOTIFICATION_ENDPOINT; }
    public static String getSubmission() { return API_ENDPOINT + SUBMISSION_ENDPOINT; }
    public static String getUniversity() { return API_ENDPOINT + UNIVERSITY_ENDPOINT; }
    public static String getAssignment() { return API_ENDPOINT + ASSIGNMENT_ENDPOINT; }
}