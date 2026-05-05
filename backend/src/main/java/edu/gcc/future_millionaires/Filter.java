package edu.gcc.future_millionaires;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Filter {

    // filter criteria
    private int number; // 422
    private String department; // COMP
    private String[] professors; // ["Graybill, Keith B."]
    // times can be a day ("M") or a day + time range ("T 15:30:00-16:45:00")
    private String[] times;
    private String semester; // 2023_Fall
    private int credits; // 3
    private String keyword; // keyword search across course fields

    // Constructor
    public Filter() {
        department = null;
        professors = null;
        times = null;
        semester = null;
        credits = -1; // represents null
        keyword = null;
    }


    // Setters
    public void setDepartment(String subject) { department = subject; }
    public void setNumber(int n) { number = n; }
    public void setProfessors(String[] professor) { professors = professor; }
    public void setTimes(String[] times) { this.times = times; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public List<Course> apply(List<Course> courses) {
        List<Course> results = new ArrayList<>();

        for (Course c : courses) {
            if (matches(c)) {
                results.add(c);
            }
        }

        return results;
    }

    private boolean matches(Course course) {

        // course code filter
        if (number > 0 &&
                number != course.getNumber())
            return false;

        // department filter
        if (department != null &&
                !department.equalsIgnoreCase(course.getSubject()))
            return false;

        // professors filter (matches if any professor appears in the JSON faculty list)
        if (professors != null) {
            boolean found = false;

            // loop through the list of professors given in the filter
            for (String prof : professors) {
                // if the list of faculty for this given course contains the prof we are looking for, return true
                if (course.getProfessors().stream()
                        .anyMatch(p -> p.getName() != null &&
                                p.getName().toLowerCase().contains(prof.toLowerCase()))) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        // semester filter
        if (semester != null && !semester.equals(course.getSemester()))
            return false;

        // credits filter
        if (credits >= 0 && credits != course.getCredits())
            return false;

        // times filter (delegated to helper function)
        if (times != null && !matchesTimes(course))
            return false;

        // keyword filter
        if (keyword != null && !matchesKeyword(course, keyword))
            return false;

        return true;
    }

    private boolean matchesTimes(Course course) {

        // if the course has no meeting times, it cannot match
        if (course.getTimes() == null || course.getTimes().isEmpty())
            return false;

        boolean anyMatch = false;

        // loop through each meeting time in the course JSON
        // JSON example:
        // {"day":"T","start_time":"15:30:00","end_time":"16:45:00"}
        for (TimeSlot slot : course.getTimes()) {

            for (String timeStr : times) {

                // split the user input into day and optional time range
                String[] parts = timeStr.split(" ");

                // day from user input (M,T,W,R,F)
                String day = parts[0];

                LocalTime start = null;
                LocalTime end = null;

                // if user provided a time range
                if (parts.length == 2) {

                    // split "15:30:00-16:45:00"
                    String[] timeParts = parts[1].split("-");

                    // convert strings into LocalTime objects
                    start = LocalTime.parse(timeParts[0]);
                    end = LocalTime.parse(timeParts[1]);
                }

                // check if day matches JSON day field
                boolean dayMatches = slot.getDay().contains(day);

                // if start is null, user only filtered by day
                boolean startMatches =
                        (start == null) || !slot.getStart_time().isBefore(start);

                // if end is null, user only filtered by day
                boolean endMatches =
                        (end == null) || !slot.getEnd_time().isAfter(end);

                // if all required conditions match
                if (dayMatches && startMatches && endMatches) {
                    anyMatch = true;
                    break;
                }
            }

            if (anyMatch) break;
        }

        return anyMatch;
    }

    private boolean matchesKeyword(Course course, String keyword) {

        String kw = keyword.toLowerCase();

        // checks subject, name, section, location, semester, faculty, and number
        if ((course.getSubject() != null && course.getSubject().toLowerCase().contains(kw))
                || (course.getName() != null && course.getName().toLowerCase().contains(kw))
                || (course.getSection() != null && course.getSection().toLowerCase().contains(kw))
                || (course.getLocation() != null && course.getLocation().toLowerCase().contains(kw))
                || (course.getSemester() != null && course.getSemester().toLowerCase().contains(kw))
                || (course.getProfessors() != null && course.getProfessors().stream().anyMatch(p -> p.getName() != null && p.getName().toLowerCase().contains(kw)))
                || Integer.toString(course.getNumber()).contains(kw)) {

            return true;
        }

        return false;
    }
}