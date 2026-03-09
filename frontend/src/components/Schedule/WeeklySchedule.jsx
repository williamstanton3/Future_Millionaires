import React from "react";
import { courses } from "../../data/mockCourses";

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
                            <div key={course.id}>
                                {course.name}
                            </div>
                        ))}
                </div>
            ))}
        </div>
    );
}