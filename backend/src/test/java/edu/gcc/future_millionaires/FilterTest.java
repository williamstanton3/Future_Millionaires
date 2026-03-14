package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    // Helper for building TimeSlots
    private static TimeSlot slot(String day, String start, String end) {
        TimeSlot t = new TimeSlot();
        t.setDay(day);
        t.setStart_time(LocalTime.parse(start));
        t.setEnd_time(LocalTime.parse(end));
        return t;
    }

    // Helper for building Courses
    private static Course buildCourse(String subject, int number, String semester,
                                      int credits, List<String> faculty, List<TimeSlot> times) {
        Course c = new Course();
        c.setSubject(subject);
        c.setNumber(number);
        c.setSemester(semester);
        c.setCredits(credits);
        c.setFaculty(faculty);
        c.setTimes(times);
        return c;
    }

    private final Course comp422 = buildCourse("COMP", 422, "2023_Fall", 3,
            List.of("Inman, John G."),
            List.of(slot("M","10:00:00","10:50:00"), slot("W","10:00:00","10:50:00")));

    private final Course comp310 = buildCourse("COMP", 310, "2023_Fall", 3,
            List.of("Graybill, Keith B."),
            List.of(slot("T","15:30:00","16:45:00"), slot("R","15:30:00","16:45:00")));

    private final Course acct201 = buildCourse("ACCT", 201, "2023_Fall", 3,
            List.of("Graybill, Keith B."),
            List.of(slot("M","12:00:00","12:50:00"), slot("W","12:00:00","12:50:00"), slot("F","12:00:00", "12:50:00")));

    private final Course math101 = buildCourse("MATH", 101, "2024_Spring", 4,
            List.of("Shultz, Tricia Michele"),
            List.of(slot("T","09:00:00","10:00:00"), slot("R","09:00:00","10:00:00")));

    private final Course hist200 = buildCourse("HIST", 200, "2023_Fall", 3,
            List.of("Doe, Jane"), List.of());

    private final Course phys101 = buildCourse("PHYS", 101, "2024_Spring", 4,
            List.of(), List.of(slot("M","08:00:00","08:50:00")));

    private final List<Course> allCourses =
            List.of(comp422, comp310, acct201, math101, hist200, phys101);

    // ---------------------------------------------------------
    // NUMBER
    // ---------------------------------------------------------

    @Test
    void filterByNumber_match() {
        Filter f = new Filter();
        f.setNumber(422);

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertEquals(422, results.get(0).getNumber());
    }

    @Test
    void filterByNumber_noMatch() {
        Filter f = new Filter();
        f.setNumber(999);

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByNumber_and_department_match() {
        Filter f = new Filter();
        f.setDepartment("COMP");
        f.setNumber(422);

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertEquals(422, results.get(0).getNumber());
    }

    @Test
    void filterByNumber_and_department_noMatch() {
        Filter f = new Filter();
        f.setDepartment("COMP");
        f.setNumber(999);

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // DEPARTMENT
    // ---------------------------------------------------------

    @Test
    void filterByDepartment_match() {
        Filter f = new Filter();
        f.setDepartment("COMP");

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    @Test
    void filterByDepartment_noMatch() {
        Filter f = new Filter();
        f.setDepartment("BIO");

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // PROFESSORS
    // ---------------------------------------------------------

    @Test
    void filterByProfessors_singleMatch() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"inman"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getFaculty().contains("Inman, John G."));
        assertFalse(results.get(0).getFaculty().contains("Graybill, Keith B."));
    }

    @Test
    void filterByProfessors_caseInsensitive() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"GRAYBILL"});

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    @Test
    void filterByProfessors_partialName() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"graybill"});

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    @Test
    void filterByProfessors_noMatch() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"fake professor"});

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByProfessors_emptyFacultyCourse() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"shultz"});

        List<Course> results = f.apply(allCourses);

        assertTrue(results.contains(math101));
        assertFalse(results.contains(phys101));
    }

    // ---------------------------------------------------------
    // SEMESTER
    // ---------------------------------------------------------

    @Test
    void filterBySemester_match() {
        Filter f = new Filter();
        f.setSemester("2023_Fall");

        assertEquals(4, f.apply(allCourses).size());
    }

    @Test
    void filterBySemester_noMatch() {
        Filter f = new Filter();
        f.setSemester("2020_Fall");

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // CREDITS
    // ---------------------------------------------------------

    @Test
    void filterByCredits_match() {
        Filter f = new Filter();
        f.setCredits(4);

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
        assertTrue(results.contains(math101));
        assertTrue(results.contains(phys101));
    }

    @Test
    void filterByCredits_noMatch() {
        Filter f = new Filter();
        f.setCredits(7);

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // TIMES (DAY ONLY)
    // ---------------------------------------------------------

    @Test
    void filterByTimes_dayMatch() {
        Filter f = new Filter();
        f.setTimes(new String[]{"M"});

        List<Course> results = f.apply(allCourses);

        assertEquals(3, results.size());
    }

    @Test
    void filterByTimes_multipleDays() {
        Filter f = new Filter();
        f.setTimes(new String[]{"T","R"});

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    // ---------------------------------------------------------
    // TIMES (DAY + TIME)
    // ---------------------------------------------------------

    @Test
    void filterByTimes_dayAndTime_match() {
        Filter f = new Filter();
        f.setTimes(new String[]{"T 15:30:00-16:45:00"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertEquals(310, results.get(0).getNumber());
    }

    @Test
    void filterByTimes_dayAndTime_noMatch() {
        Filter f = new Filter();
        f.setTimes(new String[]{"M 15:30:00-16:45:00"});

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // KEYWORD
    // ---------------------------------------------------------

    @Test
    void filterByKeyword_subject() {
        Filter f = new Filter();
        f.setKeyword("comp");

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    @Test
    void filterByKeyword_faculty() {
        Filter f = new Filter();
        f.setKeyword("Inman");

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
    }

    @Test
    void filterByKeyword_number() {
        Filter f = new Filter();
        f.setKeyword("101");

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    @Test
    void filterByKeyword_noMatch() {
        Filter f = new Filter();
        f.setKeyword("astronomy");

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // COMBINED FILTERS
    // ---------------------------------------------------------

    @Test
    void filter_department_and_professor() {
        Filter f = new Filter();
        f.setDepartment("COMP");
        f.setProfessors(new String[]{"graybill"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
    }

    @Test
    void filter_semester_and_credits() {
        Filter f = new Filter();
        f.setSemester("2023_Fall");
        f.setCredits(3);

        List<Course> results = f.apply(allCourses);

        assertEquals(4, results.size());
    }

    @Test
    void filter_professor_and_time() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"graybill"});
        f.setTimes(new String[]{"T 15:30:00-16:45:00"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
    }

    @Test
    void filter_keyword_and_department() {
        Filter f = new Filter();
        f.setKeyword("comp");
        f.setDepartment("COMP");

        List<Course> results = f.apply(allCourses);

        assertEquals(2, results.size());
    }

    @Test
    void filter_keyword_and_time() {
        Filter f = new Filter();
        f.setKeyword("graybill");
        f.setTimes(new String[]{"T 15:30:00-16:45:00"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
    }

    // ---------------------------------------------------------
    // EDGE CASES
    // ---------------------------------------------------------

    @Test
    void noFilters_returnsAllCourses() {
        Filter f = new Filter();

        List<Course> results = f.apply(allCourses);

        assertEquals(allCourses.size(), results.size());
    }

    @Test
    void emptyInput_returnsEmpty() {
        Filter f = new Filter();
        f.setDepartment("COMP");

        assertTrue(f.apply(List.of()).isEmpty());
    }

}