package edu.gcc.future_millionaires;

import io.javalin.Javalin;

import java.util.Map;

public class ScheduleController {

    public ScheduleController(Javalin app, Student student, CourseList courseList, PersistenceManager persistence) {

        // GET /schedule
        // Returns the active semester's live schedule, or 400 if no semester is set.
        app.get("/schedule", ctx -> {
            Schedule schedule = student.getActiveSchedule();
            if (schedule == null) {
                ctx.status(400).json(Map.of("message", "No active semester set."));
                return;
            }
            ctx.json(Map.of(
                    "courses", schedule.getSchedule(),
                    "credits", schedule.getCredits()
            ));
        });

        // POST /schedule/semester?semester=2024_Fall
        // Sets the active semester, creating a new live schedule if one doesn't exist yet.
        app.post("/schedule/semester", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null) {
                ctx.status(400).json(Map.of("message", "Missing required query param: semester"));
                return;
            }
            student.setActiveSemester(semester);
            persistence.save(student);
            ctx.json(Map.of(
                    "message", "Active semester set to " + semester,
                    "semester", semester
            ));
        });

        // POST /schedule/add?subject=COMP&number=422&section=A
        // Finds the matching course and adds it to the active live schedule.
        app.post("/schedule/add", ctx -> {
            Schedule schedule = student.getActiveSchedule();
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

            Course toAdd = courseList.getCourses().stream()
                    .filter(c ->
                            c.getSubject().equals(subject) &&
                                    c.getNumber() == number &&
                                    c.getSection().equals(section) &&
                                    c.getSemester().equals(student.getActiveSemester()))
                    .findFirst()
                    .orElse(null);

            if (toAdd == null) {
                ctx.status(404).json(Map.of("message",
                        "Course not found: " + subject + " " + number + " section " + section));
                return;
            }

            boolean success = schedule.addCourse(toAdd);
            if (success) persistence.save(student);

            ctx.status(success ? 200 : 409).json(Map.of(
                    "success", success,
                    "message", schedule.getUserMessage(),
                    "credits", schedule.getCredits()
            ));
        });

        // DELETE /schedule/remove?subject=COMP&number=422&section=A
        // Removes the matching course from the active live schedule.
        app.delete("/schedule/remove", ctx -> {
            Schedule schedule = student.getActiveSchedule();
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

            boolean success = schedule.removeCourse(subject, number, section);
            if (success) persistence.save(student);

            ctx.status(success ? 200 : 404).json(Map.of(
                    "success", success,
                    "message", schedule.getUserMessage(),
                    "credits", schedule.getCredits()
            ));
        });

        // POST /schedule/finalize
        // Archives the active schedule into savedSchedules and clears it from the live map.
        app.post("/schedule/finalize", ctx -> {
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
        });

        // GET /schedule/all
        // Returns all finalized (archived) schedules for the Saved Schedules panel.
        app.get("/schedule/all", ctx -> {
            ctx.json(student.getSavedSchedules());
        });
    }
}