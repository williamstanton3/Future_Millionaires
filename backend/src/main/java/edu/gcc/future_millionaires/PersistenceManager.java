package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles reading and writing student schedule data to a JSON file.
 * Call save() after any mutation so state is never lost on restart.
 *
 * File format:
 * {
 *   "schedules":      { "2024_Fall": { ... }, ... },   // live / in-progress
 *   "savedSchedules": { "2024_Spring": { ... }, ... }  // finalized / archived
 * }
 */

public class PersistenceManager {

    private final File file;
    private final ObjectMapper mapper;

    public PersistenceManager(String filePath) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // -------------------------------------------------------------------------
    // Load
    // -------------------------------------------------------------------------

    public void load(Student student, CourseList courseList) {
        if (!file.exists()) return;

        try {
            StudentData data = mapper.readValue(file, StudentData.class);

            if (data.schedules != null) {
                data.schedules.forEach((semester, scheduleData) -> {
                    Schedule schedule = new Schedule(student.getStudentID(), semester);
                    for (CourseRef ref : scheduleData.courses) {
                        Course course = courseList.findCourse(ref.subject, ref.number, ref.section, ref.semester);
                        if (course != null) schedule.addCourse(course);
                    }
                    student.getSchedules().put(semester, schedule);
                });
            }

            if (data.savedSchedules != null) {
                data.savedSchedules.forEach((semester, scheduleData) -> {
                    Schedule schedule = new Schedule(student.getStudentID(), semester);
                    for (CourseRef ref : scheduleData.courses) {
                        Course course = courseList.findCourse(ref.subject, ref.number, ref.section, ref.semester);
                        if (course != null) schedule.addCourse(course);
                    }
                    student.getSavedSchedules().put(semester, schedule);
                });
            }

            if (data.activeSemester != null) {
                student.restoreActiveSemester(data.activeSemester);
            }

        } catch (IOException e) {
            System.err.println("Warning: could not load student data from " + file.getPath());
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Save
    // -------------------------------------------------------------------------

    public void save(Student student) {
        try {
            StudentData data = new StudentData();
            data.activeSemester = student.getActiveSemester();

            student.getSchedules().forEach((semester, schedule) -> {
                ScheduleData sd = new ScheduleData();
                for (Course c : schedule.getSchedule()) {
                    sd.courses.add(new CourseRef(c.getSubject(), c.getNumber(), c.getSection(), c.getSemester()));
                }
                data.schedules.put(semester, sd);
            });

            student.getSavedSchedules().forEach((semester, schedule) -> {
                ScheduleData sd = new ScheduleData();
                for (Course c : schedule.getSchedule()) {
                    sd.courses.add(new CourseRef(c.getSubject(), c.getNumber(), c.getSection(), c.getSemester()));
                }
                data.savedSchedules.put(semester, sd);
            });

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Warning: could not save student data to " + file.getPath());
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // DTOs
    // -------------------------------------------------------------------------

    public static class StudentData {
        public String activeSemester;
        public Map<String, ScheduleData> schedules = new HashMap<>();
        public Map<String, ScheduleData> savedSchedules = new HashMap<>();
    }

    public static class ScheduleData {
        public List<CourseRef> courses = new ArrayList<>();
    }

    public static class CourseRef {
        public String subject;
        public int number;
        public String section;
        public String semester;

        public CourseRef() {}

        public CourseRef(String subject, int number, String section, String semester) {
            this.subject = subject;
            this.number = number;
            this.section = section;
            this.semester = semester;
        }
    }
}
