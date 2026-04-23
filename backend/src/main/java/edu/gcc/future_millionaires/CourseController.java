package edu.gcc.future_millionaires;

import io.javalin.Javalin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        app.get("/courses/meta", ctx -> {
            List<Course> courses = courseList.getCourses();
            Map<String, Object> meta = new HashMap<>();

            meta.put("departments", courses.stream()
                    .map(Course::getSubject)
                    .filter(s -> s != null)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList()));

            meta.put("numbers", courses.stream()
                    .mapToInt(Course::getNumber)
                    .distinct()
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList()));

            meta.put("semesters", courses.stream()
                    .map(Course::getSemester)
                    .filter(s -> s != null)
                    .distinct()
                    .sorted()
                    .map(s -> {
                        // convert "2023_Fall" -> { value: "2023_Fall", label: "Fall 2023" }
                        String[] parts = s.split("_", 2);
                        String label = parts.length == 2 ? parts[1].replace("_", " ") + " " + parts[0] : s;
                        Map<String, String> entry = new HashMap<>();
                        entry.put("value", s);
                        entry.put("label", label);
                        return entry;
                    })
                    .collect(Collectors.toList()));

            meta.put("credits", courses.stream()
                    .mapToInt(Course::getCredits)
                    .distinct()
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList()));

            meta.put("professors", courses.stream()
                    .flatMap(c -> c.getProfessors().stream())
                    .map(Professor::getName)
                    .filter(n -> n != null && !n.trim().isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList()));

            ctx.json(meta);
        });
    }
}