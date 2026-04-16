package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {

        CourseList courseList = new CourseList();
        Student student = new Student(1);

        // Load persisted schedules from disk before starting the server
        PersistenceManager persistence = new PersistenceManager("student_data.json");
        persistence.load(student);

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:5173");
                });
            });
        }).start(7070);

        new CourseController(app, courseList);
        new ScheduleController(app, student, courseList, persistence);
        new EmailController(app);
    }
}