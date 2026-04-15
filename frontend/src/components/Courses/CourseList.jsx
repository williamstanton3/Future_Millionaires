import React from "react";
import CourseCard from "./CourseCard";

export default function CourseList({ courses, onAddCourse }) {
  return (
    <div className="bg-gray-800 rounded-md p-4 text-white">
      <h2 className="text-xl font-bold mb-4">Courses</h2>

      {courses.length === 0 ? (
        <p>No courses found</p>
      ) : (
        <div className="flex flex-col gap-3 max-h-[400px] overflow-y-auto">
          {courses.map((course, index) => (
            <CourseCard
              key={index}
              course={course}
              onAddCourse={onAddCourse} // <-- pass it down
            />
          ))}
        </div>
      )}
    </div>
  );
}