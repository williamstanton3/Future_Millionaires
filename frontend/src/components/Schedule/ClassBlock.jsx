import React from "react";

const HEADER_HEIGHT = 48;

const hexToRgb = (hex) => {
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  return `${r},${g},${b}`;
};

const formatTime = (timeStr) => {
  const [hour, minute] = timeStr.split(":").map(Number);
  const period = hour >= 12 ? "PM" : "AM";
  const h = hour % 12 || 12;
  return `${h}:${String(minute).padStart(2, "0")} ${period}`;
};

export default function ClassBlock({ course, top, height, }) {
  const rgb = hexToRgb(course.color);

  return (
    <div
      className="class-block"
      style={{
        top: top + HEADER_HEIGHT + "px",
        height: height - 3 + "px",
        backgroundColor: `rgba(${rgb}, 0.18)`,
        borderLeftColor: course.color,
      }}
    >
      <div className="class-block-name" style={{ color: course.color }}>
        {course.name}
      </div>
      {height > 30 && (
        <div className="class-block-time">
          {formatTime(course.start)} – {formatTime(course.end)}
        </div>
      )}
      {height > 50 && (
        <div className="class-block-room">{course.room}</div>
      )}
    </div>
  );
}
