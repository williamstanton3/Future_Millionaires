package edu.gcc.future_millionaires;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.util.*;

public class CourseList {

    private final List<Course> courses;

    public CourseList(RateMyProfApi rmpApi) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // -----------------------------
            // Load professor images map
            // -----------------------------
            InputStream imgInput = CourseList.class.getClassLoader()
                    .getResourceAsStream("professor_images.json");

            Map<String, String> professorImages = new HashMap<>();

            if (imgInput != null) {
                professorImages = mapper.readValue(
                        imgInput,
                        new TypeReference<Map<String, String>>() {}
                );
            }

            // Normalize image keys ONCE
            Map<String, String> normalizedImages = new HashMap<>();
            for (Map.Entry<String, String> entry : professorImages.entrySet()) {
                normalizedImages.put(normalizeName(entry.getKey()), entry.getValue());
            }

            // -----------------------------
            // Load course data
            // -----------------------------
            InputStream input = CourseList.class.getClassLoader()
                    .getResourceAsStream("data_wolfe.json");

            if (input == null) {
                throw new RuntimeException("data_wolfe.json not found");
            }

            JsonNode root = mapper.readTree(input);
            JsonNode classesNode = root.get("classes");

            List<Course> courseList = new ArrayList<>();

            // -----------------------------
            // Build courses
            // -----------------------------
            for (JsonNode node : classesNode) {

                Course course = mapper.treeToValue(node, Course.class);

                List<Professor> profs = new ArrayList<>();

                JsonNode facultyNode = node.get("faculty");

                if (facultyNode != null && facultyNode.isArray()) {

                    for (JsonNode f : facultyNode) {

                        String rawName = f.asText();
                        String cleanedName = stripAlumni(rawName);

                        String first = "";
                        String last = "";

                        if (cleanedName.contains(",")) {
                            String[] parts = cleanedName.split(",", 2);
                            last = parts[0].trim();
                            first = parts[1].trim().split(" ")[0];
                        } else {
                            String[] parts = cleanedName.trim().split(" ");
                            if (parts.length >= 2) {
                                first = parts[0];
                                last = parts[parts.length - 1];
                            } else {
                                first = cleanedName.trim();
                            }
                        }

                        String normalized = normalizeName(cleanedName);
                        String imageUrl = normalizedImages.getOrDefault(
                                normalized,
                                "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3407.jpg"
                        );

                        // Look up RMP data by last name
                        Professor rmp = rmpApi.getProfessorByLastName(last);

                        Professor prof = new Professor(
                                cleanedName,                                        // id
                                rmp != null ? rmp.getLegacyId() : 0,               // legacyId
                                first,
                                last,
                                rmp != null ? rmp.getNumOfRatings() : 0,           // numOfRatings
                                rmp != null ? rmp.getOverallRating() : 0.0,        // overallRating
                                rmp != null ? rmp.getAvgDifficulty() : 0.0,        // avgDifficulty
                                course.getSubject(),
                                imageUrl
                        );

                        profs.add(prof);
                    }
                }

                course.setProfessors(profs);
                courseList.add(course);
            }

            courses = courseList;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load courses JSON", e);
        }
    }

    // -----------------------------
    // Remove alumni suffix like '07, '1999
    // -----------------------------
    private String stripAlumni(String name) {
        if (name == null) return "";

        // removes '07, '1999, etc.
        return name.replaceAll("\\s*'\\d{2,4}$", "").trim();
    }

    // -----------------------------
    // Normalize to FIRST + LAST only
    // -----------------------------
    private String normalizeName(String rawName) {
        if (rawName == null || rawName.isEmpty()) return "";

        String cleaned = stripAlumni(rawName);

        String first = "";
        String last = "";

        if (cleaned.contains(",")) {
            // "Graybill, Keith B."
            String[] parts = cleaned.split(",", 2);
            last = parts[0].trim();

            String[] firstParts = parts[1].trim().split(" ");
            first = firstParts[0]; // ONLY first name (ignore middle)
        } else {
            // "Keith B. Graybill"
            String[] parts = cleaned.trim().split(" ");
            first = parts[0];
            last = parts[parts.length - 1];
        }

        return (first + " " + last).toLowerCase();
    }

    public List<Course> getCourses() {
        return courses;
    }
}