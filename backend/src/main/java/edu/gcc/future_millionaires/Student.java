package edu.gcc.future_millionaires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Student {

    private final int studentID;
    private Map<String, Schedule> schedules;
    private String activeSemester;

    // Constructor
    public Student(int studentID) {
        this.studentID = studentID;
        this.schedules = new HashMap<>();
        this.activeSemester = null;
    }

    // Set the active semester — creates a new Schedule for it if one doesn't exist yet
    public void setActiveSemester(String semester) {
        this.activeSemester = semester;
        schedules.putIfAbsent(semester, new Schedule(studentID, semester));
    }

    public String getActiveSemester() {
        return activeSemester;
    }

    // Returns the schedule for the active semester, or null if none is set
    public Schedule getActiveSchedule() {
        if (activeSemester == null) return null;
        return schedules.get(activeSemester);
    }

    // Add a course to the transcript
    public void addCourse(String courseID) {

    }

    // Change major


    public int getStudentID() {
        return studentID;
    }
}