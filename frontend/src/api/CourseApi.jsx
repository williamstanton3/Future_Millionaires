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
  if (filters.professor) params.append("professors", filters.professor);
  if (filters.semester) params.append("semester", filters.semester);
  if (filters.credits) params.append("credits", filters.credits);

  // If specific days are selected use those, otherwise default to all weekdays
  const daysToSearch = filters.days?.length > 0
      ? filters.days
      : ["M", "T", "W", "R", "F"];

  // If a time range is provided, append each day with the time range
  if (filters.start_time && filters.end_time) {
      daysToSearch.forEach((day) => {
          params.append("times", `${day} ${filters.start_time}:00-${filters.end_time}:00`);
      });
  // If no time range, only append days that were explicitly selected
  } else if (filters.days?.length > 0) {
      filters.days.forEach((day) => {
          params.append("times", day);
      });
  }

  const res = await fetch(`http://localhost:7070/courses?${params.toString()}`);
  return res.json();
}