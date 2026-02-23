package edu.gcc.future_millionaires;

import java.util.List;

public class Schedule {

    // private class variables
    private int studentID;
    private String semester;
    private List<Course> schedule;
    private int credits;
    private boolean overlap;

    // class methods

    // Constructor
    public Schedule(int studentID, String semester) {
        this.studentID = studentID;
        this.semester = semester;
        //this.schedule = new ArrayList<>();
        this.credits = 0;
    }

    // Methods
    public void addCourse(String courseID) {
        if(courseID.isEmpty()){
            //see if there even is a string, if not error
        }
        try {
            Integer.parseInt(courseID);
            //save as ID then do search
        } catch (NumberFormatException e) {
            //inform user of an an error, not an integer
            return;
        }
//        if course is null BUT FIGURE OUT BEST WAY TO SEARCH BY ID
//        inform user
//        do not add
//
//        for each existing course in schedule
//        if times conflict
//        inform user
//        do not add
//
        Course newCourse = new Course(); //add info to object like ID and stuff when constructor is done
//        add course object to list
        credits += newCourse.getCredits();
    }

    public void removeCourse(String courseID) {
//        for each course in list
//        if courseID matches
//        remove it
//        subtract credits
//        return
//
//        notify user could not find course
    }

    public int getCredits() {
        return credits;
    }

    public void clearSchedule() {
        //schedule.clear(); <- did it as if it'll be a list
        credits = 0;
    }

    public List<Course> getSchedule(){
        return schedule;
    }
}
