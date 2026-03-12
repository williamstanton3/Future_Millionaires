import React from "react";

const TimeColumn = ({ startHour = 8, endHour = 18, hourHeight = 56 }) => {
  const totalHeight = (endHour - startHour) * hourHeight + 48; // 48 = HEADER_HEIGHT
  const formatHour = (hour) => {
    const period = hour >= 12 ? "\nPM" : "\nAM";
    const h = hour % 12 || 12;
    return `${h}:00 ${period}`;
  };
  return (
    <div className="time-column" style={{ height: totalHeight + "px" }}>
      {/* Blank header cell to align with day column headers */}
      <div className="time-column-header" style={{ height: "48px" }} />

      {Array.from({ length: endHour - startHour }).map((_, i) => (
        <div
          key={i}
          className="time-label"
          style={{ height: hourHeight + "px" }}
        >
          {formatHour(startHour + i)}
        </div>
      ))}
    </div>
  );
};

export default TimeColumn;
