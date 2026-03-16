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
import { Button } from "@/components/ui/button";

export default function CourseCard({ course, onAddCourse }) {
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
            <div className="mt-2 text-sm">Professor: {course.faculty}</div>
            <div className="text-sm">Semester: {course.semester}</div>
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

            {course.description && <div className="mt-2 text-sm">{course.description}</div>}
          </DialogDescription>
        </DialogHeader>

        <div className="mt-4 flex justify-between">
          <Button onClick={() => onAddCourse(course)}>Add to Schedule</Button>
          <DialogClose className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700">
            Close
          </DialogClose>
        </div>
      </DialogContent>
    </Dialog>
  );
}