import React, { useState, useEffect } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";
import { fetchMeta, fetchCourses } from "./api/CourseApi";
import {
  setSemester,
  addCourseToBackend,
  removeCourseFromBackend,
  finalizeSchedule,
  getAllSchedules,
  getActiveSchedule,
  clearSchedule,
  deleteSavedSchedule,
  loadSavedSchedule
} from "./api/ScheduleApi";
import CourseList from "./components/Courses/CourseList";
import ConflictModal from "./components/ConflictModal";
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

  const [pendingLoad, setPendingLoad] = useState(null);
  const [conflictData, setConflictData] = useState(null);

  const DAY_MAP = { M: "Mon", T: "Tue", W: "Wed", R: "Thu", F: "Fri" };
  const COLORS = ["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899"];

  useEffect(() => {
    fetchMeta().then(setMeta);
    getAllSchedules().then(setSavedSchedules).catch(() => {});
  }, []);

  const normalizeTime = (timeValue) => {
    if (Array.isArray(timeValue) && timeValue.length >= 2) {
      const [hour, minute] = timeValue;
      return `${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
    }
    if (typeof timeValue === "string") return timeValue;
    return "";
  };

  const normalizeCourseList = (rawCourses) =>
    (rawCourses ?? []).map((course, i) => ({
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

  // Tell the backend which semester is active, then restore whatever live schedule is there.
  const handleSemesterChange = async (semester) => {
    setActiveSemester(semester);
    setCourses([]);
    setSchedule([]);

    try {
      await setSemester(semester);
      const data = await getActiveSchedule();
      if (data?.courses?.length > 0) {
        setSchedule(normalizeCourseList(data.courses));
      }
    } catch (err) {
      console.error("Failed to load semester schedule:", err);
    }
  };

  const handleFilter = async (filters) => {
    try {
      const data = await fetchCourses(filters);
      setCourses(data);
    } catch (err) {
      console.error("Failed to fetch courses:", err);
    }
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

  // Adds to frontend state and immediately syncs to the backend live schedule.
  const handleAddCourse = async (course) => {
    const normalizedTimes = Array.isArray(course.times)
      ? course.times.map((t) => ({
          ...t,
          day: DAY_MAP[t.day] ?? t.day,
          start: normalizeTime(t.start ?? t.start_time),
          end: normalizeTime(t.end ?? t.end_time),
        }))
      : [];

    const result = await addCourseToBackend(course);

    if (result.success === false) {
      if (result.suggestedSchedules?.length > 0) {
        // Time conflict with suggestions — show the conflict modal
        setConflictData({
          message: result.message,
          suggestedSchedules: result.suggestedSchedules,
        });
      }
      throw new Error(result.message || "Failed to add course.");
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

  // Accepts a suggested alternative schedule after a time conflict.
  const handleAcceptSuggestion = async (suggestedSchedule) => {
    try {
      await clearSchedule();
      for (const course of suggestedSchedule.schedule) {
        await addCourseToBackend(course);
      }
      const updated = await getActiveSchedule();
      setSchedule(normalizeCourseList(updated.courses));
      setConflictData(null);
    } catch (err) {
      console.error("Failed to apply suggested schedule:", err);
    }
  };

  // Removes from frontend state and immediately syncs to the backend live schedule.
  const handleRemoveCourse = async (course) => {
    await removeCourseFromBackend(course);
    setSchedule((prev) =>
      prev.filter(
        (c) =>
          !(c.subject === course.subject &&
            c.number === course.number &&
            c.section === course.section)
      )
    );
  };

  // Finalizes the active schedule on the backend (archives it, clears the live slot),
  // then resets the frontend to a blank state.
  const handleSaveSchedule = async () => {
    if (schedule.length === 0 || !activeSemester) return;
    setSaveStatus("saving");
    try {
      const result = await finalizeSchedule();
      if (!result.success) {
        setSaveStatus("error");
        return;
      }
      setSaveStatus("success");
      getAllSchedules().then(setSavedSchedules).catch(() => {});
      setSchedule([]);
      setCourses([]);
      setActiveSemester("");
    } catch (err) {
      console.error("Failed to finalize schedule:", err);
      setSaveStatus("error");
    } finally {
      setTimeout(() => setSaveStatus(null), 3000);
    }
  };

  const handleRequestLoad = (semester, courses) => {
    setPendingLoad({ semester, courses });
  };

  const handleConfirmLoad = async () => {
      const { semester, courses } = pendingLoad;
      setPendingLoad(null);

      try {
          const result = await loadSavedSchedule(semester);
          if (result.success) {
              setActiveSemester(semester);
              setCourses([]);
              setSchedule(normalizeCourseList(result.courses ?? []));
              return;
          }
      } catch (err) {
          // fall through to JSON mode handling
      }

      // JSON mode fallback — load entirely on frontend
      try {
          await setSemester(semester);
          await clearSchedule();

          const normalized = normalizeCourseList(courses.schedule ?? []);
          setActiveSemester(semester);
          setCourses([]);
          setSchedule(normalized);

          await Promise.all(normalized.map((course) => addCourseToBackend(course)));
      } catch (err) {
          console.error("Failed to load schedule in JSON mode:", err);
      }
  };

  const handleDeleteSaved = async (semester) => {
      await deleteSavedSchedule(semester);
      setSavedSchedules((prev) => {
          const next = { ...prev };
          delete next[semester];
          return next;
      });
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

      <SavedSchedules schedules={savedSchedules} onLoad={handleRequestLoad} onDelete={handleDeleteSaved} />

      <ConflictModal
        open={!!conflictData}
        message={conflictData?.message}
        suggestedSchedules={conflictData?.suggestedSchedules}
        onAccept={handleAcceptSuggestion}
        onClose={() => setConflictData(null)}
      />

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