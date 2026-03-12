package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class CourseController {

    public CourseController(Javalin app, CourseList courseList) {

        app.get("/courses", ctx -> {
            Filter filter = new Filter();

            String courseCode = ctx.queryParam("courseCode");
            if (courseCode != null) {
                // expects "COMP 422" — split into subject + number
                String[] parts = courseCode.split(" ");
                if (parts.length == 2) {
                    try {
                        filter.setCourseCode(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        ctx.status(400).result("Invalid courseCode format. Expected format: 'COMP 422'");
                        return;
                    }
                } else {
                    ctx.status(400).result("Invalid courseCode format. Expected format: 'COMP 422'");
                    return;
                }
            }

            String department = ctx.queryParam("department");
            if (department != null) filter.setDepartment(department);

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