package edu.gcc.future_millionaires;
//added the package so I could access the course from schedule

import java.util.ArrayList;

public class Course {

    // private class variables
    private int courseID;
    private String department;
    private int courseCode;
    private String professor;
    private String[] days; //is this better as an enumerator? and meeting type
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
        if (enrolledStudents.size() <= maxCapacity){
            enrolledStudents.add(studentID);
        } else{
            //notify user that capacity has been reached
        }
    }

    public void removeStudent(int studentID) {
        enrolledStudents.remove(studentID);
    }

    public int getCredits(){
        return credits;
    }
    public int getCourseID(){
        return courseID;
    }
    public String[] getDays(){
        return days;
    }
    public String[] getMeetingTime(){
        return meetingTime;
    }
}
