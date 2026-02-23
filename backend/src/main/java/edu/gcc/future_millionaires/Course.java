package edu.gcc.future_millionaires.;
//added the package so I could access the course from schedule

import java.util.ArrayList;

public class Course {

    // private class variables
    private int courseID;
    private String department;
    private int courseCode;
    private String professor;
    private String[] days;
    private String semester;
    private int credits;
    private String[] meetingTime;
    private int maxCapacity;
    private ArrayList<Integer> enrolledStudents;


    // class methods

    // Constructor
    public Course() {

    }

    // Methods
    public void deleteCourse() {

    }

    public void archiveCourse() {

    }

    public void editCourse(String professor,
                           String[] days,
                           String[] meetingTime,
                           int maxCapacity) {

    }

    public void addStudent(int studentID) {

    }

    public void removeStudent(int studentID) {

    }

    public int getCredits(){
        return credits;
    }
}
