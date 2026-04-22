import React from "react";

export default function ConflictCourseList({ courses }) {
  return (
    <div className="flex flex-wrap gap-2 flex-shrink-0">
      {courses.map((c, j) => (
        <span
          key={j}
          className="px-3 py-1 rounded text-xs font-medium text-white"
          style={{ backgroundColor: c.color }}
        >
          {c.subject} {c.number}
        </span>
      ))}
    </div>
  );
}