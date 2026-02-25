package edu.gcc.future_millionaires;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Course {

    // private class variables
    private int courseID;
    private String department;
    private int courseCode;
    private String professor;
    private String[] days;
    private String semester;
    private int credits;
    private List<LocalTime[]> meetingTime;
    private int maxCapacity;
    private ArrayList<Integer> enrolledStudents;


    // class methods

    // Constructor
    public Course() {
        enrolledStudents = new ArrayList<>();
    }
    // getters
    public int getCourseID() {
        return courseID;
    }
    public String getDepartment() {
        return department;
    }
    public int getCourseCode() {
        return courseCode;
    }
    public String getProfessor() {
        return professor;
    }
    public String[] getDays() {
        return days;
    }
    public String getSemester() {
        return semester;
    }
    public int getCredits() {
        return credits;
    }
    public List<LocalTime[]>  getMeetingTime() {
        return meetingTime;
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }
    public ArrayList<Integer> getEnrolledStudents() {
        return enrolledStudents;
    }

    // setters
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setCourseCode(int courseCode) {
        this.courseCode = courseCode;
    }
    public void setProfessor(String professor) {
        this.professor = professor;
    }
    public void setDays(String[] days) {
        this.days = days;
    }
    public void setSemester(String semester) {
        this.semester = semester;
    }
    public void setCredits(int credits) {
        this.credits = credits;
    }
    public void setMeetingTime(List<LocalTime[]> meetingTime) {
        this.meetingTime = meetingTime;
    }
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    public void setEnrolledStudents(ArrayList<Integer> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    // other methods
    public void deleteCourse() {
        enrolledStudents.clear(); // clear the list of students
    }

    public void archiveCourse() {

    }

    public void editCourse(String professor, String[] days, List<LocalTime[]> meetingTime, int maxCapacity) {
        setProfessor(professor);
        setDays(days);
        setMeetingTime(meetingTime);
        setMaxCapacity(maxCapacity);
    }

    public void addStudent(int studentID) {
        if (!enrolledStudents.contains(studentID) && enrolledStudents.size() < maxCapacity) {
            enrolledStudents.add(studentID);
        }
    }

    public void removeStudent(int studentID) {
        enrolledStudents.remove(studentID);
    }
}
