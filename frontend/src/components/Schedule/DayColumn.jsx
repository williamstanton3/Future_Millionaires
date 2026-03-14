// src/components/Schedule/DayColumn.jsx
import React from "react";
import "./Schedule.css";

export default function DayColumn({ day, courses }) {
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
              // Calculate vertical position
              const startH = Number(t.start.split(":")[0]);
              const startM = Number(t.start.split(":")[1]);
              const endH = Number(t.end.split(":")[0]);
              const endM = Number(t.end.split(":")[1]);

              const top = ((startH - 8) * 60 + startM); // 60px per hour
              const height = ((endH - startH) * 60 + (endM - startM));

              return (
                <div
                  key={idx}
                  className="class-block"
                  style={{
                    top: `${top}px`,
                    height: `${height}px`,
                    backgroundColor: "#1e40af88",
                    borderColor: "#1e40af",
                  }}
                >
                  <div className="class-block-name">{course.name}</div>
                  <div className="class-block-time">{t.start} - {t.end}</div>
                </div>
              );
            })
        )}
      </div>
    </div>
  );
}
