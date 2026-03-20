// src/components/Schedule/DayColumn.jsx
import React from "react";
import "./Schedule.css";

const formatTime = (timeStr) => {
  if (!timeStr) return "";
  const [hour, min] = timeStr.split(":").map(Number);
  const h = hour % 12 === 0 ? 12 : hour % 12;
  const ampm = hour < 12 ? "AM" : "PM";
  return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
};

export default function DayColumn({ day, courses, onCourseClick }) {
  const dayCourses = courses.filter(course =>
    course.times.some(t => t.day === day)
  );

  return (
    <div className="day-column">
      <div className="day-column-header">
        <div className="day-header-abbr">{day}</div>
      </div>

      <div className="day-column-body">
        {dayCourses.map(course =>
          course.times
            .filter(t => t.day === day)
            .map((t, idx) => {
              const startH = Number(t.start.split(":")[0]);
              const startM = Number(t.start.split(":")[1]);
              const endH = Number(t.end.split(":")[0]);
              const endM = Number(t.end.split(":")[1]);

              const top = ((startH - 8) * 60 + startM);
              const height = ((endH - startH) * 60 + (endM - startM));

              return (
                <div
                  key={idx}
                  className="class-block"
                  style={{
                    top: `${top}px`,
                    height: `${height}px`,
                    backgroundColor: course.color ? `${course.color}33` : "#1e40af88",
                    borderColor: course.color ?? "#1e40af",
                    cursor: "pointer",
                  }}
                  onClick={() => onCourseClick(course)}
                >
                  <div className="class-block-name">{course.name}</div>
                  <div className="class-block-time">{formatTime(t.start)} – {formatTime(t.end)}</div>
                </div>
              );
            })
        )}
      </div>
    </div>
  );
}
