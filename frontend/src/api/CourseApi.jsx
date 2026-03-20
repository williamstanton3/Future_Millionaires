// src/api/courseApi.js

export async function fetchMeta() {
  const res = await fetch("http://localhost:7070/courses/meta");
  return res.json();
}

export async function fetchCourses(filters) {
  const params = new URLSearchParams();

  if (filters.keyword) params.append("keyword", filters.keyword);
  if (filters.department) params.append("department", filters.department);
  if (filters.course_number) params.append("number", filters.course_number);
  if (filters.semester) params.append("semester", filters.semester);
  if (filters.credits) params.append("credits", filters.credits);

  // FIX: professors is a String[] on the backend, so append each one separately
  if (filters.professors?.length > 0) {
    filters.professors.forEach((prof) => {
      params.append("professors", prof);
    });
  } else if (filters.professor) {
    // fallback: support legacy single-professor string
    params.append("professors", filters.professor);
  }

  const dayMap = {
    Monday: "M",
    Tuesday: "T",
    Wednesday: "W",
    Thursday: "R",
    Friday: "F",
  };

  if (filters.days?.length > 0) {
    filters.days.forEach((day) => {
      const backendDay = dayMap[day] || day;

      if (filters.start_time && filters.end_time) {
        // FIX: ensure times are in HH:mm:ss format as required by LocalTime.parse()
        const start = toHHmmss(filters.start_time);
        const end = toHHmmss(filters.end_time);
        params.append("times", `${backendDay} ${start}-${end}`);
      } else {
        params.append("times", backendDay);
      }
    });
  }

  const res = await fetch(`http://localhost:7070/courses?${params.toString()}`);
  return res.json();
}

/**
 * Ensures a time string is in HH:mm:ss format.
 * Accepts "HH:mm" or "HH:mm:ss" — appends ":00" if seconds are missing.
 */
function toHHmmss(time) {
  if (!time) return time;
  const parts = time.split(":");
  if (parts.length === 2) return `${time}:00`;
  return time;
}