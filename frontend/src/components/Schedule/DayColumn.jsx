import React from "react";
import ClassBlock from "./ClassBlock";

const DAY_LABELS = { Mon: "Mon", Tue: "Tue", Wed: "Wed", Thu: "Thu", Fri: "Fri" };

const DayColumn = ({ day, courses, startHour = 8, endHour = 18, hourHeight = 56, }) => {
  const totalHeight = (endHour - startHour) * hourHeight + 48; // 48 = HEADER_HEIGHT
  const timeToTop = (timeStr) => {
      const [hour, minute] = timeStr.split(":").map(Number);
      return (hour + minute / 60 - startHour) * hourHeight;
    };

  return (
    <div className="day-column" style={{ height: totalHeight + "px" }}>
      {/* Header */}
      <div className="day-column-header" style={{ height: "48px" }}>
        <span className="day-header-abbr">{day}</span>
        <span className="day-header-name">{DAY_LABELS[day]}</span>
      </div>

      {/* Hour grid lines */}
      {Array.from({ length: endHour - startHour }).map((_, i) => (
        <div
          key={i}
          className="hour-line"
          style={{ top: 48 + i * hourHeight + "px", height: hourHeight + "px" }}
        />
      ))}

      {/* Course blocks */}
      {courses
        .filter((course) => course.days.includes(day))
        .map((course) => {
          const top = timeToTop(course.start);
          const height = timeToTop(course.end) - top;
          return (
            <ClassBlock
              key={course.id}
              course={course}
              top={top}
              height={height}
            />
          );
        })}
    </div>
  );
};

export default DayColumn;
