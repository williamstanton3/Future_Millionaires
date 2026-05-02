import React from "react";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

const hexToRgb = (hex) => {
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  return `${r},${g},${b}`;
};

export default function ConflictSchedule({ courses, credits }) {
  const toHour = (time) => {
    const [h, m] = time.split(":").map(Number);
    return h + m / 60;
  };

  const formatHour = (hour) => {
    const ampm = hour < 12 ? "AM" : "PM";
    const display = hour % 12 === 0 ? 12 : hour % 12;
    return `${display}:00 ${ampm}`;
  };

  return (
    <>
      {/* Info bar */}
      <div className="flex-shrink-0 text-sm text-gray-400">
        {courses.length} course{courses.length !== 1 ? "s" : ""} · {credits} credits
      </div>

      {/* SCHEDULE GRID */}
      <div className="h-[50vh] border border-gray-700 rounded-xl bg-gray-950 overflow-hidden grid grid-cols-[70px_repeat(5,1fr)]">
        {/* Time labels */}
        <div className="border-r border-gray-700 flex flex-col text-[12px] text-gray-500">
          {/* Spacer matching the h-7 day column header */}
          <div className="h-7 flex-shrink-0 border-b border-gray-700" />
          {Array.from({ length: 13 }, (_, i) => {
            const hour = 8 + i;
            return (
              <div
                key={i}
                className="flex-1 flex items-center justify-center pr-2 border-b border-gray-800 last:border-b-0"
              >
                {formatHour(hour)}
              </div>
            );
          })}
        </div>

        {/* Day columns */}
        {DAYS.map((day) => (
          <div
            key={day}
            className="relative border-r border-gray-700 last:border-r-0 flex flex-col"
          >
            <div className="h-7 bg-gray-900 border-b border-gray-700 flex items-center justify-center text-xs font-semibold text-gray-400">
              {day}
            </div>

            <div className="flex-1 relative">
              {courses.flatMap((c, cIdx) =>
                c.times
                  .filter((t) => t.day === day)
                  .map((t, tIdx) => {
                    const startHour = toHour(t.start);
                    const endHour = toHour(t.end);

                    const startPct = Math.max(0, ((startHour - 8) / 12) * 100);
                    const heightPct = Math.max(6, ((endHour - startHour) / 12) * 100);

                    return (
                      <div
                        key={`${cIdx}-${tIdx}`}
                        className="absolute left-2 right-2 rounded-md text-white text-[12px] px-2 py-1 overflow-hidden flex flex-col justify-center"
                        style={{
                          top: `${startPct}%`,
                          height: `${heightPct}%`,
                          backgroundColor: `rgba(${hexToRgb(c.color)}, 0.18)`,
                          borderLeft: `3px solid ${c.color}`,
                          minHeight: "22px",
                        }}
                      >
                        <div className="font-medium leading-none truncate" style={{ color: c.color }}>
                          {c.subject} {c.number}
                        </div>
                      </div>
                    );
                  })
              )}
            </div>
          </div>
        ))}
      </div>
    </>
  );
}