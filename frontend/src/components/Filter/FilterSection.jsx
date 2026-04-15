import React, { useState } from "react";
import { Input } from "../ui/input";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../ui/select";
import { Button } from "../ui/button";

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

export default function FilterSection({ semesters = [], departments = [], professors = [], numbers = [], creditOptions = [], activeSemester, onSemesterChange, onFilter }) {
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

  const locked = !activeSemester;

  const toggleDay = (day) => setSelectedDays(prev =>
    prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
  );

  const handleApply = () => {
    onFilter({
      semester: activeSemester,
      keyword,
      department: department || departmentText,
      course_number: courseNumber || courseNumberText,
      professor,
      days: selectedDays,
      start_time: startTime,
      end_time: endTime,
      credits,
    });
  };

  const formatTimeLabel = (time) => {
    const [hour, min] = time.split(":").map(Number);
    const h = hour % 12 === 0 ? 12 : hour % 12;
    const ampm = hour < 12 ? "AM" : "PM";
    return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
  };

  return (
    <div className="flex flex-col gap-4 p-4 bg-gray-900 rounded-md shadow-md">

      {/* Semester — always enabled, required first */}
      <div>
        <p className="text-sm text-gray-400 mb-1">Select a semester to begin</p>
        <Select value={activeSemester} onValueChange={onSemesterChange}>
          <SelectTrigger className="w-48">
            <SelectValue placeholder="Select Semester" />
          </SelectTrigger>
          <SelectContent>
            {semesters.map(s => (
              <SelectItem key={s.value} value={s.value}>{s.label}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Everything below is disabled until a semester is chosen */}
      <div className={locked ? "opacity-40 pointer-events-none select-none" : ""}>

        <div className="flex flex-col gap-4">
          <Input
            placeholder="Keyword"
            value={keyword}
            onChange={e => setKeyword(e.target.value)}
            className="bg-gray-800 text-white placeholder-gray-400 w-96"
          />

          <div className="flex gap-2 flex-wrap">
            <Select value={department} onValueChange={(val) => {setDepartment(val === "all" ? "" : val); setDepartmentText("");} }>
              <SelectTrigger className="w-48">
                <SelectValue placeholder="Department" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Departments</SelectItem>
                {departments.map(d => <SelectItem key={d} value={d}>{d}</SelectItem>)}
              </SelectContent>
            </Select>
            <Input
              placeholder="Or type department"
              value={departmentText}
              onChange={e => {setDepartmentText(e.target.value); setDepartment("");} }
              className="w-48"
            />
          </div>

          <div className="flex gap-2 flex-wrap">
            <Select value={courseNumber} onValueChange={ (val) => {setCourseNumber(val === "all" ? "" : val); setCourseNumberText("");} }>
              <SelectTrigger className="w-32">
                <SelectValue placeholder="Course #" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All</SelectItem>
                {numbers.map(n => (
                  <SelectItem key={n} value={n}>{n}</SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Input
              placeholder="Or type course #"
              value={courseNumberText}
              onChange={e => {setCourseNumberText(e.target.value); setCourseNumber("");} }
              className="w-32"
            />
          </div>

          <Select value={professor} onValueChange={(val) => setProfessor(val === "all" ? "" : val)}>
            <SelectTrigger className="w-48">
              <SelectValue placeholder="Professor" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Any</SelectItem>
              {professors.map(p => <SelectItem key={p} value={p}>{p}</SelectItem>)}
            </SelectContent>
          </Select>

          <div className="flex flex-col gap-2">
            <div className="flex gap-2 flex-wrap">
              {DAYS.map(d => (
                <label key={d.value} className="flex items-center gap-1 text-white">
                  <input type="checkbox" checked={selectedDays.includes(d.value)} onChange={() => toggleDay(d.value)} />
                  {d.label}
                </label>
              ))}
            </div>
            <div className="flex gap-2 items-center">
              <span className="text-sm text-gray-400">Time Range:</span>
              <Select value={startTime} onValueChange={(val) => setStartTime(val === "all" ? "" : val)}>
                <SelectTrigger className="w-32"><SelectValue placeholder="Start Time" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Any</SelectItem>
                  {TIME_OPTIONS.map(t => <SelectItem key={t} value={t}>{formatTimeLabel(t)}</SelectItem>)}
                </SelectContent>
              </Select>
              <span className="text-gray-400">-</span>
              <Select value={endTime} onValueChange={(val) => setEndTime(val === "all" ? "" : val)}>
                <SelectTrigger className="w-32"><SelectValue placeholder="End Time" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Any</SelectItem>
                  {TIME_OPTIONS.map(t => <SelectItem key={t} value={t}>{formatTimeLabel(t)}</SelectItem>)}
                </SelectContent>
              </Select>
            </div>
          </div>

          <Select value={credits} onValueChange={(val) => setCredits(val === "all" ? "" : val)}>
            <SelectTrigger className="w-24">
              <SelectValue placeholder="Credits" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Any</SelectItem>
              {creditOptions.map(c => <SelectItem key={c} value={c}>{c}</SelectItem>)}
            </SelectContent>
          </Select>

          <Button onClick={handleApply} className="bg-blue-600 hover:bg-blue-700 text-white w-32">
            Apply
          </Button>
        </div>
      </div>
    </div>
  );
}