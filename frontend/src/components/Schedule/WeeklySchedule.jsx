// src/components/Schedule/WeeklySchedule.jsx
import React from "react";
import TimeColumn from "./TimeColumn";
import DayColumn from "./DayColumn";
import "./Schedule.css";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function WeeklySchedule({ courses }) {
  return (
    <div className="planner-root">
      {/* Header */}
      <div>
        <p className="planner-eyebrow">Grove City College</p>
        <h1 className="planner-title">Schedule Planner</h1>
      </div>

      {/* Grid wrapper */}
      <div className="schedule-wrapper">
        <div className="schedule">
          <TimeColumn />
          {DAYS.map((day) => (
            <DayColumn
              key={day}
              day={day}
              courses={courses}
            />
          ))}
        </div>
      </div>
    </div>
  );
}