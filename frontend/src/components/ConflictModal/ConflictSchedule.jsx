import React from "react";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function ConflictSchedule({ courses, credits }) {
  const toHour = (time) => {
    const [h, m] = time.split(":").map(Number);
    return h + m / 60;
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
        <div className="border-r border-gray-700 flex flex-col text-[10px] text-gray-500 relative">
          {Array.from({ length: 13 }, (_, i) => {
            const hour = 8 + i;
            return (
              <div
                key={i}
                className="flex-1 flex items-start justify-end pr-2 border-b border-gray-800 last:border-b-0"
              >
                <span className="translate-y-[-4px]">
                  {hour}:00
                </span>
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
                        className="absolute left-2 right-2 rounded-md text-white text-[10px] px-2 py-1 shadow-md overflow-hidden flex flex-col justify-center"
                        style={{
                          top: `${startPct}%`,
                          height: `${heightPct}%`,
                          backgroundColor: c.color,
                          minHeight: "22px",
                        }}
                      >
                        <div className="font-medium leading-none truncate">
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