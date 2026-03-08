package edu.gcc.future_millionaires;

import java.time.LocalTime;

public class TimeSlot {

    private String day;
    private LocalTime start_time;
    private LocalTime end_time;

    public TimeSlot() {}

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public LocalTime getStart_time() { return start_time; }
    public void setStart_time(LocalTime start_time) { this.start_time = start_time; }

    public LocalTime getEnd_time() { return end_time; }
    public void setEnd_time(LocalTime end_time) { this.end_time = end_time; }

}
