package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleController {

    private static final int STUDENT_ID = 1;
    private final SupabaseClient db;
    private final CourseList courseList;
    private final Student student;
    private final PersistenceManager persistence;
    private final boolean useJson;

    public ScheduleController(Javalin app, CourseList courseList, Student student,
                              PersistenceManager persistence, SupabaseClient db, boolean useJson) {
        this.db = db;
        this.courseList = courseList;
        this.student = student;
        this.persistence = persistence;
        this.useJson = useJson;

        // GET /schedule
        app.get("/schedule", ctx -> {
            if (useJson) {
                Schedule schedule = student.getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }
                ctx.json(Map.of(
                        "courses", schedule.getSchedule(),
                        "credits", schedule.getCredits()
                ));
            } else {
                JsonNode schedule = getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }
                String scheduleId = schedule.get("id").asText();
                int credits = schedule.get("credits").asInt();
                List<Course> courses = loadCoursesForSchedule(scheduleId);
                ctx.json(Map.of("courses", courses, "credits", credits));
            }
        });

        // POST /schedule/semester?semester=2024_Fall
        app.post("/schedule/semester", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null) {
                ctx.status(400).json(Map.of("message", "Missing required query param: semester"));
                return;
            }

            if (useJson) {
                student.setActiveSemester(semester);
                persistence.save(student);
                ctx.json(Map.of(
                        "message", "Active semester set to " + semester,
                        "semester", semester
                ));
            } else {
                upsertStudent();

                // Check if a non-finalized schedule already exists for this semester
                JsonNode existing = db.get("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                        + "&semester=eq." + semester
                        + "&is_finalized=eq.false&select=id,semester");

                if (existing.size() > 0) {
                    db.patch("/rest/v1/students?id=eq." + STUDENT_ID,
                            "{\"active_semester\": \"" + semester + "\"}");
                } else {
                    ObjectNode node = db.mapper.createObjectNode()
                            .put("student_id",   STUDENT_ID)
                            .put("semester",     semester)
                            .put("is_finalized", false)
                            .put("credits",      0);
                    db.post("/rest/v1/schedules", node.toString(), "");
                    db.patch("/rest/v1/students?id=eq." + STUDENT_ID,
                            "{\"active_semester\": \"" + semester + "\"}");
                }

                ctx.json(Map.of(
                        "message",  "Active semester set to " + semester,
                        "semester", semester
                ));
            }
        });

        // POST /schedule/add?subject=COMP&number=422&section=A
        app.post("/schedule/add", ctx -> {
            String subject     = ctx.queryParam("subject");
            String numberParam = ctx.queryParam("number");
            String section     = ctx.queryParam("section");

            if (subject == null || numberParam == null || section == null) {
                ctx.status(400).json(Map.of("message", "Missing required query params: subject, number, section"));
                return;
            }

            int number;
            try {
                number = Integer.parseInt(numberParam);
            } catch (NumberFormatException e) {
                ctx.status(400).json(Map.of("message", "Invalid course number: must be an integer"));
                return;
            }

            if (useJson) {
                Schedule schedule = student.getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }

                Course toAdd = courseList.getCourses().stream()
                        .filter(c -> c.getSubject().equals(subject)
                                && c.getNumber() == number
                                && c.getSection().equals(section)
                                && c.getSemester().equals(student.getActiveSemester()))
                        .findFirst().orElse(null);

                if (toAdd == null) {
                    ctx.status(404).json(Map.of("message",
                            "Course not found: " + subject + " " + number + " section " + section));
                    return;
                }

                boolean success = schedule.addCourse(toAdd);
                if (success) {
                    persistence.save(student);
                    ctx.status(200).json(Map.of(
                            "success", true,
                            "message", schedule.getUserMessage(),
                            "credits", schedule.getCredits()
                    ));
                } else {
                    boolean isTimeConflict = schedule.getLatestResult() == Schedule.Result.TIME_CONFLICT;
                    if (isTimeConflict) {
                        List<Schedule> suggestions =
                                schedule.suggestAlternatives(toAdd, courseList.getCourses());
                        ctx.status(409).json(Map.of(
                                "success", false,
                                "message", schedule.getUserMessage(),
                                "credits", schedule.getCredits(),
                                "suggestedSchedules", suggestions));
                    } else {
                        ctx.status(409).json(Map.of(
                                "success", false,
                                "message", schedule.getUserMessage(),
                                "credits", schedule.getCredits()));
                    }
                }
            } else {
                JsonNode schedule = getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }

                String scheduleId = schedule.get("id").asText();
                String semester   = schedule.get("semester").asText();

                Course toAdd = courseList.getCourses().stream()
                        .filter(c -> c.getSubject().equals(subject)
                                && c.getNumber() == number
                                && c.getSection().equals(section)
                                && c.getSemester().equals(semester))
                        .findFirst().orElse(null);

                if (toAdd == null) {
                    ctx.status(404).json(Map.of("message",
                            "Course not found: " + subject + " " + number + " section " + section));
                    return;
                }

                List<Course> existing = loadCoursesForSchedule(scheduleId);
                Schedule tempSchedule = new Schedule(STUDENT_ID, semester, existing);
                boolean success = tempSchedule.addCourse(toAdd);

                if (!success) {
                    if (tempSchedule.getLatestResult() == Schedule.Result.TIME_CONFLICT) {
                        List<Schedule> suggestions =
                                tempSchedule.suggestAlternatives(toAdd, courseList.getCourses());
                        ctx.status(409).json(Map.of(
                                "success", false,
                                "message", tempSchedule.getUserMessage(),
                                "credits", tempSchedule.getCredits(),
                                "suggestedSchedules", suggestions));
                    } else {
                        ctx.status(409).json(Map.of(
                                "success", false,
                                "message", tempSchedule.getUserMessage(),
                                "credits", tempSchedule.getCredits()));
                    }
                    return;
                }

                JsonNode courseRows = db.get("/rest/v1/courses?subject=eq." + subject
                        + "&number=eq." + number
                        + "&section=eq." + section
                        + "&semester=eq." + semester
                        + "&select=id");

                if (courseRows.size() == 0) {
                    ctx.status(404).json(Map.of("message", "Course not found in database"));
                    return;
                }

                String courseId = courseRows.get(0).get("id").asText();
                ObjectNode link = db.mapper.createObjectNode()
                        .put("schedule_id", scheduleId)
                        .put("course_id",   courseId);
                db.post("/rest/v1/schedule_courses", link.toString(), "");

                int newCredits = tempSchedule.getCredits();
                db.patch("/rest/v1/schedules?id=eq." + scheduleId,
                        "{\"credits\": " + newCredits + "}");

                ctx.status(200).json(Map.of(
                        "success", true,
                        "message", tempSchedule.getUserMessage(),
                        "credits", newCredits));
            }
        });

        // DELETE /schedule/remove?subject=COMP&number=422&section=A
        app.delete("/schedule/remove", ctx -> {
            String subject     = ctx.queryParam("subject");
            String numberParam = ctx.queryParam("number");
            String section     = ctx.queryParam("section");

            if (subject == null || numberParam == null || section == null) {
                ctx.status(400).json(Map.of("message", "Missing required query params: subject, number, section"));
                return;
            }

            int number;
            try {
                number = Integer.parseInt(numberParam);
            } catch (NumberFormatException e) {
                ctx.status(400).json(Map.of("message", "Invalid course number: must be an integer"));
                return;
            }

            if (useJson) {
                Schedule schedule = student.getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }
                boolean success = schedule.removeCourse(subject, number, section);
                if (success) persistence.save(student);
                ctx.status(success ? 200 : 404).json(Map.of(
                        "success", success,
                        "message", schedule.getUserMessage(),
                        "credits", schedule.getCredits()
                ));
            } else {
                JsonNode schedule = getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }

                String scheduleId = schedule.get("id").asText();
                String semester   = schedule.get("semester").asText();

                JsonNode courseRows = db.get("/rest/v1/courses?subject=eq." + subject
                        + "&number=eq." + number
                        + "&section=eq." + section
                        + "&semester=eq." + semester
                        + "&select=id");

                if (courseRows.size() == 0) {
                    ctx.status(404).json(Map.of("message", "Course not found in database"));
                    return;
                }

                String courseId = courseRows.get(0).get("id").asText();
                db.delete("/rest/v1/schedule_courses?schedule_id=eq." + scheduleId
                        + "&course_id=eq." + courseId);

                List<Course> remaining = loadCoursesForSchedule(scheduleId);
                int newCredits = remaining.stream().mapToInt(Course::getCredits).sum();
                db.patch("/rest/v1/schedules?id=eq." + scheduleId,
                        "{\"credits\": " + newCredits + "}");

                ctx.status(200).json(Map.of(
                        "success", true,
                        "message", "Course has been successfully removed!",
                        "credits", newCredits));
            }
        });

        // POST /schedule/finalize
        app.post("/schedule/finalize", ctx -> {
            if (useJson) {
                boolean success = student.finalizeSchedule();
                if (!success) {
                    ctx.status(400).json(Map.of("message", "No active schedule to finalize, or it is empty."));
                    return;
                }
                persistence.save(student);
                ctx.json(Map.of(
                        "success", true,
                        "message", "Schedule finalized and saved.",
                        "savedSchedules", student.getSavedSchedules()
                ));
            } else {
                JsonNode schedule = getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active schedule to finalize, or it is empty."));
                    return;
                }

                String scheduleId = schedule.get("id").asText();
                List<Course> courses = loadCoursesForSchedule(scheduleId);

                if (courses.isEmpty()) {
                    ctx.status(400).json(Map.of("message", "No active schedule to finalize, or it is empty."));
                    return;
                }

                db.patch("/rest/v1/schedules?id=eq." + scheduleId,
                        "{\"is_finalized\": true}");
                db.patch("/rest/v1/students?id=eq." + STUDENT_ID,
                        "{\"active_semester\": null}");

                JsonNode saved = db.get("/rest/v1/schedules?student_id=eq."
                        + STUDENT_ID + "&is_finalized=eq.true&select=*");

                ctx.json(Map.of(
                        "success",        true,
                        "message",        "Schedule finalized and saved.",
                        "savedSchedules", buildSavedSchedulesMap(saved)
                ));
            }
        });

        // GET /schedule/all
        app.get("/schedule/all", ctx -> {
            if (useJson) {
                ctx.json(student.getSavedSchedules());
            } else {
                JsonNode saved = db.get("/rest/v1/schedules?student_id=eq."
                        + STUDENT_ID + "&is_finalized=eq.true&select=*");
                ctx.json(buildSavedSchedulesMap(saved));
            }
        });

        // DELETE /schedule/clear
        app.delete("/schedule/clear", ctx -> {
            if (useJson) {
                Schedule schedule = student.getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }
                schedule.clearSchedule();
                persistence.save(student);
                ctx.json(Map.of("success", true, "message", "Schedule cleared."));
            } else {
                JsonNode schedule = getActiveSchedule();
                if (schedule == null) {
                    ctx.status(400).json(Map.of("message", "No active semester set."));
                    return;
                }
                String scheduleId = schedule.get("id").asText();
                db.delete("/rest/v1/schedule_courses?schedule_id=eq." + scheduleId);
                db.patch("/rest/v1/schedules?id=eq." + scheduleId, "{\"credits\": 0}");
                ctx.json(Map.of("success", true, "message", "Schedule cleared."));
            }
        });

        // DELETE /schedule/saved?semester=2024_Fall
        app.delete("/schedule/saved", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null) {
                ctx.status(400).json(Map.of("message", "Missing required query param: semester"));
                return;
            }

            if (useJson) {
                boolean removed = student.getSavedSchedules().remove(semester) != null;
                if (removed) persistence.save(student);
                ctx.status(removed ? 200 : 404).json(Map.of(
                        "success", removed,
                        "message", removed ? "Schedule deleted." : "No saved schedule found for " + semester
                ));
            } else {
                JsonNode rows = db.get("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                        + "&semester=eq." + semester
                        + "&is_finalized=eq.true&select=id");

                if (rows.size() == 0) {
                    ctx.status(404).json(Map.of("success", false,
                            "message", "No saved schedule found for " + semester));
                    return;
                }

                String scheduleId = rows.get(0).get("id").asText();
                db.delete("/rest/v1/schedules?id=eq." + scheduleId);
                ctx.status(200).json(Map.of("success", true, "message", "Schedule deleted."));
            }
        });

        // POST /schedule/load?semester=2023_Fall (DB only)
        app.post("/schedule/load", ctx -> {
            if (useJson) {
                ctx.status(400).json(Map.of("message", "Load not supported in JSON mode."));
                return;
            }

            String semester = ctx.queryParam("semester");
            if (semester == null) {
                ctx.status(400).json(Map.of("message", "Missing required query param: semester"));
                return;
            }

            JsonNode savedRows = db.get("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                    + "&semester=eq." + semester
                    + "&is_finalized=eq.true&select=*");

            if (savedRows.size() == 0) {
                ctx.status(404).json(Map.of("message", "No saved schedule found for " + semester));
                return;
            }

            String savedScheduleId = savedRows.get(0).get("id").asText();
            List<Course> courses = loadCoursesForSchedule(savedScheduleId);

            JsonNode existingForSemester = db.get("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                    + "&semester=eq." + semester
                    + "&is_finalized=eq.false&select=id");
            for (JsonNode row : existingForSemester) {
                db.delete("/rest/v1/schedules?id=eq." + row.get("id").asText());
            }

            ObjectNode node = db.mapper.createObjectNode()
                    .put("student_id",   STUDENT_ID)
                    .put("semester",     semester)
                    .put("is_finalized", false)
                    .put("credits",      savedRows.get(0).get("credits").asInt());

            JsonNode inserted = db.post("/rest/v1/schedules", node.toString(),
                    "return=representation");
            String newScheduleId = inserted.get(0).get("id").asText();

            db.patch("/rest/v1/students?id=eq." + STUDENT_ID,
                    "{\"active_semester\": \"" + semester + "\"}");

            for (Course course : courses) {
                JsonNode courseRows = db.get("/rest/v1/courses?subject=eq." + course.getSubject()
                        + "&number=eq." + course.getNumber()
                        + "&section=eq." + course.getSection()
                        + "&semester=eq." + course.getSemester()
                        + "&select=id");

                if (courseRows.size() == 0) continue;

                String courseId = courseRows.get(0).get("id").asText();
                ObjectNode link = db.mapper.createObjectNode()
                        .put("schedule_id", newScheduleId)
                        .put("course_id",   courseId);
                db.post("/rest/v1/schedule_courses", link.toString(), "");
            }

            ctx.json(Map.of(
                    "success",  true,
                    "semester", semester,
                    "courses",  courses,
                    "credits",  savedRows.get(0).get("credits").asInt()
            ));
        });
    }

    // -------------------------------------------------------------------------
    // Helpers (DB only)
    // -------------------------------------------------------------------------

    private void upsertStudent() throws Exception {
        ObjectNode node = db.mapper.createObjectNode().put("id", STUDENT_ID);
        db.post("/rest/v1/students", node.toString(), "resolution=ignore-duplicates");
    }

    private JsonNode getActiveSchedule() throws Exception {
        JsonNode studentNode = db.get("/rest/v1/students?id=eq." + STUDENT_ID
                + "&select=active_semester");
        if (studentNode.size() == 0) return null;

        JsonNode activeSemesterNode = studentNode.get(0).get("active_semester");
        if (activeSemesterNode == null || activeSemesterNode.isNull()) return null;

        String activeSemester = activeSemesterNode.asText();

        JsonNode rows = db.get("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                + "&semester=eq." + activeSemester
                + "&is_finalized=eq.false&select=*");
        if (rows.size() == 0) return null;
        return rows.get(0);
    }

    private List<Course> loadCoursesForSchedule(String scheduleId) throws Exception {
        JsonNode rows = db.get("/rest/v1/schedule_courses?schedule_id=eq."
                + scheduleId + "&select=course_id,courses(*)");

        List<Course> courses = new ArrayList<>();
        for (JsonNode row : rows) {
            JsonNode courseNode = row.get("courses");
            if (courseNode == null) continue;

            Course course = db.mapper.treeToValue(courseNode, Course.class);

            JsonNode slots = db.get("/rest/v1/time_slots?course_id=eq."
                    + courseNode.get("id").asText() + "&select=*");

            List<TimeSlot> timeSlots = new ArrayList<>();
            for (JsonNode slot : slots) {
                timeSlots.add(new TimeSlot(
                        slot.get("day").asText(),
                        LocalTime.parse(slot.get("start_time").asText()),
                        LocalTime.parse(slot.get("end_time").asText())
                ));
            }
            course.setTimes(timeSlots);
            courses.add(course);
        }
        return courses;
    }

    private Map<String, Object> buildSavedSchedulesMap(JsonNode rows) throws Exception {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        for (JsonNode row : rows) {
            String key = row.get("semester").asText();
            String scheduleId = row.get("id").asText();
            List<Course> courses = loadCoursesForSchedule(scheduleId);
            map.put(key, Map.of(
                    "schedule", courses,
                    "credits",  row.get("credits").asInt()
            ));
        }
        return map;
    }
}