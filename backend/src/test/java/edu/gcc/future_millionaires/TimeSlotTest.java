package edu.gcc.future_millionaires;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void getDay() {
        TimeSlot t = new TimeSlot();
        t.setDay("M");

        assertEquals("M", t.getDay());
    }

    @Test
    void setDay() {
        TimeSlot t = new TimeSlot();

        t.setDay("T");

        assertEquals("T", t.getDay());
    }

    @Test
    void getStart_time() {
        TimeSlot t = new TimeSlot();
        LocalTime start = LocalTime.of(9, 30);

        t.setStart_time(start);

        assertEquals(start, t.getStart_time());
    }

    @Test
    void setStart_time() {
        TimeSlot t = new TimeSlot();
        LocalTime start = LocalTime.of(10, 0);

        t.setStart_time(start);

        assertEquals(LocalTime.of(10, 0), t.getStart_time());
    }

    @Test
    void getEnd_time() {
        TimeSlot t = new TimeSlot();
        LocalTime end = LocalTime.of(11, 15);

        t.setEnd_time(end);

        assertEquals(end, t.getEnd_time());
    }

    @Test
    void setEnd_time() {
        TimeSlot t = new TimeSlot();
        LocalTime end = LocalTime.of(12, 0);

        t.setEnd_time(end);

        assertEquals(LocalTime.of(12, 0), t.getEnd_time());
    }
}