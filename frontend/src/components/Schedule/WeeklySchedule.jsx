import React from "react";
import { courses } from "../../data/mockCourses";
import ClassBlock from "./ClassBlock";
import "./Schedule.css";

const days = ["Mon", "Tue", "Wed", "Thu", "Fri"];

export default function WeeklySchedule() {
    return (
        <div>
            {days.map(day => (
                <div key={day}>
                    <h2>{day}</h2>
                    {courses
                      .filter(course => course.days.includes(day))
                      .map(course => (
                          <ClassBlock key={course.id} course={course} />
                    ))}
                </div>
            ))}
        </div>
    );
}