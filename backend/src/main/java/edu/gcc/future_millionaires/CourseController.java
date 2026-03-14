package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class CourseController {

    public CourseController(Javalin app, CourseList courseList) {

        app.get("/courses", ctx -> {
            Filter filter = new Filter();

            String department = ctx.queryParam("department");
            if (department != null) filter.setDepartment(department);

            String numberParam = ctx.queryParam("number");
            if (numberParam != null) {
                try {
                    filter.setNumber(Integer.parseInt(numberParam));
                } catch (NumberFormatException e) {
                    ctx.status(400).result("Invalid number value: must be an integer");
                    return;
                }
            }

            String[] professors = ctx.queryParams("professors").toArray(new String[0]);
            if (professors.length > 0) filter.setProfessors(professors);

            String[] times = ctx.queryParams("times").toArray(new String[0]);
            if (times.length > 0) filter.setTimes(times);

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

            String keyword = ctx.queryParam("keyword");
            if (keyword != null) filter.setKeyword(keyword);

            ctx.json(filter.apply(courseList.getCourses()));
        });
    }
}