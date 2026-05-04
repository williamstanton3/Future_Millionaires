package edu.gcc.future_millionaires;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseListTest {

    // Stub that always returns null — simulates RMP being unavailable or
    // a professor not found. CourseList must still build professors with
    // zeroed-out RMP fields rather than throwing.
    private static final RateMyProfApi NO_RMP = new RateMyProfApi(null) {
        @Override
        public Professor getProfessorByLastName(String lastName) {
            return null;
        }
    };

    // Stub that returns a fake Professor with known RMP data so we can
    // verify that CourseList actually wires RMP fields through correctly.
    private static final RateMyProfApi FAKE_RMP = new RateMyProfApi(null) {
        @Override
        public Professor getProfessorByLastName(String lastName) {
            return new Professor(
                    lastName,   // id
                    99,         // legacyId
                    "Fake",     // firstName
                    lastName,   // lastName
                    42,         // numOfRatings
                    4.5,        // overallRating
                    3.2,        // avgDifficulty
                    "TEST",     // subject
                    "https://example.com/fake.jpg"
            );
        }
    };

    private List<Course> courses;

    @BeforeEach
    void load() {
        courses = new CourseList(NO_RMP).getCourses();
    }

    // ---------------------------------------------------------
    // LOADING
    // ---------------------------------------------------------

    @Test
    void courses_notNull() {
        assertNotNull(courses);
    }

    @Test
    void courses_notEmpty() {
        assertFalse(courses.isEmpty());
    }

    @Test
    void courseList_does_not_throw_when_rmp_returns_null() {
        assertDoesNotThrow(() -> new CourseList(NO_RMP));
    }

    // ---------------------------------------------------------
    // COURSE FIELDS
    // ---------------------------------------------------------

    @Test
    void every_course_has_subject() {
        for (Course c : courses) {
            assertNotNull(c.getSubject(),
                    "Expected subject on course " + c.getNumber());
            assertFalse(c.getSubject().isBlank(),
                    "Expected non-blank subject on course " + c.getNumber());
        }
    }

    @Test
    void every_course_has_positive_number() {
        for (Course c : courses) {
            assertTrue(c.getNumber() > 0,
                    "Expected positive course number, got " + c.getNumber());
        }
    }

    @Test
    void every_course_has_semester() {
        for (Course c : courses) {
            assertNotNull(c.getSemester(),
                    "Expected semester on course " + c.getNumber());
            assertFalse(c.getSemester().isBlank(),
                    "Expected non-blank semester on course " + c.getNumber());
        }
    }

    @Test
    void every_course_professors_list_not_null() {
        for (Course c : courses) {
            assertNotNull(c.getProfessors(),
                    "Professor list should never be null (course " + c.getNumber() + ")");
        }
    }

    @Test
    void every_course_times_list_not_null() {
        for (Course c : courses) {
            assertNotNull(c.getTimes(),
                    "Times list should never be null (course " + c.getNumber() + ")");
        }
    }

    // ---------------------------------------------------------
    // PROFESSOR FIELDS — structural
    // ---------------------------------------------------------


    @Test
    void professor_name_is_firstName_space_lastName() {
        for (Course c : courses) {
            for (Professor p : c.getProfessors()) {
                String expected = p.getFirstName() + " " + p.getLastName();
                assertEquals(expected, p.getName(),
                        "getName() should equal firstName + \" \" + lastName");
            }
        }
    }

    @Test
    void professor_subject_matches_course_subject() {
        for (Course c : courses) {
            for (Professor p : c.getProfessors()) {
                assertEquals(c.getSubject(), p.getDepartment(),
                        "Professor subject should match course subject for " + p.getName());
            }
        }
    }

    // ---------------------------------------------------------
    // PROFESSOR FIELDS — RMP zeroed when API unavailable
    // ---------------------------------------------------------

    @Test
    void professors_rmp_fields_zero_when_api_unavailable() {
        for (Course c : courses) {
            for (Professor p : c.getProfessors()) {
                assertEquals(0, p.getLegacyId(),
                        "Expected legacyId=0 when RMP unavailable (" + p.getName() + ")");
                assertEquals(0, p.getNumOfRatings(),
                        "Expected numOfRatings=0 when RMP unavailable (" + p.getName() + ")");
                assertEquals(0.0, p.getOverallRating(), 0.001,
                        "Expected overallRating=0.0 when RMP unavailable (" + p.getName() + ")");
                assertEquals(0.0, p.getAvgDifficulty(), 0.001,
                        "Expected avgDifficulty=0.0 when RMP unavailable (" + p.getName() + ")");
            }
        }
    }

    // ---------------------------------------------------------
    // PROFESSOR FIELDS — RMP data wired through when API returns data
    // ---------------------------------------------------------

    @Test
    void professors_rmp_fields_populated_when_api_returns_data() {
        List<Course> fakeRmpCourses = new CourseList(FAKE_RMP).getCourses();
        for (Course c : fakeRmpCourses) {
            for (Professor p : c.getProfessors()) {
                assertEquals(99, p.getLegacyId(),
                        "Expected legacyId=99 from FAKE_RMP for " + p.getName());
                assertEquals(42, p.getNumOfRatings(),
                        "Expected numOfRatings=42 from FAKE_RMP for " + p.getName());
                assertEquals(4.5, p.getOverallRating(), 0.001,
                        "Expected overallRating=4.5 from FAKE_RMP for " + p.getName());
                assertEquals(3.2, p.getAvgDifficulty(), 0.001,
                        "Expected avgDifficulty=3.2 from FAKE_RMP for " + p.getName());
            }
        }
    }

    // ---------------------------------------------------------
    // ALUMNI SUFFIX STRIPPING
    // ---------------------------------------------------------

    @Test
    void no_professor_name_contains_alumni_suffix() {
        for (Course c : courses) {
            for (Professor p : c.getProfessors()) {
                assertFalse(p.getName().matches(".*'\\d{2,4}$"),
                        "Alumni suffix not stripped from name: " + p.getName());
            }
        }
    }

    @Test
    void no_professor_firstName_contains_alumni_suffix() {
        for (Course c : courses) {
            for (Professor p : c.getProfessors()) {
                assertFalse(p.getFirstName().matches(".*'\\d{2,4}$"),
                        "Alumni suffix not stripped from firstName: " + p.getFirstName());
            }
        }
    }

    @Test
    void no_professor_lastName_contains_alumni_suffix() {
        for (Course c : courses) {
            for (Professor p : c.getProfessors()) {
                assertFalse(p.getLastName().matches(".*'\\d{2,4}$"),
                        "Alumni suffix not stripped from lastName: " + p.getLastName());
            }
        }
    }

    // ---------------------------------------------------------
    // CONSISTENCY CHECKS
    // ---------------------------------------------------------

    @Test
    void getCourses_returns_same_list_on_repeated_calls() {
        CourseList cl = new CourseList(NO_RMP);
        assertSame(cl.getCourses(), cl.getCourses(),
                "getCourses() should return the same list instance each time");
    }

    @Test
    void no_course_has_null_subject_and_zero_number_simultaneously() {
        for (Course c : courses) {
            assertFalse(c.getSubject() == null && c.getNumber() == 0,
                    "Course appears completely unpopulated (null subject + number=0)");
        }
    }
}