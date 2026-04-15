package edu.gcc.future_millionaires;

import java.util.HashMap;
import java.util.Map;

public class Student {

    private final int studentID;
    private Map<String, Schedule> schedules;       // live, in-progress
    private Map<String, Schedule> savedSchedules;  // finalized, archived
    private String activeSemester;

    public Student(int studentID) {
        this.studentID = studentID;
        this.schedules = new HashMap<>();
        this.savedSchedules = new HashMap<>();
        this.activeSemester = null;
    }

    public void setActiveSemester(String semester) {
        this.activeSemester = semester;
        schedules.putIfAbsent(semester, new Schedule(studentID, semester));
    }

    /**
     * Used by PersistenceManager on startup to restore activeSemester
     * without creating a new blank Schedule (the map is already populated).
     */
    public void restoreActiveSemester(String semester) {
        this.activeSemester = semester;
    }

    public String getActiveSemester() {
        return activeSemester;
    }

    public Schedule getActiveSchedule() {
        if (activeSemester == null) return null;
        return schedules.get(activeSemester);
    }

    /**
     * Moves the active schedule from the live map into savedSchedules,
     * then clears it from the live map and resets activeSemester.
     */
    public boolean finalizeSchedule() {
        if (activeSemester == null) return false;
        Schedule active = schedules.get(activeSemester);
        if (active == null || active.getSchedule().isEmpty()) return false;

        savedSchedules.put(activeSemester, active);
        schedules.remove(activeSemester);
        activeSemester = null;
        return true;
    }

    // Exposed as mutable so PersistenceManager can populate them on load
    public Map<String, Schedule> getSchedules() { return schedules; }
    public Map<String, Schedule> getSavedSchedules() { return savedSchedules; }

    public void addCourse(String courseID) {
        // stub for transcript
    }

    public int getStudentID() { return studentID; }
}