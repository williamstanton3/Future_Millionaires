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
  const [toast, setToast] = useState(null); // { type: "success" | "error", msg: string }
  const [errorMsg, setErrorMsg] = useState("");

  const [imageOpen, setImageOpen] = useState(false);
  const [selectedImage, setSelectedImage] = useState(null);

  function formatTime(time) {
    if (Array.isArray(time)) {
      let [hour, minute] = time;
      const ampm = hour >= 12 ? "PM" : "AM";
      hour = hour % 12 || 12;
      return `${hour}:${String(minute).padStart(2, "0")} ${ampm}`;
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

  const showToast = (type, msg) => {
    setToast({ type, msg });
    setTimeout(() => setToast(null), 3000);
  };

  const handleAdd = async () => {
    try {
      await onAddCourse(course);
      setErrorMsg("");
      setDetailOpen(false);
      showToast("success", `${course.subject} ${course.number} added to schedule.`);
    } catch (e) {
      const isConflict = e.message?.toLowerCase().includes("conflict");
      setErrorMsg(e.message || "Something went wrong. Please try again.");
      setDetailOpen(false);
      if (!isConflict) showToast("error", e.message || "Could not add course.");
    }
  };

  const groupedMeetings = {};
  (course.times || []).forEach((m) => {
    const key = `${m.start_time}-${m.end_time}`;
    if (!groupedMeetings[key]) {
      groupedMeetings[key] = { days: [], start: m.start_time, end: m.end_time };
    }
    groupedMeetings[key].days.push(m.day);
  });

  return (
    <>
      {/* SEARCH RESULTS CARD (MAIN ONE) */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogTrigger asChild>
          <div className="relative bg-gray-700 p-4 rounded-md shadow-md cursor-pointer hover:bg-gray-600 transition flex items-center">

            {/* LEFT SIDE */}
            <div>
              <div className="font-bold text-lg">
                {course.subject} {course.number} {course.section}
              </div>
              <div className="text-sm text-gray-300">{course.name}</div>
              <div className="text-sm mt-2">
                {(course.professors || []).map((prof, i) => (
                  <div key={i}>Faculty: {prof.name}</div>
                ))}
              </div>
            </div>

            {/* CENTER */}
            <div className="absolute left-1/2 -translate-x-1/2 text-sm text-center">
              <span className="font-semibold">Meetings:</span>{" "}
              {course.times?.length > 0 ? (
                Object.values(groupedMeetings).map((g, i) => (
                  <div key={i}>
                    {g.days.join("")} {formatTime(g.start)} - {formatTime(g.end)}
                  </div>
                ))
              ) : (
                <span className="text-gray-400">None</span>
              )}
            </div>

            {/* RIGHT */}
            <div className="ml-auto text-sm font-semibold whitespace-nowrap">
              {course.credits} credits
            </div>
          </div>
        </DialogTrigger>

        {/* DETAIL MODAL */}
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {course.subject} {course.number} {course.section} - {course.name}
            </DialogTitle>
          </DialogHeader>

          {/* PROFESSORS */}
          <div className="mt-4 space-y-3">
            {(course.professors || []).map((prof, i) => (
              <div key={i} className="flex items-center gap-3">

                {/* CLICKABLE IMAGE */}
                <img
                  src={prof.imageUrl || "/default-prof.png"}
                  alt={prof.name}
                  className="w-16 h-16 rounded-full object-cover cursor-pointer hover:scale-105 transition flex-shrink-0"
                  onClick={() => {
                    setSelectedImage(prof.imageUrl || "/default-prof.png");
                    setImageOpen(true);
                  }}
                />

                <div>
                  <div className="font-medium">{prof.name}</div>

                  {/* RMP Rating Row */}
                  <div className="flex items-center gap-2 mt-1 text-xs">
                    <span className="bg-blue-700 text-white px-2 py-0.5 rounded font-bold">
                      {prof.overallRating > 0 ? prof.overallRating.toFixed(1) : "N/A"}
                    </span>
                    <span className="text-gray-400">Rating</span>

                    <span className="bg-orange-700 text-white px-2 py-0.5 rounded font-bold ml-2">
                      {prof.avgDifficulty > 0 ? prof.avgDifficulty.toFixed(1) : "N/A"}
                    </span>
                    <span className="text-gray-400">Difficulty</span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* COURSE INFO */}
          <div className="mt-4 text-sm space-y-1">
            <div>Semester: {formatSemester(course.semester)}</div>
            <div>Credits: {course.credits}</div>
            <div>Section: {course.section}</div>
            <div>Location: {course.location}</div>
            <div>Open Seats: {course.open_seats}</div>
            <div>Total Seats: {course.total_seats}</div>
          </div>

          {/* MEETINGS */}
          <div className="mt-2 text-sm">
            <span className="font-semibold">Meetings:</span>{" "}
            {course.times?.length > 0 ? (
              <div className="mt-1 space-y-0.5">
                {course.times.map((m, index) => (
                  <div key={index}>
                    {m.day} {formatTime(m.start_time)} - {formatTime(m.end_time)}
                  </div>
                ))}
              </div>
            ) : (
              <span className="text-gray-400">None</span>
            )}
          </div>

          <div className="mt-4 flex justify-between">
            <Button onClick={handleAdd}>Add to Schedule</Button>
            <DialogClose className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700">
              Close
            </DialogClose>
          </div>
        </DialogContent>
      </Dialog>

      {/* IMAGE ZOOM MODAL */}
      <Dialog open={imageOpen} onOpenChange={setImageOpen}>
        <DialogContent className="flex justify-center items-center max-w-3xl">
          <DialogTitle className="sr-only">Professor Image</DialogTitle>
          <img
            src={selectedImage}
            alt="Professor"
            className="w-80 h-80 rounded-full object-cover"
          />
        </DialogContent>
      </Dialog>

      {/* TOAST NOTIFICATION */}
      {toast && (
        <div
          className={`fixed top-5 left-1/2 -translate-x-1/2 z-50 flex items-center gap-2 px-5 py-3 rounded-lg shadow-lg text-white text-sm font-medium transition-all animate-in fade-in slide-in-from-top-2 ${
            toast.type === "success" ? "bg-green-600" : "bg-red-600"
          }`}
        >
          <span>{toast.type === "success" ? "✓" : "✗"}</span>
          <span>{toast.msg}</span>
        </div>
      )}
    </>
  );
}