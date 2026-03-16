import React, { useState, useEffect } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";
import { fetchMeta, fetchCourses } from "./api/CourseApi";
import CourseList from "./components/Courses/CourseList";

export default function App() {
  const [meta, setMeta] = useState({
    departments: [],
    numbers: [],
    semesters: [],
    credits: [],
  });
  const [courses, setCourses] = useState([]);
  const [schedule, setSchedule] = useState([]); // your weekly schedule courses

  useEffect(() => {
    fetchMeta().then((data) => setMeta(data));
  }, []);

  const handleFilter = async (filters) => {
    try {
      const data = await fetchCourses(filters);
      setCourses(data);
    } catch (err) {
      console.error("Failed to fetch courses:", err);
    }
  };

  const DAY_MAP = { M: "Mon", T: "Tue", W: "Wed", R: "Thu", F: "Fri" };
  const COLORS = ["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899"];

  const normalizeTime = (timeValue) => {
    if (Array.isArray(timeValue) && timeValue.length >= 2) {
      const [hour, minute] = timeValue;
      return `${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
    }
    if (typeof timeValue === "string") return timeValue;
    return "";
  };

  // Handler to add a course to the schedule
  const handleAddCourse = (course) => {
    const normalized = {
      ...course,
      color: COLORS[schedule.length % COLORS.length],
      times: Array.isArray(course.times)
        ? course.times.map((t) => ({
            ...t,
            day: DAY_MAP[t.day] ?? t.day,
            start: normalizeTime(t.start ?? t.start_time),
            end: normalizeTime(t.end ?? t.end_time),
          }))
        : [],
    };
    setSchedule((prev) => [...prev, normalized]);
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
      <CourseList courses={courses} onAddCourse={handleAddCourse} />
      <WeeklySchedule courses={schedule} /> {/* show added courses */}
    </div>
  );
}
