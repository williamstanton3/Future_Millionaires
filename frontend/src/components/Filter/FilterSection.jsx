// src/components/Filter/FilterSection.jsx
import React, { useState } from "react";
import { Input } from "../ui/input";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../ui/select";
import { Button } from "../ui/button";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];
const formatSemester = (s) => {
  const [year, term] = s.split("_");
  return `${term.charAt(0).toUpperCase() + term.slice(1)} ${year}`;
};

export default function FilterSection({ semesters = [], departments = [], maxCourseNumber = 499, onFilter }) {
  const [semester, setSemester] = useState("");
  const [keyword, setKeyword] = useState("");
  const [department, setDepartment] = useState("");
  const [departmentText, setDepartmentText] = useState("");
  const [courseNumber, setCourseNumber] = useState("");
  const [courseNumberText, setCourseNumberText] = useState("");
  const [professor, setProfessor] = useState("");
  const [selectedDays, setSelectedDays] = useState([]);
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [credits, setCredits] = useState("");

  const toggleDay = (day) => setSelectedDays(prev =>
    prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
  );

  const handleApply = () => {
    onFilter({
      semester,
      keyword,
      department: department || departmentText,
      course_number: courseNumber || courseNumberText,
      professor,
      days: selectedDays,
      start_time: startTime,
      end_time: endTime,
      credits
    });
  };

  return (
    <div className="flex flex-col gap-4 p-4 bg-gray-900 rounded-md shadow-md">
      {/* Semester */}
      <Select onValueChange={setSemester}>
        <SelectTrigger className="w-48">
          <SelectValue placeholder="Select Semester" />
        </SelectTrigger>
        <SelectContent>
          {semesters.map(s => <SelectItem key={s} value={s}>{formatSemester(s)}</SelectItem>)}
        </SelectContent>
      </Select>

      {/* Keyword */}
      <Input placeholder="Keyword" value={keyword} onChange={e => setKeyword(e.target.value)} className="bg-gray-800 text-white placeholder-gray-400 w-full" />

      {/* Department */}
      <div className="flex gap-2 flex-wrap">
        <Select onValueChange={setDepartment}>
          <SelectTrigger className="w-48">
            <SelectValue placeholder="Department" />
          </SelectTrigger>
          <SelectContent>
            {departments.map(d => <SelectItem key={d} value={d}>{d}</SelectItem>)}
          </SelectContent>
        </Select>
        <Input placeholder="Or type department" value={departmentText} onChange={e => setDepartmentText(e.target.value)} className="w-48" />
      </div>

      {/* Course Number */}
      <div className="flex gap-2 flex-wrap">
        <Select onValueChange={setCourseNumber}>
          <SelectTrigger className="w-32">
            <SelectValue placeholder="Course #" />
          </SelectTrigger>
          <SelectContent>
            {Array.from({ length: maxCourseNumber - 99 }, (_, i) => 100 + i).map(n => <SelectItem key={n} value={n}>{n}</SelectItem>)}
          </SelectContent>
        </Select>
        <Input placeholder="Or type course #" value={courseNumberText} onChange={e => setCourseNumberText(e.target.value)} className="w-32" />
      </div>

      {/* Professor */}
      <Input placeholder="Professor" value={professor} onChange={e => setProfessor(e.target.value)} className="w-48" />

      {/* Days + Time */}
      <div className="flex gap-4 flex-wrap items-center">
        <div className="flex gap-2 flex-wrap">
          {DAYS.map(d => (
            <label key={d} className="flex items-center gap-1">
              <input type="checkbox" checked={selectedDays.includes(d)} onChange={() => toggleDay(d)} />
              {d}
            </label>
          ))}
        </div>
        <Input type="time" value={startTime} onChange={e => setStartTime(e.target.value)} className="w-24" />
        <Input type="time" value={endTime} onChange={e => setEndTime(e.target.value)} className="w-24" />
      </div>

      {/* Credits */}
      <Select onValueChange={setCredits}>
        <SelectTrigger className="w-24">
          <SelectValue placeholder="Credits" />
        </SelectTrigger>
        <SelectContent>
          {[1,2,3,4,5].map(c => <SelectItem key={c} value={c}>{c}</SelectItem>)}
        </SelectContent>
      </Select>

      {/* Apply */}
      <Button onClick={handleApply} className="bg-blue-600 hover:bg-blue-700 text-white w-32">Apply</Button>
    </div>
  );
}