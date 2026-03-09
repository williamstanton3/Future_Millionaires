package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseListTest {

    @Test
    void getCourses() {
        CourseList c = new CourseList();
        List<Course> courses = c.getCourses();

        // Verify courses loaded
        assertNotNull(courses);

        // Verify JSON actually loaded data
        assertFalse(courses.isEmpty());
    }
}