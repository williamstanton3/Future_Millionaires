// src/components/Schedule/SavedSchedulesSidebar.jsx
import React from "react";
import { Button } from "@/components/ui/button";

const formatSemester = (semester) => {
  if (!semester) return "";
  const parts = semester.split("_");
  const year = parts[0];
  const term = parts.slice(1).join(" ");
  return `${term} ${year}`;
};

export default function SavedSchedulesSidebar({ savedSchedules = {}, onLoad, onDelete }) {
  const entries = Object.entries(savedSchedules);

  if (entries.length === 0) return null;

  return (
    <div className="w-64 shrink-0 bg-gray-800 rounded-md p-4 text-white self-start">
      <h2 className="text-base font-bold mb-3 text-gray-100">Saved Schedules</h2>
      <div className="flex flex-col gap-2">
        {entries.map(([semester, schedule]) => (
          <div key={semester} className="bg-gray-700 rounded-md p-3 flex flex-col gap-2">
            <div>
              <div className="font-medium text-sm text-white leading-tight">
                {formatSemester(semester)}
              </div>
              <div className="text-xs text-gray-400 mt-0.5">
                {schedule.schedule?.length ?? 0} course(s) · {schedule.credits} credits
              </div>
            </div>
            <div className="flex gap-1.5">
              <Button
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white text-xs h-7 px-2"
                onClick={() => onLoad?.(semester, schedule)}
              >
                Load
              </Button>
              <Button
                className="flex-1 bg-red-600 hover:bg-red-700 text-white text-xs h-7 px-2"
                onClick={() => onDelete?.(semester)}
              >
                Delete
              </Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
