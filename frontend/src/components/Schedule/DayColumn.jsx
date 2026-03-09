import React from "react";
import ClassBlock from "./ClassBlock";

const DayColumn = ({ day, courses, startHour = 8, hourHeight = 40 }) => {
    // convert HH:MM to px from top
    const timeToTop = (timeStr) => {
        const [hour, minute] = timeStr.split(":").map(Number);
        return (hour + minute / 60 - startHour) * hourHeight;
    };
    return (
        <div className="day-column">
            <h2>{day}</h2>
            {courses
                .filter((course) => course.days.includes(day))
                .map(course => {
                    const top = timeToTop(course.start);
                    const height = timeToTop(course.end) - timeToTop(course.start);
                    return (
                        <ClassBlock
                            key = {course.id}
                            course = {course}
                            style = {{top: top + "px", height: height + "px"}}
                            />
                    );
                })
            }
        </div>
    )
}

export default DayColumn;