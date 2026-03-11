package edu.gcc.future_millionaires;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

public class Schedule {
    private int studentID;
    private String semester;
    private List<Course> schedule;
    private int credits;
    private String userMessage;

    public Schedule(int studentID, String semester) {
        this.studentID = studentID;
        this.semester = semester;
        this.schedule = new ArrayList<>();
        this.credits = 0;
        userMessage = "";
    }

    public boolean addCourse(Course newCourse) {
        if (newCourse == null) {
            userMessage = "No course was provided.";
            return false;
        }

        if (!newCourse.getSemester().equals(semester)) {
            userMessage = "This course's semester does not match selected semester.";
            return false;
        }

        for (Course course : schedule) {
            if (course.getSubject().equals(newCourse.getSubject()) && course.getNumber() == newCourse.getNumber() && course.getSection().equals(newCourse.getSection())) {
                userMessage = "This course has already been added.";
                return false;
            }
        }

        for (Course existing : schedule) {
            for (TimeSlot existingTime : existing.getTimes()) {
                for (TimeSlot newTime : newCourse.getTimes()) {
                    if (existingTime.getDay().equals(newTime.getDay())) {
                        LocalTime start1 = existingTime.getStart_time();
                        LocalTime end1 = existingTime.getEnd_time();
                        LocalTime start2 = newTime.getStart_time();
                        LocalTime end2 = newTime.getEnd_time();

                        boolean overlap = start1.isBefore(end2) && start2.isBefore(end1);

                        if (overlap) {
                            userMessage = "Adding this course causes schedule overlap. Remove the conflicting course to add this one.";
                            return false;
                        }
                    }
                }
            }
        }

        schedule.add(newCourse);
        credits += newCourse.getCredits();
        userMessage = "Course has been successfully added!";
        return true;
    }

    public boolean removeCourse(String subject, int number, String section) {
        for (int i = 0; i < schedule.size(); i++) {
            Course course = schedule.get(i);

            if (course.getSubject().equals(subject) && course.getNumber() == number && course.getSection().equals(section)) {
                credits -= course.getCredits();
                schedule.remove(i);
                userMessage = "Course has been successfully removed!";
                return true;
            }
        }
        userMessage = "Cannot remove a course that does not exits in schedule.";
        return false;
    }

    public String getUserMessage(){
        return userMessage;
    }

    public int getCredits() {
        return credits;
    }

    public void clearSchedule() {
        schedule.clear();
        credits = 0;
    }

    public List<Course> getSchedule() {
        return schedule;
    }
}
