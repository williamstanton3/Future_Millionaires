package edu.gcc.future_millionaires;

import java.util.ArrayList;
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
        this.schedule = new ArrayList<>();
        this.credits = 0;
    }

    // Methods
    public void addCourse(String courseID) {
        if(courseID.isEmpty()){
            //see if there even is a string, if not error
            return;
        }

        int ID;
        try {
            ID = Integer.parseInt(courseID);
        } catch (NumberFormatException e) {
            //inform user of an an error, not an integer
            return;
        }

        //do i create new course here? don't want to if there is a conflict...
        Course newCourse = new Course(); //add info to object like ID and stuff when constructor is done

        for(Course course : schedule)
        {
            for(String day : course.getDays()){
                for(String time : course.getMeetingTime()) {
                    if(newCourse.getMeetingTime().equals(time) && newCourse.getDays().equals(day)){
                        newCourse = null; //I'm thinking like C, I don't need to do this to free space bc it dies after the method, right?
                        //inform user and do not add
                        return;
                    }
                }
            }
        }

        schedule.add(newCourse);
        credits += newCourse.getCredits();
    }

    public void removeCourse(String courseID) {
        for(Course course : schedule)
        {
            if(course.getCourseID() == Integer.parseInt(courseID)){
                //remove & subtract credits
                return;
            }
        }

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
