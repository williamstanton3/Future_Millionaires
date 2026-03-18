package edu.gcc.future_millionaires;

import io.javalin.Javalin;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        CourseList courseList = new CourseList();
        Student student = new Student(1);

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:5173");
                });
            });
        }).start(7070);

        new CourseController(app, courseList);
        new ScheduleController(app, student, courseList);
        new EmailController(app);
    }
}