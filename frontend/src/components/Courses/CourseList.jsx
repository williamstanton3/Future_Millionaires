import React from "react";
import CourseCard from "./CourseCard";

export default function CourseList({ courses }) {
  return (
    <div className="bg-gray-800 rounded-md p-4 text-white">
      <h2 className="text-xl font-bold mb-4">Courses</h2>

      {courses.length === 0 ? (
        <p>No courses found</p>
      ) : (
        <div className="flex flex-col gap-3">
          {courses.map((course, index) => (
            <CourseCard key={index} course={course} />
          ))}
        </div>
      )}
    </div>
  );
}