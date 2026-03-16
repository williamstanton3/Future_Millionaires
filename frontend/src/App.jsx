// src/App.jsx
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
  const [schedule, setSchedule] = useState([]);

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

  const toMinutes = (timeStr) => {
    const [h, m] = timeStr.split(":").map(Number);
    return h * 60 + m;
  };

  const hasConflict = (newTimes) => {
    for (const newSlot of newTimes) {
      const newStart = toMinutes(newSlot.start);
      const newEnd = toMinutes(newSlot.end);
      for (const scheduled of schedule) {
        for (const slot of scheduled.times) {
          if (slot.day !== newSlot.day) continue;
          const start = toMinutes(slot.start);
          const end = toMinutes(slot.end);
          if (newStart < end && newEnd > start) return true;
        }
      }
    }
    return false;
  };

  const handleAddCourse = (course) => {
    const normalizedTimes = Array.isArray(course.times)
      ? course.times.map((t) => ({
          ...t,
          day: DAY_MAP[t.day] ?? t.day,
          start: normalizeTime(t.start ?? t.start_time),
          end: normalizeTime(t.end ?? t.end_time),
        }))
      : [];

    if (hasConflict(normalizedTimes)) {
      throw new Error("Time conflict with an existing course.");
    }

    const normalized = {
      ...course,
      color: COLORS[schedule.length % COLORS.length],
      times: normalizedTimes,
    };
    setSchedule((prev) => [...prev, normalized]);
  };

  const handleRemoveCourse = (course) => {
    setSchedule((prev) =>
      prev.filter((c) => !(c.subject === course.subject && c.number === course.number && c.section === course.section))
    );
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
      <WeeklySchedule courses={schedule} onRemoveCourse={handleRemoveCourse} />
    </div>
  );
}
