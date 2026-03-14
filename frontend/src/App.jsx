// src/App.jsx
import React, { useState, useEffect } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";

export default function App() {
  const [meta, setMeta] = useState({
    departments: [],
    numbers: [],
    semesters: [],
    credits: [],
  });
  const [courses, setCourses] = useState([]);

  // Fetch metadata once
  useEffect(() => {
    fetch("http://localhost:7070/courses/meta")
      .then((res) => res.json())
      .then((data) => setMeta(data))
      .catch((err) => console.error("Failed to fetch meta:", err));
  }, []);

  // Called when user clicks "Search / Apply"
  const handleFilter = async (filters) => {
    const params = new URLSearchParams();

    if (filters.keyword) params.append("keyword", filters.keyword);
    if (filters.department) params.append("department", filters.department);
    if (filters.course_number) params.append("number", filters.course_number);
    if (filters.professor) params.append("professors", filters.professor);
    if (filters.semester) params.append("semester", filters.semester);
    if (filters.credits) params.append("credits", filters.credits);

    // Days + time
    if (filters.days && filters.days.length > 0) {
      filters.days.forEach((day) => {
        if (filters.start_time && filters.end_time) {
          params.append(
            "times",
            `${day} ${filters.start_time}-${filters.end_time}`
          );
        } else {
          params.append("times", day);
        }
      });
    }

    try {
      const res = await fetch(`http://localhost:7070/courses?${params.toString()}`);
      const data = await res.json();
      setCourses(data);
    } catch (err) {
      console.error("Failed to fetch courses:", err);
    }
  };

  return (
    <div className="min-h-screen bg-gray-900 p-4 flex flex-col gap-6">
      <FilterSection
        semesters={meta.semesters}
        departments={meta.departments}
        maxCourseNumber={Math.max(...meta.numbers, 499)}
        credits={meta.credits}
        onFilter={handleFilter}
      />
      <WeeklySchedule courses={courses} />
    </div>
  );
}