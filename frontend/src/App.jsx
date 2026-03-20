import React, { useState, useEffect } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";
import { fetchMeta, fetchCourses } from "./api/CourseApi";
import { setSemester, addCourseToBackend, getAllSchedules } from "./api/ScheduleApi";
import CourseList from "./components/Courses/CourseList";
import SavedSchedules from "./components/Schedule/SavedSchedules";
import { Button } from "./components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "./components/ui/dialog";

export default function App() {
  const [meta, setMeta] = useState({ departments: [], numbers: [], semesters: [], credits: [] });
  const [courses, setCourses] = useState([]);
  const [schedule, setSchedule] = useState([]);
  const [activeSemester, setActiveSemester] = useState("");
  const [saveStatus, setSaveStatus] = useState(null);
  const [savedSchedules, setSavedSchedules] = useState({});

  // Confirm dialog state for loading a saved schedule
  const [pendingLoad, setPendingLoad] = useState(null); // { semester, courses }

  const DAY_MAP = { M: "Mon", T: "Tue", W: "Wed", R: "Thu", F: "Fri" };
  const COLORS = ["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899"];

  useEffect(() => {
    fetchMeta().then(setMeta);
    getAllSchedules().then(setSavedSchedules).catch(() => {});
  }, []);

  const handleSemesterChange = (semester) => {
    setActiveSemester(semester);
    setCourses([]);
    setSchedule([]);
  };

  const handleFilter = async (filters) => {
    try {
      const data = await fetchCourses(filters);
      setCourses(data);
    } catch (err) {
      console.error("Failed to fetch courses:", err);
    }
  };

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

    setSchedule((prev) => [
      ...prev,
      {
        ...course,
        color: COLORS[prev.length % COLORS.length],
        times: normalizedTimes,
      },
    ]);
  };

  const handleRemoveCourse = (course) => {
    setSchedule((prev) =>
      prev.filter(
        (c) =>
          !(c.subject === course.subject &&
            c.number === course.number &&
            c.section === course.section)
      )
    );
  };

  const handleSaveSchedule = async () => {
    if (schedule.length === 0 || !activeSemester) return;
    setSaveStatus("saving");
    try {
      await setSemester(activeSemester);
      const results = await Promise.all(
        schedule.map((course) => addCourseToBackend(course))
      );
      const anyFailed = results.some((r) => !r.success);
      setSaveStatus(anyFailed ? "error" : "success");
      if (!anyFailed) {
        getAllSchedules().then(setSavedSchedules).catch(() => {});
        setSchedule([]);
        setCourses([]);
        setActiveSemester("");
      }
    } catch (err) {
      console.error("Failed to save schedule:", err);
      setSaveStatus("error");
    } finally {
      setTimeout(() => setSaveStatus(null), 3000);
    }
  };

  // User clicks Load on a saved schedule
  const handleRequestLoad = (semester, courses) => {
    setPendingLoad({ semester, courses });
  };

  // User confirms the load
  const handleConfirmLoad = () => {
    const { semester, courses } = pendingLoad;
    setPendingLoad(null);
    setActiveSemester(semester);

    const normalized = (courses.schedule ?? []).map((course, i) => ({  // <-- change courses to courses.schedule
      ...course,
      color: COLORS[i % COLORS.length],
      times: Array.isArray(course.times)
        ? course.times.map((t) => ({
            ...t,
            day: DAY_MAP[t.day] ?? t.day,
            start: normalizeTime(t.start ?? t.start_time),
            end: normalizeTime(t.end ?? t.end_time),
          }))
        : [],
    }));
    setSchedule(normalized);
    setCourses([]);
  };

  return (
    <div className="min-h-screen bg-gray-900 p-4 flex flex-col gap-6">
      <FilterSection
        semesters={meta.semesters}
        departments={meta.departments}
        professors={meta.professors}
        numbers={meta.numbers}
        creditOptions={meta.credits}
        activeSemester={activeSemester}
        onSemesterChange={handleSemesterChange}
        onFilter={handleFilter}
      />

      <CourseList courses={courses} onAddCourse={handleAddCourse} />

      <div className="flex items-center gap-4">
        <Button
          onClick={handleSaveSchedule}
          disabled={schedule.length === 0 || !activeSemester || saveStatus === "saving"}
          className="bg-blue-600 hover:bg-blue-700 text-white"
        >
          {saveStatus === "saving" ? "Saving..." : "Save Schedule"}
        </Button>
        {saveStatus === "success" && (
          <span className="text-green-400 text-sm">✓ Schedule saved!</span>
        )}
        {saveStatus === "error" && (
          <span className="text-red-400 text-sm">✗ Failed to save. Try again.</span>
        )}
      </div>

      <WeeklySchedule courses={schedule} onRemoveCourse={handleRemoveCourse} />

      <SavedSchedules schedules={savedSchedules} onLoad={handleRequestLoad} />

      {/* Confirm load dialog */}
      <Dialog open={!!pendingLoad} onOpenChange={(open) => !open && setPendingLoad(null)}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>Load saved schedule?</DialogTitle>
            <DialogDescription>
              This will replace your current schedule with the saved{" "}
              <span className="font-medium text-white">{pendingLoad?.semester}</span> schedule.
              Any unsaved changes will be lost.
            </DialogDescription>
          </DialogHeader>
          <div className="flex justify-end gap-3 mt-4">
            <Button variant="outline" onClick={() => setPendingLoad(null)}>
              Cancel
            </Button>
            <Button className="bg-blue-600 hover:bg-blue-700 text-white" onClick={handleConfirmLoad}>
              Load
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
