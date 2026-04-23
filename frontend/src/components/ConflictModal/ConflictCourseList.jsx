import React from "react";

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

export default function ConflictCourseList({ courses }) {
  return (
    <div className="flex flex-row gap-2 flex-shrink-0">
      {courses.map((c, j) => (
        <div
          key={j}
          className="rounded-md px-3 py-2 text-xs"
          style={{
            backgroundColor: `rgba(${hexToRgb(c.color)}, 0.12)`,
            borderLeft: `3px solid ${c.color}`,
          }}
        >
          {/* Course title row */}
          <div className="font-semibold text-sm" style={{ color: c.color }}>
            {c.subject} {c.number} {c.section} — {c.name}
          </div>

          {/* Professor */}
          {c.professors?.[0] && (
            <div className="text-gray-400 mt-0.5">
              {c.professors[0].name}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
