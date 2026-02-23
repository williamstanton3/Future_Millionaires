package edu.gcc.future_millionaires;


public class Schedule {

    // private class variables
    private int studentID;
    private String semester;
    private String[] schedule; //We should probably do a list of course objects instead, right?
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
//        if course is null BUT FIGURE OUT BEST WAY TO SEARCH BY ID
//        inform user
//        do not add
//
//        for each existing course in schedule
//        if times conflict
//        inform user
//        do not add
//
//        add course object to list
//        increase totalCredits
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

    //get scehdule method too?
}
