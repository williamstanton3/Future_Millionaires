import React, { useState } from "react";
import { Button } from "../ui/button";

import SemesterSelect from "./filters/SemesterSelect";
import KeywordInput from "./filters/KeywordInput";
import DepartmentCombobox from "./filters/DepartmentCombobox";
import CourseNumberCombobox from "./filters/CourseNumberCombobox";
import ProfessorCombobox from "./filters/ProfessorCombobox";
import DayTimeFilter from "./filters/DayTimeFilter";
import CreditsSelect from "./filters/CreditsSelect";

export default function FilterSection({ semesters = [], departments = [], professors = [], numbers = [], creditOptions = [], activeSemester, onSemesterChange, onFilter }) {
  const [keyword, setKeyword] = useState("");
  const [department, setDepartment] = useState(null);
  const [courseNumber, setCourseNumber] = useState(null);
  const [professor, setProfessor] = useState(null);
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

  return (
    <div className="flex flex-col gap-4 p-4 bg-gray-900 rounded-md shadow-md">

      <SemesterSelect
        semesters={semesters}
        activeSemester={activeSemester}
        onSemesterChange={onSemesterChange}
      />

      <div className={locked ? "opacity-40 pointer-events-none select-none" : ""}>
        <div className="flex flex-col gap-4">

          <KeywordInput keyword={keyword} onChange={setKeyword} />

          <DepartmentCombobox
            departments={departments}
            value={department}
            onChange={setDepartment}
          />

          <CourseNumberCombobox
            numbers={numbers}
            value={courseNumber}
            onChange={setCourseNumber}
          />

          <ProfessorCombobox
            professors={professors}
            value={professor}
            onChange={setProfessor}
          />

          <DayTimeFilter
            selectedDays={selectedDays}
            onToggleDay={toggleDay}
            startTime={startTime}
            onStartTimeChange={setStartTime}
            endTime={endTime}
            onEndTimeChange={setEndTime}
          />

          <CreditsSelect
            creditOptions={creditOptions}
            value={credits}
            onChange={setCredits}
          />

          <Button onClick={handleApply} className="bg-blue-600 hover:bg-blue-700 text-white w-32">
            Apply
          </Button>

        </div>
      </div>
    </div>
  );
}
