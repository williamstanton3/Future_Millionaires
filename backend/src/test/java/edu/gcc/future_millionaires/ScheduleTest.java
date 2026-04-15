package edu.gcc.future_millionaires;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {
    private Schedule schedule;
    private final String SEMESTER = "Fall_2026";

    @BeforeEach
    void setUp() {
        schedule = new Schedule(12345, SEMESTER);
    }

    @Test
    void testAddCourseSuccess() {
        Course comp141 = createCourse("COMP", 141, "A", 3, SEMESTER, "M", "09:00", "09:50");

        boolean success = schedule.addCourse(comp141);

        assertTrue(success);
        assertEquals(Schedule.Result.SUCCESS, schedule.getLatestResult());
        assertEquals(1, schedule.getSchedule().size());
        assertEquals(3, schedule.getCredits());
    }

    @Test
    void testAddCourseWrongSemester() {
        Course wrongSem = createCourse("COMP", 141, "A", 3, "Spring_2026", "M", "09:00", "09:50");

        boolean success = schedule.addCourse(wrongSem);

        assertFalse(success);
        assertEquals(Schedule.Result.INVALID_SEMESTER, schedule.getLatestResult());
    }

    @Test
    void testTimeConflict() {
        Course math = createCourse("MATH", 161, "A", 4, SEMESTER, "M", "10:00", "11:00");
        Course physics = createCourse("PHYS", 121, "A", 4, SEMESTER, "M", "10:30", "11:30");

        schedule.addCourse(math);
        boolean success = schedule.addCourse(physics);

        assertFalse(success);
        assertEquals(Schedule.Result.TIME_CONFLICT, schedule.getLatestResult());
        assertEquals(1, schedule.getSchedule().size()); // Physics shouldn't be added
    }

    @Test
    void testDuplicateCourseSection() {
        Course mathA = createCourse("MATH", 161, "A", 4, SEMESTER, "M", "10:00", "11:00");
        Course mathB = createCourse("MATH", 161, "B", 4, SEMESTER, "T", "10:00", "11:00");

        schedule.addCourse(mathA);
        boolean success = schedule.addCourse(mathB);

        assertFalse(success);
        assertEquals(Schedule.Result.DUPLICATE, schedule.getLatestResult());
    }

    @Test
    void testRemoveCourse() {
        Course comp = createCourse("COMP", 141, "A", 3, SEMESTER, "M", "09:00", "09:50");
        schedule.addCourse(comp);

        boolean removed = schedule.removeCourse("COMP", 141, "A");

        assertTrue(removed);
        assertEquals(0, schedule.getSchedule().size());
        assertEquals(0, schedule.getCredits());
    }

    @Test
    void testSuggestAlternatives() {
        // Current Schedule: Math at 10am
        Course mathA = createCourse("MATH", 161, "A", 4, SEMESTER, "M", "10:00", "11:00");
        schedule.addCourse(mathA);

        // Try to add Physics at 10:30 (Conflict!)
        Course physics = createCourse("PHYS", 121, "A", 4, SEMESTER, "M", "10:30", "11:30");

        // Catalog contains an alternate Math at 1pm
        Course mathB = createCourse("MATH", 161, "B", 4, SEMESTER, "M", "13:00", "14:00");
        List<Course> catalog = Arrays.asList(mathA, mathB, physics);

        List<Schedule> suggestions = schedule.suggestAlternatives(physics, catalog);

        assertFalse(suggestions.isEmpty(), "Should find at least one alternative");

        // The suggestion should contain Physics A and Math B
        Schedule firstSuggestion = suggestions.get(0);
        assertTrue(firstSuggestion.getSchedule().stream().anyMatch(c -> c.getSection().equals("B")));
        assertTrue(firstSuggestion.getSchedule().stream().anyMatch(c -> c.getSubject().equals("PHYS")));
    }

    // Helper method to create courses quickly for testing
    private Course createCourse(String sub, int num, String sec, int cred, String sem, String day, String start, String end) {
        TimeSlot slot = new TimeSlot(day, LocalTime.parse(start), LocalTime.parse(end));
        Course c = new Course();
        c.setSubject(sub);
        c.setNumber(num);
        c.setSection(sec);
        c.setCredits(cred);
        c.setSemester(sem);
        c.setTimes(Arrays.asList(slot));
        return c;
    }
}