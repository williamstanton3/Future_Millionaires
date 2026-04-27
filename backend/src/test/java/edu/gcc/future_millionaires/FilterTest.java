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

    // Helper for building a minimal Professor using the full constructor.
    // firstName + " " + lastName is how Professor.name is assembled internally,
    // so we split on the first space to reconstruct first/last from a full name string.
    private static Professor prof(String fullName) {
        int sep = fullName.indexOf(' ');
        String first = (sep == -1) ? fullName : fullName.substring(0, sep);
        String last  = (sep == -1) ? ""       : fullName.substring(sep + 1);
        return new Professor(null, 0, first, last, 0, 0.0, 0.0, null, null);
    }

    // Helper for building Courses
    private static Course buildCourse(String subject, int number, String semester,
                                      int credits, List<Professor> faculty, List<TimeSlot> times) {
        Course c = new Course();
        c.setSubject(subject);
        c.setNumber(number);
        c.setSemester(semester);
        c.setCredits(credits);
        c.setProfessors(faculty);
        c.setTimes(times);
        return c;
    }

    // Professor names are passed as "firstName lastName" because Professor.getName()
    // returns firstName + " " + lastName (assembled in the constructor).
    private final Course comp422 = buildCourse("COMP", 422, "2023_Fall", 3,
            List.of(prof("John Inman")),
            List.of(slot("M", "10:00:00", "10:50:00"), slot("W", "10:00:00", "10:50:00")));

    private final Course comp310 = buildCourse("COMP", 310, "2023_Fall", 3,
            List.of(prof("Keith Graybill")),
            List.of(slot("T", "15:30:00", "16:45:00"), slot("R", "15:30:00", "16:45:00")));

    private final Course acct201 = buildCourse("ACCT", 201, "2023_Fall", 3,
            List.of(prof("Keith Graybill")),
            List.of(slot("M", "12:00:00", "12:50:00"), slot("W", "12:00:00", "12:50:00"), slot("F", "12:00:00", "12:50:00")));

    private final Course math101 = buildCourse("MATH", 101, "2024_Spring", 4,
            List.of(prof("Tricia Shultz")),
            List.of(slot("T", "09:00:00", "10:00:00"), slot("R", "09:00:00", "10:00:00")));

    private final Course hist200 = buildCourse("HIST", 200, "2023_Fall", 3,
            List.of(prof("Jane Doe")),
            List.of());

    private final Course phys101 = buildCourse("PHYS", 101, "2024_Spring", 4,
            List.of(),
            List.of(slot("M", "08:00:00", "08:50:00")));

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
    void filterByNumber_zero_treatedAsUnset() {
        // number == 0 should behave like "no filter" (the guard is number > 0)
        Filter f = new Filter();
        f.setNumber(0);

        assertEquals(allCourses.size(), f.apply(allCourses).size());
    }

    @Test
    void filterByNumber_negative_treatedAsUnset() {
        // negative numbers should also be ignored by the number > 0 guard
        Filter f = new Filter();
        f.setNumber(-1);

        assertEquals(allCourses.size(), f.apply(allCourses).size());
    }

    // ---------------------------------------------------------
    // DEPARTMENT
    // ---------------------------------------------------------

    @Test
    void filterByDepartment_match() {
        Filter f = new Filter();
        f.setDepartment("COMP");

        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filterByDepartment_noMatch() {
        Filter f = new Filter();
        f.setDepartment("BIO");

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByDepartment_caseInsensitive_lower() {
        Filter f = new Filter();
        f.setDepartment("comp");

        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filterByDepartment_caseInsensitive_mixed() {
        Filter f = new Filter();
        f.setDepartment("CoMp");

        assertEquals(2, f.apply(allCourses).size());
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
        assertEquals("John Inman", results.get(0).getProfessors().get(0).getName());
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
    void filterByProfessors_firstNameOnly() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"jane"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertEquals(hist200, results.get(0));
    }

    @Test
    void filterByProfessors_fullNameMatch() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"John Inman"});

        assertEquals(1, f.apply(allCourses).size());
        assertEquals(comp422, f.apply(allCourses).get(0));
    }

    @Test
    void filterByProfessors_multipleNames_anyMatchSuffices() {
        // array with two names — a course matching either should be returned
        Filter f = new Filter();
        f.setProfessors(new String[]{"inman", "graybill"});

        // comp422 (Inman) + comp310 (Graybill) + acct201 (Graybill) = 3
        assertEquals(3, f.apply(allCourses).size());
    }

    @Test
    void filterByProfessors_noMatch() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"fake professor"});

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByProfessors_emptyFacultyCourseExcluded() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"shultz"});

        List<Course> results = f.apply(allCourses);

        assertTrue(results.contains(math101));
        assertFalse(results.contains(phys101)); // phys101 has no professors
    }

    @Test
    void filterByProfessors_emptyArray_returnsNoCourses() {
        // An empty professors array — the for-loop never sets found=true,
        // so professors != null but no name is ever matched → nothing returned
        Filter f = new Filter();
        f.setProfessors(new String[]{});

        assertTrue(f.apply(allCourses).isEmpty());
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
        f.setSemester("1999_Fall");

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
    }

    @Test
    void filterByCredits_noMatch() {
        Filter f = new Filter();
        f.setCredits(5);

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByCredits_zero_returnsEmpty() {
        // credits == 0 passes the >= 0 guard, so it actively filters for 0-credit courses
        Filter f = new Filter();
        f.setCredits(0);

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByCredits_negativeOne_treatedAsUnset() {
        // -1 is the sentinel "unset" value; should return all courses
        Filter f = new Filter();
        f.setCredits(-1);

        assertEquals(allCourses.size(), f.apply(allCourses).size());
    }

    // ---------------------------------------------------------
    // TIMES
    // ---------------------------------------------------------

    @Test
    void filterByTimes_dayOnly_match() {
        Filter f = new Filter();
        f.setTimes(new String[]{"M"});

        // comp422 (M,W), acct201 (M,W,F), phys101 (M)
        assertEquals(3, f.apply(allCourses).size());
    }

    @Test
    void filterByTimes_dayAndTimeRange_match() {
        Filter f = new Filter();
        f.setTimes(new String[]{"T 15:30:00-16:45:00"});

        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertEquals(comp310, results.get(0));
    }

    @Test
    void filterByTimes_courseWithNoSlots_excluded() {
        Filter f = new Filter();
        f.setTimes(new String[]{"M"});

        // hist200 has no time slots and should never be returned
        assertFalse(f.apply(allCourses).contains(hist200));
    }

    @Test
    void filterByTimes_multipleDays_anyMatchSuffices() {
        Filter f = new Filter();
        f.setTimes(new String[]{"M", "F"});

        // comp422 (M,W), acct201 (M,W,F), phys101 (M) — all have M or F
        assertEquals(3, f.apply(allCourses).size());
    }

    @Test
    void filterByTimes_exactSlotBoundary_matches() {
        // Filter window exactly equals phys101's slot: M 08:00-08:50
        Filter f = new Filter();
        f.setTimes(new String[]{"M 08:00:00-08:50:00"});

        List<Course> results = f.apply(allCourses);

        assertTrue(results.contains(phys101));
    }

    @Test
    void filterByTimes_timeRange_noMatch() {
        Filter f = new Filter();
        f.setTimes(new String[]{"M 07:00:00-07:50:00"});

        // phys101 starts at 08:00, which is after the window ends
        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filterByTimes_slotStartsBeforeWindowStart_excluded() {
        // comp422 starts at 10:00; filter requires start >= 10:30 → should not match
        Filter f = new Filter();
        f.setTimes(new String[]{"M 10:30:00-11:00:00"});

        assertFalse(f.apply(allCourses).contains(comp422));
    }

    @Test
    void filterByTimes_emptyArray_returnsNoCourses() {
        // times != null but empty — matchesTimes inner loop never runs, anyMatch stays false
        Filter f = new Filter();
        f.setTimes(new String[]{});

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // KEYWORD
    // ---------------------------------------------------------

    @Test
    void filterByKeyword_subject() {
        Filter f = new Filter();
        f.setKeyword("comp");

        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filterByKeyword_professorName() {
        Filter f = new Filter();
        f.setKeyword("Inman");

        assertEquals(1, f.apply(allCourses).size());
    }

    @Test
    void filterByKeyword_courseNumber() {
        Filter f = new Filter();
        f.setKeyword("101");

        // math101 and phys101
        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filterByKeyword_caseInsensitive_upperInput() {
        Filter f = new Filter();
        f.setKeyword("INMAN");

        assertEquals(1, f.apply(allCourses).size());
    }

    @Test
    void filterByKeyword_semester() {
        Filter f = new Filter();
        f.setKeyword("2024_Spring");

        // math101 and phys101
        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filterByKeyword_partialSubject() {
        Filter f = new Filter();
        f.setKeyword("ac"); // matches "ACCT"

        assertEquals(1, f.apply(allCourses).size());
        assertEquals(acct201, f.apply(allCourses).get(0));
    }

    @Test
    void filterByKeyword_emptyString_matchesAll() {
        // Every field .contains("") is true, so all courses should be returned
        Filter f = new Filter();
        f.setKeyword("");

        assertEquals(allCourses.size(), f.apply(allCourses).size());
    }

    @Test
    void filterByKeyword_noMatch() {
        Filter f = new Filter();
        f.setKeyword("zzznomatch");

        assertTrue(f.apply(allCourses).isEmpty());
    }

    // ---------------------------------------------------------
    // COMBINED
    // ---------------------------------------------------------

    @Test
    void filter_department_and_professors() {
        Filter f = new Filter();
        f.setDepartment("COMP");
        f.setProfessors(new String[]{"graybill"});

        // comp310 matches both; comp422 has Inman so it's excluded
        assertEquals(1, f.apply(allCourses).size());
        assertEquals(comp310, f.apply(allCourses).get(0));
    }

    @Test
    void filter_semester_and_credits() {
        Filter f = new Filter();
        f.setSemester("2024_Spring");
        f.setCredits(4);

        // math101 and phys101 are both Spring 2024, 4 credits
        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filter_number_and_wrongDepartment_returnsEmpty() {
        Filter f = new Filter();
        f.setNumber(422);
        f.setDepartment("MATH"); // 422 exists but not in MATH

        assertTrue(f.apply(allCourses).isEmpty());
    }

    @Test
    void filter_professors_and_times_narrowsResult() {
        Filter f = new Filter();
        f.setProfessors(new String[]{"graybill"});
        f.setTimes(new String[]{"T"});

        // Graybill teaches comp310 (T,R) and acct201 (M,W,F); only comp310 has Tuesday
        List<Course> results = f.apply(allCourses);

        assertEquals(1, results.size());
        assertEquals(comp310, results.get(0));
    }

    @Test
    void filter_keyword_and_credits_combined() {
        Filter f = new Filter();
        f.setKeyword("spring");
        f.setCredits(4);

        // Both Spring courses are 4 credits
        assertEquals(2, f.apply(allCourses).size());
    }

    @Test
    void filter_emptyCourselist_returnsEmpty() {
        Filter f = new Filter();
        f.setDepartment("COMP");

        assertTrue(f.apply(List.of()).isEmpty());
    }

    @Test
    void noFilters_returnsAllCourses() {
        Filter f = new Filter();

        assertEquals(allCourses.size(), f.apply(allCourses).size());
    }
}