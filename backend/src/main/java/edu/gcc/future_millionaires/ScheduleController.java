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

    public ScheduleController(Javalin app, CourseList courseList, SupabaseClient db) {
        this.db = db;
        this.courseList = courseList;

        // GET /schedule
        app.get("/schedule", ctx -> {
            JsonNode schedule = getActiveSchedule();
            if (schedule == null) {
                ctx.status(400).json(Map.of("message", "No active semester set."));
                return;
            }

            String scheduleId = schedule.get("id").asText();
            int credits = schedule.get("credits").asInt();
            List<Course> courses = loadCoursesForSchedule(scheduleId);

            ctx.json(Map.of("courses", courses, "credits", credits));
        });

        // POST /schedule/semester?semester=2024_Fall
        app.post("/schedule/semester", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null) {
                ctx.status(400).json(Map.of("message", "Missing required query param: semester"));
                return;
            }

            // Ensure student exists
            upsertStudent();

            // Clear any existing active semester
            db.patch("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                            + "&is_finalized=eq.false",
                    "{\"active_semester\": false}");

            // Upsert this semester's schedule and mark it active
            ObjectNode node = db.mapper.createObjectNode()
                    .put("student_id",      STUDENT_ID)
                    .put("semester",        semester)
                    .put("is_finalized",    false)
                    .put("active_semester", true)
                    .put("credits",         0);

            db.post("/rest/v1/schedules", node.toString(),
                    "resolution=ignore-duplicates");

            // Re-activate if it already existed
            db.patch("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                            + "&semester=eq." + semester,
                    "{\"active_semester\": true}");

            ctx.json(Map.of("message", "Active semester set to " + semester, "semester", semester));
        });

        // POST /schedule/add?subject=COMP&number=422&section=A
        app.post("/schedule/add", ctx -> {
            JsonNode schedule = getActiveSchedule();
            if (schedule == null) {
                ctx.status(400).json(Map.of("message", "No active semester set."));
                return;
            }

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

            String scheduleId = schedule.get("id").asText();
            String semester   = schedule.get("semester").asText();

            // Find the course in CourseList
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

            // Load existing courses to check for conflicts
            List<Course> existing = loadCoursesForSchedule(scheduleId);

            // Build a temporary schedule to reuse your existing logic
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

            // Look up course ID in DB
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

            // Insert into schedule_courses
            ObjectNode link = db.mapper.createObjectNode()
                    .put("schedule_id", scheduleId)
                    .put("course_id",   courseId);
            db.post("/rest/v1/schedule_courses", link.toString(), "");

            // Update credits
            int newCredits = tempSchedule.getCredits();
            db.patch("/rest/v1/schedules?id=eq." + scheduleId,
                    "{\"credits\": " + newCredits + "}");

            ctx.status(200).json(Map.of(
                    "success", true,
                    "message", tempSchedule.getUserMessage(),
                    "credits", newCredits));
        });

        // DELETE /schedule/remove?subject=COMP&number=422&section=A
        app.delete("/schedule/remove", ctx -> {
            JsonNode schedule = getActiveSchedule();
            if (schedule == null) {
                ctx.status(400).json(Map.of("message", "No active semester set."));
                return;
            }

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

            String scheduleId = schedule.get("id").asText();
            String semester   = schedule.get("semester").asText();

            // Look up course ID
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

            // Delete the link
            db.delete("/rest/v1/schedule_courses?schedule_id=eq." + scheduleId
                    + "&course_id=eq." + courseId);

            // Recalculate credits
            List<Course> remaining = loadCoursesForSchedule(scheduleId);
            int newCredits = remaining.stream().mapToInt(Course::getCredits).sum();
            db.patch("/rest/v1/schedules?id=eq." + scheduleId,
                    "{\"credits\": " + newCredits + "}");

            ctx.status(200).json(Map.of(
                    "success", true,
                    "message", "Course has been successfully removed!",
                    "credits", newCredits));
        });

        // POST /schedule/finalize
        app.post("/schedule/finalize", ctx -> {
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
                    "{\"is_finalized\": true, \"active_semester\": false}");

            JsonNode saved = db.get("/rest/v1/schedules?student_id=eq."
                    + STUDENT_ID + "&is_finalized=eq.true&select=*");

            ctx.json(Map.of("success", true, "message", "Schedule finalized and saved.",
                    "savedSchedules", saved));
        });

        // GET /schedule/all
        app.get("/schedule/all", ctx -> {
            JsonNode saved = db.get("/rest/v1/schedules?student_id=eq."
                    + STUDENT_ID + "&is_finalized=eq.true&select=*");
            ctx.json(saved);
        });

        // DELETE /schedule/clear
        app.delete("/schedule/clear", ctx -> {
            JsonNode schedule = getActiveSchedule();
            if (schedule == null) {
                ctx.status(400).json(Map.of("message", "No active semester set."));
                return;
            }

            String scheduleId = schedule.get("id").asText();
            db.delete("/rest/v1/schedule_courses?schedule_id=eq." + scheduleId);
            db.patch("/rest/v1/schedules?id=eq." + scheduleId, "{\"credits\": 0}");

            ctx.json(Map.of("success", true, "message", "Schedule cleared."));
        });

        // DELETE /schedule/saved?semester=2024_Fall
        app.delete("/schedule/saved", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null) {
                ctx.status(400).json(Map.of("message", "Missing required query param: semester"));
                return;
            }

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
        });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void upsertStudent() throws Exception {
        ObjectNode node = db.mapper.createObjectNode().put("id", STUDENT_ID);
        db.post("/rest/v1/students", node.toString(), "resolution=ignore-duplicates");
    }

    private JsonNode getActiveSchedule() throws Exception {
        JsonNode rows = db.get("/rest/v1/schedules?student_id=eq." + STUDENT_ID
                + "&active_semester=eq.true&is_finalized=eq.false&select=*");
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
}