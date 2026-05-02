// src/api/courseApi.js

const BASE = "http://localhost:7070";

/* ---------------- META ---------------- */

export async function fetchMeta() {
  const res = await fetch(`${BASE}/courses/meta`);
  return res.json();
}

/* ---------------- COURSES ---------------- */

export async function fetchCourses(filters) {
  const params = new URLSearchParams();

  if (filters.keyword) params.append("keyword", filters.keyword);
  if (filters.department) params.append("department", filters.department);
  if (filters.course_number) params.append("number", filters.course_number);
  if (filters.professor) params.append("professors", filters.professor);
  if (filters.semester) params.append("semester", filters.semester);
  if (filters.credits != null) {
    if (filters.credits === 0) return [];
    params.append("credits", filters.credits)
  }

  const daysToSearch =
    filters.days?.length > 0
      ? filters.days
      : ["M", "T", "W", "R", "F"];

  if (filters.start_time && filters.end_time) {
    daysToSearch.forEach((day) => {
      params.append(
        "times",
        `${day} ${filters.start_time}:00-${filters.end_time}:00`
      );
    });
  } else if (filters.days?.length > 0) {
    filters.days.forEach((day) => {
      params.append("times", day);
    });
  }

  const res = await fetch(`${BASE}/courses?${params.toString()}`);
  return res.json();
}

/* ---------------- SCHEDULE ---------------- */

export async function setSemester(semester) {
  const res = await fetch(`${BASE}/schedule/semester?semester=${semester}`, {
    method: "POST",
  });

  const data = await res.json();
  return res.ok ? data : { success: false, ...data };
}

export async function addCourseToBackend(course) {
  const res = await fetch(
    `${BASE}/schedule/add?subject=${course.subject}&number=${course.number}&section=${course.section}`,
    { method: "POST" }
  );

  const data = await res.json();
  return res.ok ? data : { success: false, ...data };
}

export async function removeCourseFromBackend(course) {
  const res = await fetch(
    `${BASE}/schedule/remove?subject=${course.subject}&number=${course.number}&section=${course.section}`,
    { method: "DELETE" }
  );

  const data = await res.json();
  return res.ok ? data : { success: false, ...data };
}

export async function getActiveSchedule() {
  const res = await fetch(`${BASE}/schedule`);
  return res.json();
}

export async function getAllSchedules() {
  const res = await fetch(`${BASE}/schedule/all`);
  return res.json();
}