import React from "react";
import { courses } from "../../data/mockCourses";
import ClassBlock from "./ClassBlock";
import TimeColumn from "./TimeColumn";
import DayColumn from "./DayColumn";
import "./Schedule.css";

const days = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function WeeklySchedule() {
    return (
        <div className="schedule">
            <TimeColumn />
            {days.map((day) => (
                <DayColumn key={day} day={day} courses={courses} />
            ))}
        </div>
    );
}