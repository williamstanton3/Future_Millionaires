import React from "react";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../../ui/select";

export default function SemesterSelect({ semesters = [], activeSemester, onSemesterChange }) {
  return (
    <div>
      <p className="text-sm text-gray-400 mb-1">Select a semester to begin</p>
      <Select value={activeSemester} onValueChange={onSemesterChange}>
        <SelectTrigger className="w-full">
          <SelectValue placeholder="Select Semester" />
        </SelectTrigger>
        <SelectContent>
          {semesters.map(s => (
            <SelectItem key={s.value} value={s.value}>{s.label}</SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}
