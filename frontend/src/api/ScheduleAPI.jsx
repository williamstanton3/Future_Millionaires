const BASE = "http://localhost:7070";

export async function setSemester(semester) {
  const res = await fetch(`${BASE}/schedule/semester?semester=${semester}`, {
    method: "POST",
  });
  return res.json();
}

export async function addCourseToBackend(course) {
  const res = await fetch(
    `${BASE}/schedule/add?subject=${course.subject}&number=${course.number}&section=${course.section}`,
    { method: "POST" }
  );
  return res.json();
}

export async function removeCourseFromBackend(course) {
  const res = await fetch(
    `${BASE}/schedule/remove?subject=${course.subject}&number=${course.number}&section=${course.section}`,
    { method: "DELETE" }
  );
  return res.json();
}

export async function getActiveSchedule() {
  const res = await fetch(`${BASE}/schedule`);
  return res.json();
}

export async function getAllSchedules() {
  const res = await fetch(`${BASE}/schedule/all`);
  return res.json();
}