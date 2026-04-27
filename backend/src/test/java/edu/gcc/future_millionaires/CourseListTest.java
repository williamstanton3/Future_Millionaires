//package edu.gcc.future_millionaires;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class CourseListTest {
//
//    // Stub that always returns null — simulates RMP being unavailable or
//    // a professor not found. CourseList must still build professors with
//    // zeroed-out RMP fields rather than throwing.
//    private static final RateMyProfApi NO_RMP = lastName -> null;
//
//    private List<Course> courses;
//
//    @BeforeEach
//    void load() {
//        courses = new CourseList(NO_RMP).getCourses();
//    }
//
//    // ---------------------------------------------------------
//    // LOADING
//    // ---------------------------------------------------------
//
//    @Test
//    void courses_notNull() {
//        assertNotNull(courses);
//    }
//
//    @Test
//    void courses_notEmpty() {
//        assertFalse(courses.isEmpty());
//    }
//
//    // ---------------------------------------------------------
//    // COURSE FIELDS
//    // ---------------------------------------------------------
//
//    @Test
//    void every_course_has_subject() {
//        for (Course c : courses) {
//            assertNotNull(c.getSubject(),
//                    "Expected subject on course " + c.getNumber());
//            assertFalse(c.getSubject().isBlank(),
//                    "Expected non-blank subject on course " + c.getNumber());
//        }
//    }
//
//    @Test
//    void every_course_has_positive_number() {
//        for (Course c : courses) {
//            assertTrue(c.getNumber() > 0,
//                    "Expected positive course number, got " + c.getNumber());
//        }
//    }
//
//    @Test
//    void every_course_has_semester() {
//        for (Course c : courses) {
//            assertNotNull(c.getSemester(),
//                    "Expected semester on course " + c.getNumber());
//            assertFalse(c.getSemester().isBlank(),
//                    "Expected non-blank semester on course " + c.getNumber());
//        }
//    }
//
//    @Test
//    void every_course_professors_list_not_null() {
//        for (Course c : courses) {
//            assertNotNull(c.getProfessors(),
//                    "Professor list should never be null (course " + c.getNumber() + ")");
//        }
//    }
//
//    @Test
//    void every_course_times_list_not_null() {
//        for (Course c : courses) {
//            assertNotNull(c.getTimes(),
//                    "Times list should never be null (course " + c.getNumber() + ")");
//        }
//    }
//
//    // ---------------------------------------------------------
//    // PROFESSOR FIELDS
//    // ---------------------------------------------------------
//
//    @Test
//    void professors_have_non_blank_first_and_last_name() {
//        for (Course c : courses) {
//            for (Professor p : c.getProfessors()) {
//                assertFalse(p.getFirstName().isBlank(),
//                        "Expected non-blank firstName on professor in course " + c.getNumber());
//                assertFalse(p.getLastName().isBlank(),
//                        "Expected non-blank lastName on professor in course " + c.getNumber());
//            }
//        }
//    }
//
//    @Test
//    void professor_name_is_firstName_space_lastName() {
//        for (Course c : courses) {
//            for (Professor p : c.getProfessors()) {
//                String expected = p.getFirstName() + " " + p.getLastName();
//                assertEquals(expected, p.getName(),
//                        "getName() should equal firstName + \" \" + lastName");
//            }
//        }
//    }
//
//    @Test
//    void professors_have_non_null_image_url() {
//        for (Course c : courses) {
//            for (Professor p : c.getProfessors()) {
//                assertNotNull(p.getImageUrl(),
//                        "imageUrl should not be null for professor " + p.getName());
//                assertFalse(p.getImageUrl().isBlank(),
//                        "imageUrl should not be blank for professor " + p.getName());
//            }
//        }
//    }
//
//    @Test
//    void professors_rmp_fields_zero_when_api_unavailable() {
//        // With NO_RMP stub every professor should have zeroed-out RMP data
//        for (Course c : courses) {
//            for (Professor p : c.getProfessors()) {
//                assertEquals(0, p.getLegacyId(),
//                        "Expected legacyId=0 when RMP unavailable (" + p.getName() + ")");
//                assertEquals(0, p.getNumOfRatings(),
//                        "Expected numOfRatings=0 when RMP unavailable (" + p.getName() + ")");
//                assertEquals(0.0, p.getOverallRating(), 0.001,
//                        "Expected overallRating=0.0 when RMP unavailable (" + p.getName() + ")");
//                assertEquals(0.0, p.getAvgDifficulty(), 0.001,
//                        "Expected avgDifficulty=0.0 when RMP unavailable (" + p.getName() + ")");
//            }
//        }
//    }
//
//    // ---------------------------------------------------------
//    // ALUMNI SUFFIX STRIPPING
//    // ---------------------------------------------------------
//
//    @Test
//    void no_professor_name_contains_alumni_suffix() {
//        // Names like "Smith '07" or "Jones '2003" must be stripped before
//        // being stored in Professor.firstName / lastName / name.
//        for (Course c : courses) {
//            for (Professor p : c.getProfessors()) {
//                assertFalse(p.getName().matches(".*'\\d{2,4}$"),
//                        "Alumni suffix not stripped from professor name: " + p.getName());
//            }
//        }
//    }
//}