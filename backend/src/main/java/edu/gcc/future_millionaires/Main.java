package edu.gcc.future_millionaires;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {

        // Set to true to use JSON file, false to use Supabase
        boolean useJson = true;

        RateMyProfApi rmpApi = new RateMyProfApi("U2Nob29sLTM4NA==");
        CourseList courseList = new CourseList(rmpApi);
        Student student = useJson ? new Student(1) : null;
        SupabaseClient db = useJson ? null : new SupabaseClient();

        // Load persisted schedules from disk before starting the server
        PersistenceManager persistence = useJson ? new PersistenceManager("student_data.json") : null;

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:5173");
                });
            });
        }).start(7070);

        if (useJson) {
            persistence.load(student);
        }
        new CourseController(app, courseList);
        new ScheduleController(app, courseList, student, persistence, db, useJson);
        new EmailController(app);
    }
}