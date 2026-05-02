import React, { useState } from "react";
import { Button } from "../ui/button";

import SemesterSelect from "./filters/SemesterSelect";
import KeywordInput from "./filters/KeywordInput";
import DepartmentCombobox from "./filters/DepartmentCombobox";
import CourseNumberCombobox from "./filters/CourseNumberCombobox";
import ProfessorCombobox from "./filters/ProfessorCombobox";
import DayTimeFilter from "./filters/DayTimeFilter";
import CreditsSelect from "./filters/CreditsSelect";

export default function FilterSection({
  semesters = [],
  departments = [],
  professors = [],
  numbers = [],
  creditOptions = [],
  activeSemester,
  onSemesterChange,
  onFilter,
}) {
  const [keyword, setKeyword] = useState("");
  const [department, setDepartment] = useState(null);
  const [courseNumber, setCourseNumber] = useState(null);
  const [professor, setProfessor] = useState(null);
  const [selectedDays, setSelectedDays] = useState([]);
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [credits, setCredits] = useState("");

  const locked = !activeSemester;

  const handleApply = () => {
      console.log("credits value:", credits, "type:", typeof credits);
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
    <>
      {/* Row 1: Semester */}
      <div className="w-48">
        <SemesterSelect
          semesters={semesters}
          activeSemester={activeSemester}
          onSemesterChange={onSemesterChange}
        />
      </div>

      {/* Row 2: Filters + Row 3: Apply Button */}
      <div className={locked ? "opacity-40 pointer-events-none select-none" : ""}>
        {/* Filters row */}
        <div className="flex flex-wrap gap-4 items-end">
          <div className="w-48">
            <KeywordInput keyword={keyword} onChange={setKeyword} />
          </div>

          <div className="w-32">
            <DepartmentCombobox
              departments={departments}
              value={department}
              onChange={setDepartment}
            />
          </div>

          <div className="w-32">
            <CourseNumberCombobox
              numbers={numbers}
              value={courseNumber}
              onChange={setCourseNumber}
            />
          </div>

          <div className="w-48">
            <ProfessorCombobox
              professors={professors}
              value={professor}
              onChange={setProfessor}
            />
          </div>

          <div className="w-32">
            <CreditsSelect
              creditOptions={creditOptions}
              value={credits}
              onChange={setCredits}
            />
          </div>

          <div className="flex-shrink-0">
            <DayTimeFilter
              selectedDays={selectedDays}
              setSelectedDays={setSelectedDays}
              startTime={startTime}
              onStartTimeChange={setStartTime}
              endTime={endTime}
              onEndTimeChange={setEndTime}
            />
          </div>
        </div>

        {/* New row: Apply button (left-justified) */}
        <div className="mt-6 flex justify-start">
          <Button
            onClick={handleApply}
            className="bg-blue-600 hover:bg-blue-700 text-white w-32"
          >
            Apply
          </Button>
        </div>
      </div>
    </>
  );
}