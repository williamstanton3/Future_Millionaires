import React from "react";

export default function TimeColumn() {
  const hours = [];
  for (let h = 8; h <= 20; h++) {
    const ampm = h < 12 ? "AM" : "PM";
    const displayHour = h > 12 ? h - 12 : h;
    hours.push(`${displayHour}:00 ${ampm}`);
  }

  return (
    <div className="time-column">
      {/* Spacer to match day-column header */}
      <div className="day-column-header-spacer" />
      
      {hours.map((time, idx) => (
        <div key={idx} className="time-label">
          {time}
        </div>
      ))}
    </div>
  );
}