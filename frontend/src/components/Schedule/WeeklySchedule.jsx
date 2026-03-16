// src/components/Schedule/WeeklySchedule.jsx
import React, { useState } from "react";
import TimeColumn from "./TimeColumn";
import DayColumn from "./DayColumn";
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

const formatTime = (timeArr) => {
  if (!Array.isArray(timeArr)) return timeArr ?? "";
  const [hour, min] = timeArr;
  const h = hour % 12 === 0 ? 12 : hour % 12;
  const ampm = hour < 12 ? "AM" : "PM";
  return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
};

export default function WeeklySchedule({ courses, onRemoveCourse }) {
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

      <div className="schedule-wrapper">
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

      {/* Course detail + remove dialog */}
      <Dialog open={!!selected} onOpenChange={(open) => !open && setSelected(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {selected?.subject} {selected?.number} {selected?.section} — {selected?.name}
            </DialogTitle>
            <DialogDescription>
              <div className="mt-2 text-sm">Professor: {selected?.faculty}</div>
              <div className="text-sm">Semester: {selected?.semester}</div>
              <div className="text-sm">Credits: {selected?.credits}</div>
              <div className="text-sm">Location: {selected?.location}</div>
              <div className="text-sm">Open Seats: {selected?.open_seats}</div>
              <div className="text-sm">Total Seats: {selected?.total_seats}</div>

              <div className="mt-2 text-sm font-semibold">Meetings:</div>
              {selected?.times.map((m, i) => (
                <div key={i} className="text-sm">
                  {m.day} {formatTime(m.start_time)} – {formatTime(m.end_time)}
                </div>
              ))}

              {selected?.description && (
                <div className="mt-2 text-sm">{selected.description}</div>
              )}
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