import React from "react";
import "./Schedule.css";

const TimeColumn = ({ startHour = 8, endHour = 21, hourHeight = 40}) => {
    return (
        <div className="day-column">
            {Array.from({ length: endHour - startHour }).map((_, i) => (
                <div key={i} className = "time-label">
                    {startHour + i}:00
                </div>
            ))}
        </div>
    );
};

export default TimeColumn;