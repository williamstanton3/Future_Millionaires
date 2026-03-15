// src/App.jsx
import React, { useState, useEffect } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";
import { fetchMeta, fetchCourses } from "./api/CourseApi"
import CourseList from "./components/Courses/CourseList";

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
      fetchMeta().then((data) => setMeta(data))
  }, []);

  // Called when Apply is clicked
    const handleFilter = async (filters) => {
      try {
        const data = await fetchCourses(filters);
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
      <CourseList courses={courses} />
      <WeeklySchedule courses={courses} />
    </div>
  );
}