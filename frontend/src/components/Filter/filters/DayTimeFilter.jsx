import React from "react";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../../ui/select";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";

const DAYS = ["M", "T", "W", "R", "F"];

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

export default function DayTimeFilter({
  selectedDays,
  setSelectedDays,
  startTime,
  onStartTimeChange,
  endTime,
  onEndTimeChange
}) {
  return (
    <div className="flex flex-row gap-2">

      {/* Days */}
      <ToggleGroup
        type="multiple"
        value={selectedDays}
        onValueChange={(val) => setSelectedDays(val)}
        variant="outline"
        size="sm"
        className="gap-1"
      >
        {DAYS.map((d) => (
          <ToggleGroupItem
            key={d}
            value={d}
            className="data-[state=on]:bg-blue-600 data-[state=on]:text-white">
            {d}
          </ToggleGroupItem>
        ))}
      </ToggleGroup>

      {/* Time */}
      <div className="flex gap-2 items-center">
        <span className="text-sm text-gray-400">Time:</span>

        <div className="w-full">
          <Select
            value={startTime}
            onValueChange={(val) => onStartTimeChange(val === "all" ? "" : val)}
          >
            <SelectTrigger className="w-32">
              <SelectValue placeholder="Start" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Any</SelectItem>
              {TIME_OPTIONS.map(t => (
                <SelectItem key={t} value={t}>
                  {formatTimeLabel(t)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <span className="text-gray-400">-</span>

        <div className="w-full">
          <Select
            value={endTime}
            onValueChange={(val) => onEndTimeChange(val === "all" ? "" : val)}
          >
            <SelectTrigger className="w-32">
              <SelectValue placeholder="End" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Any</SelectItem>
              {TIME_OPTIONS.map(t => (
                <SelectItem key={t} value={t}>
                  {formatTimeLabel(t)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

    </div>
  );
}