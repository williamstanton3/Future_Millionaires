package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    private static TimeSlot slot(String day, String start, String end) {
        TimeSlot t = new TimeSlot();
        t.setDay(day);
        t.setStart_time(LocalTime.parse(start));
        t.setEnd_time(LocalTime.parse(end));
        return t;
    }

    @Test
    void returnResults() {

        // Create courses using your JSON-style fields

        Course c1 = new Course();
        c1.setFaculty(List.of("Dr. Smith"));
        c1.setSubject("CS");
        c1.setSemester("Fall");
        c1.setCredits(3);
        c1.setTimes(List.of(
                slot("M", "10:00", "11:00"),
                slot("W", "10:00", "11:00")
        ));
        c1.setTotalSeats(30);

        Course c2 = new Course();
        c2.setFaculty(List.of("Dr. Jones"));
        c2.setSubject("Math");
        c2.setSemester("Spring");
        c2.setCredits(4);
        c2.setTimes(List.of(
                slot("T", "12:00", "13:00"),
                slot("R", "12:00", "13:00")
        ));
        c2.setTotalSeats(25);

        Course c3 = new Course();
        c3.setFaculty(List.of("Dr. Smith"));
        c3.setSubject("CS");
        c3.setSemester("Fall");
        c3.setCredits(3);
        c3.setTimes(List.of(
                slot("T", "14:00", "15:00"),
                slot("R", "14:00", "15:00")
        ));
        c3.setTotalSeats(25);

        List<Course> courses = List.of(c1, c2, c3);

        // Create filter (JSON-style)
        Filter filter = new Filter();
        filter.setProfessor("Dr. Smith");
        filter.setSubject("CS");

        // Run filter
        List<Course> results = filter.apply(courses);

        // Assertions
        assertEquals(2, results.size(),
                "Should return 2 courses matching professor Dr. Smith and subject CS");

        for (Course c : results) {
            assertEquals("CS", c.getSubject());
            assertTrue(c.getFaculty().contains("Dr. Smith"));
        }
    }
}