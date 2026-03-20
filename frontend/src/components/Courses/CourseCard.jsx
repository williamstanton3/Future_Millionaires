import React, { useState } from "react";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogClose,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

export default function CourseCard({ course, onAddCourse }) {
  const [detailOpen, setDetailOpen] = useState(false);
  const [statusOpen, setStatusOpen] = useState(false);
  const [addSuccess, setAddSuccess] = useState(null);
  const [errorMsg, setErrorMsg] = useState("");

  const formatTime = (timeArr) => {
    if (!Array.isArray(timeArr)) return "";
    const [hour, min] = timeArr;
    const h = hour % 12 === 0 ? 12 : hour % 12;
    const ampm = hour < 12 ? "AM" : "PM";
    return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
  };

  const formatSemester = (semester) => {
    if (!semester) return "";
    const parts = semester.split("_");
    const year = parts[0];
    const term = parts.slice(1).join(" ");
    return `${term} ${year}`;
  };

  const handleAdd = () => {
    try {
      onAddCourse(course);
      setAddSuccess(true);
      setErrorMsg("");
    } catch (e) {
      setAddSuccess(false);
      setErrorMsg(e.message || "Something went wrong. Please try again.");
    } finally {
      setDetailOpen(false);
      setStatusOpen(true);
    }
  };

  return (
    <>
      {/* Course detail dialog */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogTrigger asChild>
          <div className="bg-gray-700 p-4 rounded-md shadow-md cursor-pointer hover:bg-gray-600 transition">
            <div className="font-bold text-lg">
              {course.subject} {course.number} {course.section}
            </div>
            <div className="text-sm text-gray-300">{course.name}</div>
            <div className="mt-2 text-sm">Professor: {course.faculty}</div>
            <div className="text-sm">Semester: {formatSemester(course.semester)}</div>
            <div className="text-sm">Credits: {course.credits}</div>
          </div>
        </DialogTrigger>

        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {course.subject} {course.number} {course.section} - {course.name}
            </DialogTitle>
            <DialogDescription>
              <div className="mt-2 text-sm">Professor: {course.faculty}</div>
              <div className="text-sm">Semester: {formatSemester(course.semester)}</div>
              <div className="text-sm">Credits: {course.credits}</div>
              <div className="text-sm">Section: {course.section}</div>
              <div className="text-sm">Location: {course.location}</div>
              <div className="text-sm">Open Seats: {course.open_seats}</div>
              <div className="text-sm">Total Seats: {course.total_seats}</div>

              <div className="mt-2 text-sm font-semibold">Meetings:</div>
              {course.times.map((m, index) => (
                <div key={index}>
                  {m.day} {formatTime(m.start_time)} - {formatTime(m.end_time)}
                </div>
              ))}

              {course.description && (
                <div className="mt-2 text-sm">{course.description}</div>
              )}
            </DialogDescription>
          </DialogHeader>

          <div className="mt-4 flex justify-between">
            <Button onClick={handleAdd}>Add to Schedule</Button>
            <DialogClose className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700">
              Close
            </DialogClose>
          </div>
        </DialogContent>
      </Dialog>

      {/* Success / failure dialog */}
      <Dialog open={statusOpen} onOpenChange={setStatusOpen}>
        <DialogContent className="max-w-sm text-center">
          <DialogHeader>
            <DialogTitle className={addSuccess ? "text-green-400" : "text-red-400"}>
              {addSuccess ? "✓ Course Added!" : "✗ Could Not Add Course"}
            </DialogTitle>
            <DialogDescription>
              {addSuccess
                ? `${course.subject} ${course.number} has been added to your schedule.`
                : errorMsg}
            </DialogDescription>
          </DialogHeader>
          <Button
            className={`mt-4 w-full ${addSuccess ? "bg-green-600 hover:bg-green-700" : "bg-red-600 hover:bg-red-700"}`}
            onClick={() => setStatusOpen(false)}
          >
            OK
          </Button>
        </DialogContent>
      </Dialog>
    </>
  );
}
