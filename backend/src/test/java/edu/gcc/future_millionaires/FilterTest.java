package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    @Test
    void returnResults() {
        // Create courses
        Course c1 = new Course();
        c1.editCourse("Dr. Smith", new String[]{"Mon", "Wed"}, new String[]{"10:00-11:00"}, 30);
        c1.setDepartment("CS");
        c1.setSemester("Fall");
        c1.setCredits(3);

        Course c2 = new Course();
        c2.editCourse("Dr. Jones", new String[]{"Tue", "Thu"}, new String[]{"12:00-13:00"}, 25);
        c2.setDepartment("Math");
        c2.setSemester("Spring");
        c2.setCredits(4);

        Course c3 = new Course();
        c3.editCourse("Dr. Smith", new String[]{"Tue", "Thu"}, new String[]{"14:00-15:00"}, 25);
        c3.setDepartment("CS");
        c3.setSemester("Fall");
        c3.setCredits(3);

        Course[] courses = {c1, c2, c3};

        // Create filter
        Filter filter = new Filter();
        filter.setProfessor("Dr. Smith");
        filter.setDepartment("CS");

        // Run filter
        ArrayList<Course> results = filter.returnResults(courses);

        // Assertions
        assertEquals(2, results.size(), "Should return 2 courses matching professor Dr. Smith and department CS");

        for (Course c : results) {
            assertEquals("Dr. Smith", c.getProfessor());
            assertEquals("CS", c.getDepartment());
        }
    }
}