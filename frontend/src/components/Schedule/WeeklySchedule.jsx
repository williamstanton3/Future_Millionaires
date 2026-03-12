import React from "react";
import { courses } from "../../data/mockCourses";
import TimeColumn from "./TimeColumn";
import DayColumn from "./DayColumn";
import "./Schedule.css";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function WeeklySchedule() {
  return (
    <div className="planner-root">
      {/* Header */}
      <div>
        <p className="planner-eyebrow">Grove City College</p>
        <h1 className="planner-title">Schedule Planner</h1>
        <div className="planner-legend">
          {courses.map((c) => (
            <span
              key={c.id}
              className="legend-chip"
              style={{
                color: c.color,
                background: `${c.color}18`,
                border: `1px solid ${c.color}33`,
              }}
            >
              {c.name}
            </span>
          ))}
        </div>
      </div>

      {/* Grid wrapper: scrollable if too tall */}
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