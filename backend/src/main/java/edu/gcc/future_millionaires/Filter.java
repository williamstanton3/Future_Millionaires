package edu.gcc.future_millionaires;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    // filter criteria
    private String subject;
    private String professor;
    private String[] days;
    private String semester;
    private int credits;

    // Constructor
    public Filter() {
        subject = null;
        professor = null;
        days = null;
        semester = null;
        credits = 0;
    }

    // setters

    public void setSubject(String subject) {
        this.subject = subject;
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

    // Main filtering method

    public List<Course> apply(List<Course> courses) {
        List<Course> results = new ArrayList<>();
        for (Course c : courses) {
            if (matches(c)) results.add(c);
        }
        return results;
    }


    // Check if a course matches

    private boolean matches(Course course) {

        // subject filter
        if (subject != null &&
                !subject.equals(course.getSubject())) {
            return false;
        }

        // professor filter
        if (professor != null) {

            boolean found = false;

            for (String facultyMember : course.getFaculty()) {
                if (facultyMember.equals(professor)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        // semester filter
        if (semester != null &&
                !semester.equals(course.getSemester())) {
            return false;
        }

        // credits filter
        if (credits > 0 &&
                credits != course.getCredits()) {
            return false;
        }

        // days filter
        if (days != null) {

            for (TimeSlot slot : course.getTimes()) {

                boolean match = false;

                for (String day : days) {
                    if (slot.getDay().equals(day)) {
                        match = true;
                        break;
                    }
                }

                if (!match) {
                    return false;
                }
            }
        }

        return true;
    }
}