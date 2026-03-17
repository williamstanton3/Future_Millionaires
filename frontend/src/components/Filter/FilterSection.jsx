import React, { useState } from "react";
import { Input } from "../ui/input";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../ui/select";
import { Button } from "../ui/button";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function FilterSection({ semesters = [], departments = [], maxCourseNumber = 499, creditOptions = [], activeSemester, onSemesterChange, onFilter }) {
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
            className="bg-gray-800 text-white placeholder-gray-400 w-full"
          />

          <div className="flex gap-2 flex-wrap">
            <Select onValueChange={setDepartment}>
              <SelectTrigger className="w-48">
                <SelectValue placeholder="Department" />
              </SelectTrigger>
              <SelectContent>
                {departments.map(d => <SelectItem key={d} value={d}>{d}</SelectItem>)}
              </SelectContent>
            </Select>
            <Input
              placeholder="Or type department"
              value={departmentText}
              onChange={e => setDepartmentText(e.target.value)}
              className="w-48"
            />
          </div>

          <div className="flex gap-2 flex-wrap">
            <Select onValueChange={setCourseNumber}>
              <SelectTrigger className="w-32">
                <SelectValue placeholder="Course #" />
              </SelectTrigger>
              <SelectContent>
                {Array.from({ length: maxCourseNumber - 99 }, (_, i) => 100 + i).map(n => (
                  <SelectItem key={n} value={n}>{n}</SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Input
              placeholder="Or type course #"
              value={courseNumberText}
              onChange={e => setCourseNumberText(e.target.value)}
              className="w-32"
            />
          </div>

          <Input
            placeholder="Professor"
            value={professor}
            onChange={e => setProfessor(e.target.value)}
            className="w-48"
          />

          <div className="flex gap-4 flex-wrap items-center">
            <div className="flex gap-2 flex-wrap">
              {DAYS.map(d => (
                <label key={d} className="flex items-center gap-1 text-white">
                  <input type="checkbox" checked={selectedDays.includes(d)} onChange={() => toggleDay(d)} />
                  {d}
                </label>
              ))}
            </div>
            <Input type="time" value={startTime} onChange={e => setStartTime(e.target.value)} className="w-24" />
            <Input type="time" value={endTime} onChange={e => setEndTime(e.target.value)} className="w-24" />
          </div>

          <Select onValueChange={setCredits}>
            <SelectTrigger className="w-24">
              <SelectValue placeholder="Credits" />
            </SelectTrigger>
            <SelectContent>
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