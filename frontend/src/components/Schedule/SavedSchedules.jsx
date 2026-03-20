import React from "react";
import { Button } from "../ui/button";

export default function SavedSchedules({ schedules, onLoad, onDelete }) {
  const entries = Object.entries(schedules);

  if (entries.length === 0) return null;

  return (
    <div className="bg-gray-800 rounded-md p-4 text-white">
      <h2 className="text-xl font-bold mb-4">Saved Schedules</h2>
      <div className="flex flex-col gap-2">
        {entries.map(([semester, schedule]) => (
          <div key={semester} className="flex items-center justify-between bg-gray-700 rounded-md p-3">
            <div>
              <div className="font-medium">{semester}</div>
              <div className="text-sm text-gray-400">
                {schedule.schedule?.length ?? 0} course(s) · {schedule.credits} credits
              </div>
            </div>
            <div className="flex gap-2">
              <Button
                className="bg-blue-600 hover:bg-blue-700 text-white text-sm"
                onClick={() => onLoad(semester, schedule)}
              >
                Load
              </Button>
              <Button
                className="bg-red-600 hover:bg-red-700 text-white text-sm"
                onClick={() => onDelete(semester)}
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