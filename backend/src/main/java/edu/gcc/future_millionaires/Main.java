package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {

        CourseList courseList = new CourseList();
        SupabaseClient db = new SupabaseClient();

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:5173");
                });
            });
        }).start(7070);

        new CourseController(app, courseList);
        new ScheduleController(app, courseList, db);
    }
}