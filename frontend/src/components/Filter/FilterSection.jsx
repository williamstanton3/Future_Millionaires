import React, { useState } from "react";
import { Input } from "../ui/input";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../ui/select";
import { Button } from "../ui/button";
import {Combobox,ComboboxInput,ComboboxContent,ComboboxList,ComboboxItem,ComboboxEmpty,} from "../ui/combobox";

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

  const [department, setDepartment] = useState(null);
  const [deptSearch, setDeptSearch] = useState("");

  const [courseNumber, setCourseNumber] = useState(null);
  const [courseSearch, setCourseSearch] = useState("");

  const [professor, setProfessor] = useState(null);
  const [profSearch, setProfSearch] = useState("");

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
      department: department ?? "",
      course_number: courseNumber ?? "",
      professor: professor ?? "",
      days: selectedDays,
      start_time: startTime,
      end_time: endTime,
      credits,
    });
  };

  const filteredDepartments = departments.filter(d =>
    d.toLowerCase().includes((deptSearch ?? "").toLowerCase())
  );

  const filteredCourses = numbers.filter(n =>
    n.toString().toLowerCase().includes((courseSearch ?? "").toLowerCase())
  );

  const filteredProfessors = professors.filter(p =>
    p.toLowerCase().includes((profSearch ?? "").toLowerCase())
  );

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

          {/* Department */}
          <Combobox
            value={department}
            onValueChange={(val) => {
              setDepartment(val);
              setDeptSearch(val);
            }}
          >
            <ComboboxInput
              placeholder="Department"
              value={deptSearch}
              onChange={(e) => {
                setDeptSearch(e.target.value);
                setDepartment(null);
              }}
              showClear={!!department}
              className="w-48"
            />

            <ComboboxContent>
              <ComboboxList>
                {filteredDepartments.length === 0 ? (
                  <ComboboxEmpty>No departments found.</ComboboxEmpty>
                ) : (
                  filteredDepartments.map((d) => (
                    <ComboboxItem key={d} value={d}>
                      {d}
                    </ComboboxItem>
                  ))
                )}
              </ComboboxList>
            </ComboboxContent>
          </Combobox>

          {/* Course Number */}
          <Combobox
            value={courseNumber}
            onValueChange={(val) => {
              setCourseNumber(val);
              setCourseSearch(val);
            }}
          >
            <ComboboxInput
              placeholder="Course #"
              value={courseSearch}
              onChange={(e) => {
                setCourseSearch(e.target.value);
                setCourseNumber(null); // optional but recommended
              }}
              showClear={!!courseNumber}
              className="w-32"
            />
            <ComboboxContent>
              <ComboboxList>
                {filteredCourses.length === 0 ? (
                  <ComboboxEmpty>No courses found.</ComboboxEmpty>
                ) : (
                  filteredCourses.map((n) => (
                    <ComboboxItem key={n} value={n}>
                      {n}
                    </ComboboxItem>
                  ))
                )}
              </ComboboxList>
            </ComboboxContent>
          </Combobox>

          {/* Professor */}
          <Combobox
            value={professor}
            onValueChange={(val) => {
              setProfessor(val);
              setProfSearch(val);
            }}
          >
            <ComboboxInput
              placeholder="Professor"
              value={profSearch}
              onChange={(e) => {
                setProfSearch(e.target.value);
                setProfessor(null);
              }}
              showClear={!!professor}
              className="w-48"
            />

            <ComboboxContent>
              <ComboboxList>
                {filteredProfessors.length === 0 ? (
                  <ComboboxEmpty>No professors found.</ComboboxEmpty>
                ) : (
                  filteredProfessors.map((p) => (
                    <ComboboxItem key={p} value={p}>
                      {p}
                    </ComboboxItem>
                  ))
                )}
              </ComboboxList>
            </ComboboxContent>
          </Combobox>

          {/* Days + Time */}
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

          {/* Credits */}
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
