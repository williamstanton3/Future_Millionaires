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

  if (filters.days?.length > 0) {
    filters.days.forEach((day) => {
      if (filters.start_time && filters.end_time) {
        params.append("times", `${day} ${filters.start_time}-${filters.end_time}`);
      } else {
        params.append("times", day);
      }
    });
  }

  const res = await fetch(`http://localhost:7070/courses?${params.toString()}`);
  return res.json();
}