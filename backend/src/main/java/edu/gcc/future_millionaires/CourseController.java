package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class CourseController {

    public CourseController(Javalin app, CourseList courseList) {

        app.get("/courses", ctx ->
                ctx.json(courseList.getCourses())
        );
    }
}
