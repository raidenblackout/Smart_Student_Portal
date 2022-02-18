package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class Course implements Serializable {
    private String courseName;
    private String courseCode;
    private double courseCredit;
    private boolean status;
    private String facultyName;
    private String courseCategory;
    public Course(String courseName, String courseCode, double courseCredit, boolean status, String facultyName) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseCredit = courseCredit;
        this.status = status;
        this.facultyName = facultyName;
    }

    public Course() {

    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public void setCourseCredit(double courseCredit) {
        this.courseCredit = courseCredit;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public double getCourseCredit() {
        return courseCredit;
    }
    public boolean getStatus() {
        return status;
    }
    public String getFacultyName() {
        return facultyName;
    }

    public String getCourseCategory() {
        return courseCategory;
    }
    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }
}
