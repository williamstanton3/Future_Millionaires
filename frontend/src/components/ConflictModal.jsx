import React, { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import TimeColumn from "./Schedule/TimeColumn";
import DayColumn from "./Schedule/DayColumn";
import "./Schedule/Schedule.css";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

// Normalize backend course format into the shape DayColumn expects
function normalizeCourse(course) {
  const times = (course.times ?? []).map((t) => {
    // Backend may send times as { day, start: [h, m], end: [h, m] }
    // or already as { day, start: "HH:MM", end: "HH:MM" }
    const toStr = (v) =>
      Array.isArray(v)
        ? `${String(v[0]).padStart(2, "0")}:${String(v[1]).padStart(2, "0")}`
        : v;
    return { day: t.day, start: toStr(t.start), end: toStr(t.end) };
  });
  return { ...course, times, color: course.color ?? "#3b82f6" };
}

export default function ConflictModal({ open, message, suggestedSchedules, onAccept, onClose }) {
  const [selectedIdx, setSelectedIdx] = useState(0);

  if (!suggestedSchedules || suggestedSchedules.length === 0) return null;

  const current = suggestedSchedules[selectedIdx];
  const courses = (current?.schedule ?? []).map(normalizeCourse);

  const handleAccept = () => {
    onAccept(suggestedSchedules[selectedIdx]);
    onClose();
  };

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-4xl w-full bg-gray-900 text-white border border-gray-700">
        <DialogHeader>
          <DialogTitle className="text-red-400 text-lg">Time Conflict Detected</DialogTitle>
          <DialogDescription className="text-gray-300">
            {message} Here are {suggestedSchedules.length} suggested alternative schedule{suggestedSchedules.length !== 1 ? "s" : ""} that resolve the conflict.
          </DialogDescription>
        </DialogHeader>

        {/* Suggestion tabs */}
        {suggestedSchedules.length > 1 && (
          <div className="flex gap-2 flex-wrap mt-1">
            {suggestedSchedules.map((_, i) => (
              <button
                key={i}
                onClick={() => setSelectedIdx(i)}
                className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
                  i === selectedIdx
                    ? "bg-blue-600 text-white"
                    : "bg-gray-700 text-gray-300 hover:bg-gray-600"
                }`}
              >
                Option {i + 1}
              </button>
            ))}
          </div>
        )}

        {/* Credits summary */}
        <div className="text-sm text-gray-400 -mb-1">
          {courses.length} course{courses.length !== 1 ? "s" : ""} · {current?.credits ?? 0} credits
        </div>

        {/* Schedule grid */}
        <div className="schedule-wrapper rounded-md overflow-hidden border border-gray-700">
          <div className="schedule" style={{ maxHeight: "420px", overflowY: "auto" }}>
            <TimeColumn />
            {DAYS.map((day) => (
              <DayColumn
                key={day}
                day={day}
                courses={courses}
                onCourseClick={() => {}}
              />
            ))}
          </div>
        </div>

        {/* Course list for this suggestion */}
        <div className="flex flex-col gap-1 max-h-32 overflow-y-auto">
          {courses.map((c, i) => (
            <div key={i} className="flex items-center gap-2 text-sm text-gray-300">
              <span
                className="inline-block w-2.5 h-2.5 rounded-full flex-shrink-0"
                style={{ backgroundColor: c.color }}
              />
              <span className="font-medium">{c.subject} {c.number} {c.section}</span>
              <span className="text-gray-500">—</span>
              <span>{c.name}</span>
            </div>
          ))}
        </div>

        {/* Actions */}
        <div className="flex justify-end gap-3 pt-1">
          <Button variant="outline" onClick={onClose} className="border-gray-600 text-gray-300 hover:bg-gray-800">
            Cancel
          </Button>
          <Button onClick={handleAccept} className="bg-blue-600 hover:bg-blue-700 text-white">
            Accept This Schedule
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
