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

    // Required by Jackson for deserialization
    public Schedule() {
        this.schedule = new ArrayList<>();
        this.credits = 0;
        this.userMessage = "";
    }

    public Schedule(int studentID, String semester) {
        this.studentID = studentID;
        this.semester = semester;
        this.schedule = new ArrayList<>();
        this.credits = 0;
        this.userMessage = "";
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
            if (course.getSubject().equals(newCourse.getSubject())
                    && course.getNumber() == newCourse.getNumber()
                    && course.getSection().equals(newCourse.getSection())) {
                userMessage = "This course has already been added.";
                return false;
            }
        }

        for (Course existing : schedule) {
            if (timesOverlap(existing, newCourse)) {
                userMessage = "Adding this course causes schedule overlap. Remove the conflicting course to add this one.";
                return false;
            }
        }

        for (Course existing : schedule) {
            if (existing.getSubject().equals(newCourse.getSubject()) && existing.getNumber() == newCourse.getNumber()) {
                userMessage = "Alternate section of this course is already added";
                return false;
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
            if (course.getSubject().equals(subject)
                    && course.getNumber() == number
                    && course.getSection().equals(section)) {
                credits -= course.getCredits();
                schedule.remove(i);
                userMessage = "Course has been successfully removed!";
                return true;
            }
        }
        userMessage = "Cannot remove a course that does not exist in schedule.";
        return false;
    }
    /**
    * @param newCourse is the course trying to be added that conflicts with the current schedule
    * @param allCourses is the list of the current classes on the schedule
    * @return a list of schedule suggestions without conflicts
    */
    public List<Schedule> suggestAlternatives(Course newCourse, List<Course> allCourses) {
        return null;
    }

    private void getCombinations(
        List<List<Course>> sectionChoices,
        List<Course> fixed,
        int depth,
        List<Course> current,
        List<Schedule> results) {

            if (depth == sectionChoices.size()) {
                List<Course> candidate = new ArrayList<>(fixed);
                candidate.addAll(current);

                if (isConflictFree(candidate)) {
                    Schedule suggested = new Schedule(studentID, semester);
                    suggested.setSchedule(new ArrayList<>(candidate));
                    suggested.setCredits(candidate.stream().mapToInt(Course::getCredits).sum());
                    results.add(suggested);
                }
                return;
            }

            for (Course section : sectionChoices.get(depth)) {
                current.add(section);
                getCombinations(sectionChoices, fixed, depth + 1, current, results);
                current.remove(current.size() - 1);
            }
    }

    private boolean isConflictFree(List<Course> courses) {
        for (int i = 0; i < courses.size(); i++) {
            for (int j = i + 1; j < courses.size(); j++) {
                if (timesOverlap(courses.get(i), courses.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean timesOverlap(Course a, Course b) {
        if (a.getTimes() == null || b.getTimes() == null) return false;
        for (TimeSlot slotA : a.getTimes()) {
            for (TimeSlot slotB : b.getTimes()) {
                if (slotA.getDay().equals(slotB.getDay())) {
                    LocalTime start1 = slotA.getStart_time();
                    LocalTime end1   = slotA.getEnd_time();
                    LocalTime start2 = slotB.getStart_time();
                    LocalTime end2   = slotB.getEnd_time();
                    if (start1.isBefore(end2) && start2.isBefore(end1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public String getUserMessage() { return userMessage; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public void clearSchedule() {
        schedule.clear();
        credits = 0;
    }

    public List<Course> getSchedule() { return schedule; }
    public void setSchedule(List<Course> schedule) { this.schedule = schedule; }

    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}