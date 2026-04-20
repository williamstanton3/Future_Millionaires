import React from "react";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../../ui/select";

const DAYS = [
  { value: "M", label: "Mon" },
  { value: "T", label: "Tue" },
  { value: "W", label: "Wed" },
  { value: "R", label: "Thu" },
  { value: "F", label: "Fri" },
];

const TIME_OPTIONS = [
  "08:00", "08:30", "09:00", "09:30",
  "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
  "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
  "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
  "19:00", "19:30", "20:00", "20:30", "21:00"
];

function formatTimeLabel(time) {
  const [hour, min] = time.split(":").map(Number);
  const h = hour % 12 === 0 ? 12 : hour % 12;
  const ampm = hour < 12 ? "AM" : "PM";
  return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
}

export default function DayTimeFilter({ selectedDays, onToggleDay, startTime, onStartTimeChange, endTime, onEndTimeChange }) {
  return (
    <div className="flex flex-col gap-2">
      <div className="flex gap-2 flex-wrap">
        {DAYS.map(d => (
          <label key={d.value} className="flex items-center gap-1 text-white">
            <input
              type="checkbox"
              checked={selectedDays.includes(d.value)}
              onChange={() => onToggleDay(d.value)}
            />
            {d.label}
          </label>
        ))}
      </div>
      <div className="flex gap-2 items-center">
        <span className="text-sm text-gray-400">Time Range:</span>
        <Select value={startTime} onValueChange={(val) => onStartTimeChange(val === "all" ? "" : val)}>
          <SelectTrigger className="w-32"><SelectValue placeholder="Start Time" /></SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Any</SelectItem>
            {TIME_OPTIONS.map(t => <SelectItem key={t} value={t}>{formatTimeLabel(t)}</SelectItem>)}
          </SelectContent>
        </Select>
        <span className="text-gray-400">-</span>
        <Select value={endTime} onValueChange={(val) => onEndTimeChange(val === "all" ? "" : val)}>
          <SelectTrigger className="w-32"><SelectValue placeholder="End Time" /></SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Any</SelectItem>
            {TIME_OPTIONS.map(t => <SelectItem key={t} value={t}>{formatTimeLabel(t)}</SelectItem>)}
          </SelectContent>
        </Select>
      </div>
    </div>
  );
}
