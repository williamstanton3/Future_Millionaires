package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class CourseController {

    public CourseController(Javalin app, CourseList courseList) {

        app.get("/courses", ctx -> {
            Filter filter = new Filter();

            String subject = ctx.queryParam("subject");
            if (subject != null) filter.setSubject(subject);

            String professor = ctx.queryParam("professor");
            if (professor != null) filter.setProfessor(professor);

            String semester = ctx.queryParam("semester");
            if (semester != null) filter.setSemester(semester);

            String creditsParam = ctx.queryParam("credits");
            if (creditsParam != null) {
                try {
                    filter.setCredits(Integer.parseInt(creditsParam));
                } catch (NumberFormatException e) {
                    ctx.status(400).result("Invalid credits value: must be an integer");
                    return;
                }
            }

            String[] days = ctx.queryParams("days").toArray(new String[0]);
            if (days.length > 0) filter.setDays(days);

            ctx.json(filter.apply(courseList.getCourses()));
        });
    }
}
