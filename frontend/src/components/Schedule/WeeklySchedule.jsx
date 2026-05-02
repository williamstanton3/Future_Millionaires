// src/components/Schedule/WeeklySchedule.jsx
import React, { useState } from "react";
import TimeColumn from "./TimeColumn";
import DayColumn from "./DayColumn";
import SavedSchedulesSidebar from "./SavedSchedulesSidebar";
import "./Schedule.css";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

const formatTime = (timeValue) => {
  if (typeof timeValue === "string") {
    const [hour, min] = timeValue.split(":").map(Number);
    const h = hour % 12 === 0 ? 12 : hour % 12;
    const ampm = hour < 12 ? "AM" : "PM";
    return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
  }
  if (Array.isArray(timeValue)) {
    const [hour, min] = timeValue;
    const h = hour % 12 === 0 ? 12 : hour % 12;
    const ampm = hour < 12 ? "AM" : "PM";
    return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
  }
  return "";
};

const formatSemester = (semester) => {
  if (!semester) return "";
  const parts = semester.split("_");
  const year = parts[0];
  const term = parts.slice(1).join(" ");
  return `${term} ${year}`;
};

export default function WeeklySchedule({
  courses,
  onRemoveCourse,
  savedSchedules = {},
  onLoadSchedule,
  onDeleteSchedule,
}) {
  const [selected, setSelected] = useState(null);

  const handleRemove = () => {
    onRemoveCourse(selected);
    setSelected(null);
  };

  return (
    <div className="planner-root">
      <div>
        <p className="planner-eyebrow">Grove City College</p>
        <h1 className="planner-title">Schedule Planner</h1>
      </div>

      <div className="flex gap-4 items-start">
        {/* Weekly grid */}
        <div className="schedule-wrapper flex-1 min-w-0">
          <div className="schedule">
            <TimeColumn />
            {DAYS.map((day) => (
              <DayColumn
                key={day}
                day={day}
                courses={courses}
                onCourseClick={setSelected}
              />
            ))}
          </div>
        </div>

        {/* Saved schedules sidebar */}
        <SavedSchedulesSidebar
          savedSchedules={savedSchedules}
          onLoad={onLoadSchedule}
          onDelete={onDeleteSchedule}
        />
      </div>

      {/* Course detail + remove dialog */}
      <Dialog open={!!selected} onOpenChange={(open) => !open && setSelected(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {selected?.subject} {selected?.number} {selected?.section} — {selected?.name}
            </DialogTitle>
            <DialogDescription asChild>
              <div className="text-sm text-muted-foreground">
                <div className="mt-3 space-y-3">
                  {(selected?.professors || []).map((prof, i) => (
                    <div key={i} className="flex items-center gap-3">
                      <img
                        src={prof.imageUrl || "/default-prof.png"}
                        alt={prof.name}
                        className="w-16 h-16 rounded-full object-cover flex-shrink-0"
                      />
                      <div>
                        <div className="font-medium">{prof.name}</div>
                        <div className="flex items-center gap-2 mt-1 text-xs">
                          <span className="bg-blue-700 text-white px-2 py-0.5 rounded font-bold">
                            {prof.overallRating > 0 ? prof.overallRating.toFixed(1) : "N/A"}
                          </span>
                          <span className="text-gray-400">Rating</span>
                          <span className="bg-orange-700 text-white px-2 py-0.5 rounded font-bold ml-2">
                            {prof.avgDifficulty > 0 ? prof.avgDifficulty.toFixed(1) : "N/A"}
                          </span>
                          <span className="text-gray-400">Difficulty</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="text-sm">Semester: {formatSemester(selected?.semester)}</div>
                <div className="text-sm">Credits: {selected?.credits}</div>
                <div className="text-sm">Location: {selected?.location}</div>
                <div className="text-sm">Open Seats: {selected?.open_seats}</div>
                <div className="text-sm">Total Seats: {selected?.total_seats}</div>

                <div className="mt-2 text-sm font-semibold">Meetings:</div>
                {selected?.times.map((m, i) => (
                  <div key={i} className="text-sm">
                    {m.day} {formatTime(m.start)} – {formatTime(m.end)}
                  </div>
                ))}

                {selected?.description && (
                  <div className="mt-2 text-sm">{selected.description}</div>
                )}
              </div>
            </DialogDescription>
          </DialogHeader>

          <div className="mt-4 flex justify-between">
            <Button
              className="bg-red-600 hover:bg-red-700 text-white"
              onClick={handleRemove}
            >
              Remove from Schedule
            </Button>
            <Button variant="outline" onClick={() => setSelected(null)}>
              Close
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
