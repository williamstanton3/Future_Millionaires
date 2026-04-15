package edu.gcc.future_millionaires;

import io.javalin.Javalin;

import java.util.Map;

public class EmailController {

    public EmailController(Javalin app) {

        // POST /email-professor?professor=Last, First M.&course=CourseName
        app.post("/email-professor", ctx -> {

            String professorName = ctx.queryParam("professor");
            String course = ctx.queryParam("course");

            if (professorName == null || course == null) {
                ctx.status(400).json(Map.of(
                        "message", "Missing required query params: professor, course"
                ));
                return;
            }

            String email = EmailAddressBuilder.generateEmail(professorName);

            String subject = "Question about " + course;
            String message =
                    "Hello Professor " + professorName + ",\n\n" +
                            "INSERT QUESTION HERE" + course + ".\n\n" +
                            "Thank you,\nNAME";

            try {
                EmailService.sendEmail(email, subject, message);

                ctx.json(Map.of(
                        "success", true,
                        "message", "Email sent successfully",
                        "email", email
                ));

            } catch (Exception e) {
                ctx.status(500).json(Map.of(
                        "success", false,
                        "message", "Failed to send email"
                ));
            }
        });
    }
}