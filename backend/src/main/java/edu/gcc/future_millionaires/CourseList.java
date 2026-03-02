package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class CourseList {

    private final List<Course> courses;

    public CourseList() {
        try {

            InputStream input =
                    CourseList.class.getClassLoader()
                            .getResourceAsStream("data_wolfe.json");

            if (input == null) {
                throw new RuntimeException(
                        "data_wolfe.json not found in src/main/resources/"
                );
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // Read root object
            JsonNode root = mapper.readTree(input);

            // Extract "classes" array
            JsonNode classesNode = root.get("classes");

            Course[] arr =
                    mapper.treeToValue(classesNode, Course[].class);

            courses = Arrays.asList(arr);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load courses JSON",
                    e
            );
        }
    }

    public List<Course> getCourses() {
        return courses;
    }
}