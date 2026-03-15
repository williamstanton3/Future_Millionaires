import React from "react";

export default function CourseCard({ course }) {
  const formatTime = (timeArr) => {
      if (!Array.isArray(timeArr)) return "";
      let [hour, minute] = timeArr.map((t) => parseInt(t, 10));
      const ampm = hour >= 12 ? "PM" : "AM";
      hour = hour % 12 || 12; // convert 0->12 and 13->1
      return `${hour}:${minute.toString().padStart(2, "0")} ${ampm}`;
    };

  return (
    <div className="bg-gray-700 p-4 rounded-md shadow-md">
      <div className="font-bold text-lg">
        {course.subject} {course.number}
      </div>

      <div className="text-sm text-gray-300">
        {course.title}
      </div>

      <div className="mt-2 text-sm">
        Professor: {course.professor}
      </div>

      <div className="text-sm">
        Semester: {course.semester}
      </div>

      <div className="text-sm">
        Credits: {course.credits}
      </div>

      <div className="text-sm mt-2">
        <div className="font-semibold">Meetings:</div>
        {course.times.map((meeting, index) => (
          <div key={index}>
            {meeting.day} {formatTime(meeting.start_time)} - {formatTime(meeting.end_time)}
          </div>
        ))}
      </div>

    </div>
  );
}