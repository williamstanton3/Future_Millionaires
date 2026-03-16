// src/components/Courses/CourseCard.jsx
import React from "react";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogClose,
} from "@/components/ui/dialog";

export default function CourseCard({ course }) {
      console.log(course); // <--- check the actual field names
  const formatTime = (timeArr) => {
    if (!Array.isArray(timeArr)) return "";
    const [hour, min] = timeArr;
    const h = hour % 12 === 0 ? 12 : hour % 12;
    const ampm = hour < 12 ? "AM" : "PM";
    return `${h}:${String(min).padStart(2, "0")} ${ampm}`;
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <div className="bg-gray-700 p-4 rounded-md shadow-md cursor-pointer hover:bg-gray-600 transition">
          <div className="font-bold text-lg">
            {course.subject} {course.number} {course.section}
          </div>
          <div className="text-sm text-gray-300">{course.name}</div>
          <div className="mt-2 text-sm">Professor: {course.faculty}</div>
          <div className="text-sm">Semester: {course.semester}</div>
          <div className="text-sm">Credits: {course.credits}</div>
        </div>
      </DialogTrigger>

      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>
            {course.subject} {course.number} {course.section} - {course.name}
          </DialogTitle>
          <DialogDescription>
            <p className="mt-2 text-sm">Professor: {course.faculty}</p>
            <p className="text-sm">Semester: {course.semester}</p>
            <p className="text-sm">Credits: {course.credits}</p>
            <p className="text-sm">Section: {course.section}</p>
            <p className="text-sm">Location: {course.location}</p>
            <p className="text-sm">Open Seats: {course.open_seats}</p>
            <p className="text-sm">Total Seats: {course.total_seats}</p>

            <p className="text-sm mt-2 font-semibold">Meetings:</p>
            {course.times.map((m, index) => (
              <p key={index}>
                {m.day} {formatTime(m.start_time)} - {formatTime(m.end_time)}
              </p>
            ))}

            {course.description && <p className="mt-2 text-sm">{course.description}</p>}
          </DialogDescription>
        </DialogHeader>
        <DialogClose className="mt-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
          Close
        </DialogClose>
      </DialogContent>
    </Dialog>
  );
}