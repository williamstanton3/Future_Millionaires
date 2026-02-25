package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    @Test
    void addEmptyCourseID() { //does nothing
        Schedule newSchedule = new Schedule(00000, "test");

        int initialSize = newSchedule.getSchedule().size();
        int initialCredits = newSchedule.getCredits();

        newSchedule.addCourse("");

        assertEquals(initialSize, newSchedule.getSchedule().size());
        assertEquals(initialCredits, newSchedule.getCredits());
    }

    @Test
    void addNonIntegerCourseID(){ //should not crash nor add a course
        Schedule newSchedule = new Schedule(00000, "test");

        int initialSize = newSchedule.getSchedule().size();
        int initialCredits = newSchedule.getCredits();

        newSchedule.addCourse("ABCDE");

        assertEquals(initialSize, newSchedule.getSchedule().size());
        assertEquals(initialCredits, newSchedule.getCredits());
    }

    @Test
    void addConflictingCourse(){ //should not crash nor add a course
        Schedule newSchedule = new Schedule(00000, "test");

        newSchedule.addCourse("101");

        int sizeAfterFirst = newSchedule.getSchedule().size();
        int creditsAfterFirst = newSchedule.getCredits();

        newSchedule.addCourse("101");

        assertEquals(sizeAfterFirst, newSchedule.getSchedule().size());
        assertEquals(creditsAfterFirst, newSchedule.getCredits());
    }

    void addCourseCorrect(){
        Schedule newSchedule = new Schedule(00000, "test");

        int initialSize = newSchedule.getSchedule().size();
        int initialCredits = newSchedule.getCredits();

        newSchedule.addCourse("101");

        assertEquals(initialSize, newSchedule.getSchedule().size());
        assertEquals(initialCredits, newSchedule.getCredits());
        assertEquals(101, newSchedule.getSchedule().get(0).getCourseID());
    }

    @Test
    void removeCourse() {
        Course newCourse0 = new Course();
        Course newCourse1 = new Course();
        Course newCourse2 = new Course();
    }
}