package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    /**
     * Reads persisted data and populates the student's two schedule maps.
     * If the file doesn't exist yet, the maps are left empty (first run).
     */
    public void load(Student student) {
        if (!file.exists()) return;

        try {
            StudentData data = mapper.readValue(file, StudentData.class);

            if (data.schedules != null) {
                data.schedules.forEach((semester, schedule) -> {
                    student.getSchedules().put(semester, schedule);
                });
            }
            if (data.savedSchedules != null) {
                data.savedSchedules.forEach((semester, schedule) -> {
                    student.getSavedSchedules().put(semester, schedule);
                });
            }

            // Restore active semester if there is exactly one in-progress schedule
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

    /**
     * Writes the student's current state to disk. Call this after every mutation.
     */
    public void save(Student student) {
        try {
            StudentData data = new StudentData();
            data.activeSemester = student.getActiveSemester();
            data.schedules = student.getSchedules();
            data.savedSchedules = student.getSavedSchedules();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Warning: could not save student data to " + file.getPath());
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // DTO used for the root JSON object
    // -------------------------------------------------------------------------

    public static class StudentData {
        public String activeSemester;
        public Map<String, Schedule> schedules = new HashMap<>();
        public Map<String, Schedule> savedSchedules = new HashMap<>();
    }
}
