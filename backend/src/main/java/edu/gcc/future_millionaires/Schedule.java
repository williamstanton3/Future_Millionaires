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

    public Result getLatestResult() {
        return latestResult;
    }

    public enum Result {
        SUCCESS,
        TIME_CONFLICT,
        DUPLICATE,
        INVALID_SEMESTER,
        NOT_FOUND
    }
    private Result latestResult;

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

    public Schedule(int studentID, String semester, List<Course> schedule){
        this.studentID = studentID;
        this.semester = semester;
        this.schedule = new ArrayList<>(schedule);
        this.credits = schedule.stream().mapToInt(Course::getCredits).sum();
        this.userMessage = "";
    }

    public boolean addCourse(Course newCourse) {
        if (newCourse == null) {
            userMessage = "No course was provided.";
            latestResult = Result.NOT_FOUND;
            return false;
        }

        if (!newCourse.getSemester().equals(semester)) {
            userMessage = "This course's semester does not match selected semester.";
            latestResult = Result.INVALID_SEMESTER;
            return false;
        }

        for (Course course : schedule) {
            if (course.getSubject().equals(newCourse.getSubject())
                    && course.getNumber() == newCourse.getNumber()
                    && course.getSection().equals(newCourse.getSection())) {
                userMessage = "This course has already been added.";
                latestResult = Result.DUPLICATE;
                return false;
            }
        }

        for (Course existing : schedule) {
            if (timesOverlap(existing, newCourse)) {
                userMessage = "Adding this course causes schedule overlap. Remove the conflicting course to add this one.";
                latestResult = Result.TIME_CONFLICT;
                return false;
            }
        }

        for (Course existing : schedule) {
            if (existing.getSubject().equals(newCourse.getSubject()) && existing.getNumber() == newCourse.getNumber()) {
                userMessage = "Alternate section of this course is already added";
                latestResult = Result.DUPLICATE;
                return false;
            }
        }

        schedule.add(newCourse);
        credits += newCourse.getCredits();
        userMessage = "Course has been successfully added!";
        latestResult = Result.SUCCESS;
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
    * @param allCourses is the all the classes available
    * @return a list of schedule suggestions without conflicts
    */
    public List<Schedule> suggestAlternatives(Course newCourse, List<Course> allCourses) {
        // create a list of lists where each inner list contains all the sections of all the courses that need to be repicked
        List<Course> toRePick = new ArrayList<>(schedule);
        toRePick.add(newCourse);

        List<List<Course>> sectionChoices = new ArrayList<>();
        for (Course courseToPick : toRePick) {
            List<Course> sections = new ArrayList<>();
            for (Course c : allCourses) {
                if (c.getSubject().equals(courseToPick.getSubject()) &&
                        c.getNumber() == courseToPick.getNumber() &&
                        c.getSemester().equals(semester)) {
                    sections.add(c);
                }
            }
            if (sections.isEmpty()) {
                return new ArrayList<>();
            }
            sectionChoices.add(sections);
        }

        // Generate every combination (cartesian product) and keep the valid ones
        List<Schedule> validSchedules = new ArrayList<>();
        List<Course> current = new ArrayList<>();
        getCombinations(sectionChoices, 0, current, validSchedules);

        return validSchedules;
    }

    /**
     * recurses through the list of lists containing all the possible sections and finds all the
     * schedules that could fix the conflict
     * @param sectionChoices the list of lists containing type Course with all the course sections
     * @param depth what level of the sectionsChoices list you currently on
     * @param current the current schedule
     * @param results full schedules that have no conflicts
     */
    private void getCombinations(
        List<List<Course>> sectionChoices,
        int depth,
        List<Course> current,
        List<Schedule> results) {

            if (depth == sectionChoices.size()) {
                // We found a complete, valid schedule!
                Schedule suggested = new Schedule(studentID, semester, current);
                results.add(suggested);
                return;
            }

            for (Course section : sectionChoices.get(depth)) {
                // Only go deeper if this section doesn't conflict with what we've picked so far
                if (isConflictFree(section, current)) {
                    current.add(section);
                    getCombinations(sectionChoices, depth + 1, current, results);
                    current.remove(current.size() - 1); // Standard backtracking undo
                }
            }
    }

    private boolean isConflictFree(Course candidate, List<Course> currentSelection) {
        for (Course accepted : currentSelection) {
            if (timesOverlap(candidate, accepted)) {
                return false; // Found a conflict, don't let them in!
            }
        }
        return true; // No conflicts with anyone already in the list
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