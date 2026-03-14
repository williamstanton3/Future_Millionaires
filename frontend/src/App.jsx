// src/App.jsx
import React, { useState } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";

// Mock data
import { semesters, departments } from "./data/mockFilters";
import { courses as allCourses } from "./data/mockCourses";

export default function App() {
  const [filteredCourses, setFilteredCourses] = useState(allCourses);

  const handleFilter = (filters) => {
    // Simple local filter
    const results = allCourses.filter((course) => {
      if (filters.keyword && !course.name.toLowerCase().includes(filters.keyword.toLowerCase())) return false;
      if (filters.department && course.department !== filters.department) return false;
      if (filters.course_number && course.courseNumber.toString() !== filters.course_number.toString()) return false;
      if (filters.professor && !course.faculty.includes(filters.professor)) return false;
      if (filters.semester && course.semester !== filters.semester) return false;
      if (filters.credits && course.credits !== Number(filters.credits)) return false;
      if (filters.days && filters.days.length > 0) {
        const dayMatch = course.times.some(slot => filters.days.includes(slot.day));
        if (!dayMatch) return false;
      }
      return true;
    });
    setFilteredCourses(results);
  };

  return (
    <div className="min-h-screen bg-gray-900 p-4 flex flex-col gap-6">
      {/* Filter Section */}
      <FilterSection
        semesters={semesters}
        departments={departments}
        maxCourseNumber={499}
        onFilter={handleFilter}
      />

      {/* Schedule */}
      <WeeklySchedule courses={filteredCourses} />
    </div>
  );
}