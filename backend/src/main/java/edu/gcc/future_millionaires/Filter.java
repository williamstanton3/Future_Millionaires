package edu.gcc.future_millionaires;

import java.util.ArrayList;

public class Filter {

    // private class variables
    private String department;
    private String professor;
    private String[] days;
    private String semester;
    private int credits;

    // class methods

    // Constructor
    public Filter() {
        // initialize every class variable
        this.department = null;
        this.professor = null;
        this.days = null;
        this.semester = null;
        this.credits = 0;
    }

    // setters for filtering
    public void setDepartment(String department) {
        this.department = department;
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


    // Methods
    public ArrayList<Course> returnResults(Course[] courses) {
        ArrayList<Course> results = new ArrayList<>(); // arraylist of matching courses

        for (Course myCourse : courses) {
            if (matches(myCourse)) {
                results.add(myCourse);
            }
        }

        return results;
    }

    // method to check if a course matches the given filter
    private boolean matches(Course myCourse) {
        if (department != null && !department.equals(myCourse.getDepartment())) {
            return false;
        }
        if (professor != null && !professor.equals(myCourse.getProfessor())) {
            return false;
        }
        if (semester != null && !semester.equals(myCourse.getSemester())) {
            return false;
        }
        if (credits > 0 && credits !=myCourse.getCredits()) {
            return false;
        }
        if (days != null) {
            String [] availableDays = days; // list of days that they are willing to meet
            String [] courseDays = myCourse.getDays(); // list of days that the current course meets

            for (String courseDay: courseDays) { // loop through each day that the course meets
                boolean match = false;

                for (String availableDay: availableDays) { // loop through each available day
                    if (courseDay.equals(availableDay)) {
                        match = true; // once we find a match, we know that the current day that the course meets is one that the user is available on
                        break; // move on to the next course day
                    }
                }
                if (!match) {
                    return false; // course meets on a day that the user hasn't selected, return false
                }
            }
        }

        return true;
    }
}