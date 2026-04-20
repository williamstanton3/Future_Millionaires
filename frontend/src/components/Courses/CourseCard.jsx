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

  function formatTime(time) {
    if (Array.isArray(time)) {
      let [hour, minute] = time;
      const ampm = hour >= 12 ? "PM" : "AM";
      hour = hour % 12 || 12;
      return `${hour}:${minute.toString().padStart(2, "0")} ${ampm}`;
    }
    return "";
  }

  const formatSemester = (semester) => {
    if (!semester) return "";
    const parts = semester.split("_");
    const year = parts[0];
    const term = parts.slice(1).join(" ");
    return `${term} ${year}`;
  };

  const handleAdd = async () => {
    try {
      await onAddCourse(course);
      setAddSuccess(true);
      setErrorMsg("");
      setDetailOpen(false);
      setStatusOpen(true);
    } catch (e) {
      const isConflict = e.message?.toLowerCase().includes("conflict");
      setAddSuccess(false);
      setErrorMsg(e.message || "Something went wrong. Please try again.");
      setDetailOpen(false);
      // Don't show the status dialog for conflicts — the ConflictModal handles that
      if (!isConflict) setStatusOpen(true);
    }
  };

const groupedMeetings = {};

course.times.forEach((m) => {
  const key = `${m.start_time}-${m.end_time}`;

  if (!groupedMeetings[key]) {
    groupedMeetings[key] = {
      days: [],
      start: m.start_time,
      end: m.end_time,
    };
  }

  groupedMeetings[key].days.push(m.day);
});

  return (
    <>
      {/* Course detail dialog */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogTrigger asChild>
          <div className="bg-gray-700 p-4 rounded-md shadow-md cursor-pointer hover:bg-gray-600 transition">

            <div className="font-bold text-lg">
              {course.subject} {course.number} {course.section}
            </div>

            <div className="text-sm text-gray-300">
              {course.name}
            </div>

            {/* Professor preview card */}
            <div className="mt-3 flex items-center justify-between gap-4">
              <div className="text-sm">
                Professor: {primaryProf?.name || "TBA"}
              </div>

              <img
                src={primaryProf?.imageUrl || "/default-prof.png"}
                alt={primaryProf?.name || "Professor"}
                className="w-20 h-20 object-cover rounded-full shrink-0"
              />
            </div>

            <div className="text-sm mt-2">
              Semester: {formatSemester(course.semester)}
            </div>

            <div className="text-sm">
              Credits: {course.credits}
            </div>

          <div className="relative bg-gray-700 p-4 rounded-md shadow-md cursor-pointer hover:bg-gray-600 transition flex items-center">

            {/* LEFT */}
            <div>
              <div className="font-bold text-lg">
                {course.subject} {course.number} {course.section}
              </div>
              <div className="text-sm text-gray-300">{course.name}</div>
              <div className="text-sm mt-1">Prof: {course.faculty}</div>
            </div>

            {/* CENTER (truly centered regardless of sides) */}
            <div className="absolute left-1/2 -translate-x-1/2 text-sm text-center">
              <div className="text-sm">
                <span className="font-semibold">Meetings:</span>{" "}
                {Object.values(groupedMeetings).map((g, i) => (
                  <div key={i}>
                    {g.days.join("")} {formatTime(g.start)} - {formatTime(g.end)}
                  </div>
                ))}
              </div>
            </div>

            {/* RIGHT (pushed all the way right) */}
            <div className="ml-auto text-sm font-semibold whitespace-nowrap">
              {course.credits} credits
            </div>

          </div>
        </DialogTrigger>

        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {course.subject} {course.number} {course.section} - {course.name}
            </DialogTitle>

            <DialogDescription>
              {course.professors?.map((prof, index) => (
                <div key={index} className="mt-2 flex items-center gap-2">
                  <img
                    src={prof.imageUrl || "/default-prof.png"}
                    alt={prof.name}
                    className="w-12 h-12 object-cover rounded-full"
                  />
                  <div className="text-sm">{prof.name}</div>
                </div>
              ))}

              <div className="text-sm mt-2">
                Semester: {formatSemester(course.semester)}
              </div>
              <div className="text-sm">Credits: {course.credits}</div>
              <div className="text-sm">Section: {course.section}</div>
              <div className="text-sm">Location: {course.location}</div>
              <div className="text-sm">Open Seats: {course.open_seats}</div>
              <div className="text-sm">Total Seats: {course.total_seats}</div>

              <div className="mt-2 text-sm font-semibold">Meetings:</div>
              {course.times?.map((m, index) => (
                <div key={index}>
                  {m.day} {formatTime(m.start_time)} - {formatTime(m.end_time)}
                </div>
              ))}

              {course.description && (
                <div className="mt-2 text-sm">
                  {course.description}
                </div>
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
            className={`mt-4 w-full ${
              addSuccess
                ? "bg-green-600 hover:bg-green-700"
                : "bg-red-600 hover:bg-red-700"
            }`}
            onClick={() => setStatusOpen(false)}
          >
            OK
          </Button>
        </DialogContent>
      </Dialog>
    </>
  );
}
