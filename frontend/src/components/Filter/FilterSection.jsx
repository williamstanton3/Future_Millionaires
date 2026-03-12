import React, { useState } from "react";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../ui/select";

const subjects = ["Math", "CS", "History", "Biology"]; // example
const professors = ["Smith", "Johnson", "Lee", "Brown"];
const semesters = ["Fall", "Spring", "Summer"];
const days = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function FilterSection({ onFilter }) {
  const [subject, setSubject] = useState("");
  const [professor, setProfessor] = useState("");
  const [semester, setSemester] = useState("");
  const [credits, setCredits] = useState("");
  const [selectedDays, setSelectedDays] = useState([]);

  const handleApply = () => {
    onFilter({
      subject: subject || null,
      professor: professor || null,
      semester: semester || null,
      credits: credits ? parseInt(credits) : 0,
      days: selectedDays.length > 0 ? selectedDays : null,
    });
  };

  const toggleDay = (day) => {
    setSelectedDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
  };

  return (
    <div className="flex flex-col md:flex-row gap-4 bg-gray-900 p-4 rounded-md shadow-md">

      {/* Subject */}
      <Select onValueChange={setSubject}>
        <SelectTrigger className="w-40">
          <SelectValue placeholder="Subject" />
        </SelectTrigger>
        <SelectContent>
          {subjects.map((s) => (
            <SelectItem key={s} value={s}>{s}</SelectItem>
          ))}
        </SelectContent>
      </Select>

      {/* Professor */}
      <Select onValueChange={setProfessor}>
        <SelectTrigger className="w-40">
          <SelectValue placeholder="Professor" />
        </SelectTrigger>
        <SelectContent>
          {professors.map((p) => (
            <SelectItem key={p} value={p}>{p}</SelectItem>
          ))}
        </SelectContent>
      </Select>

      {/* Semester */}
      <Select onValueChange={setSemester}>
        <SelectTrigger className="w-32">
          <SelectValue placeholder="Semester" />
        </SelectTrigger>
        <SelectContent>
          {semesters.map((s) => (
            <SelectItem key={s} value={s}>{s}</SelectItem>
          ))}
        </SelectContent>
      </Select>

      {/* Credits */}
      <Input
        placeholder="Credits"
        type="number"
        value={credits}
        onChange={(e) => setCredits(e.target.value)}
        className="w-20"
      />

      {/* Days */}
      <div className="flex gap-1">
        {days.map((d) => (
          <button
            key={d}
            type="button"
            className={`px-2 py-1 rounded-md text-sm ${
              selectedDays.includes(d) ? "bg-blue-600 text-white" : "bg-gray-700 text-gray-200"
            }`}
            onClick={() => toggleDay(d)}
          >
            {d}
          </button>
        ))}
      </div>

      {/* Apply Button */}
      <Button onClick={handleApply} className="ml-auto bg-blue-600 hover:bg-blue-700 text-white">
        Apply
      </Button>
    </div>
  );
}