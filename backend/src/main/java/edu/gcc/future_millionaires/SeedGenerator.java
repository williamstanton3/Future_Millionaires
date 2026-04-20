package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;

public class SeedGenerator {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        InputStream input = SeedGenerator.class.getClassLoader()
                .getResourceAsStream("data_wolfe.json");
        JsonNode root = mapper.readTree(input);
        JsonNode classes = root.get("classes");

        PrintWriter out = new PrintWriter(new FileWriter("supabase/seed.sql"));

        for (JsonNode c : classes) {
            String subject    = escape(c.get("subject").asText());
            String name       = escape(c.get("name").asText());
            String section    = escape(c.get("section").asText());
            String location   = escape(c.get("location").asText());
            String semester   = escape(c.get("semester").asText());
            int number        = c.get("number").asInt();
            int credits       = c.get("credits").asInt();
            int openSeats     = c.get("open_seats").asInt();
            int totalSeats    = c.get("total_seats").asInt();
            boolean isLab     = c.get("is_lab").asBoolean();
            boolean isOpen    = c.get("is_open").asBoolean();

            // Build faculty array string
            StringBuilder faculty = new StringBuilder("ARRAY[");
            JsonNode facultyNode = c.get("faculty");
            for (int i = 0; i < facultyNode.size(); i++) {
                if (i > 0) faculty.append(",");
                faculty.append("'").append(escape(facultyNode.get(i).asText())).append("'");
            }
            faculty.append("]");

            out.println("INSERT INTO courses (subject, number, section, name, credits, location, semester, is_lab, is_open, open_seats, total_seats, faculty)");
            out.println("VALUES ('" + subject + "', " + number + ", '" + section + "', '" + name + "', " + credits + ", '" + location + "', '" + semester + "', " + isLab + ", " + isOpen + ", " + openSeats + ", " + totalSeats + ", " + faculty + ")");
            out.println("ON CONFLICT (subject, number, section, semester) DO NOTHING");
            out.println("RETURNING id;");

            // Time slots are trickier in pure SQL since we need the ID back
            // We use a DO block with a variable to capture it
            JsonNode times = c.get("times");
            if (times != null && times.isArray() && times.size() > 0) {
                out.println("DO $$");
                out.println("DECLARE v_id uuid;");
                out.println("BEGIN");
                out.println("  SELECT id INTO v_id FROM courses WHERE subject='" + subject + "' AND number=" + number + " AND section='" + section + "' AND semester='" + semester + "';");
                for (JsonNode t : times) {
                    String day   = t.get("day").asText();
                    String start = t.get("start_time").asText();
                    String end   = t.get("end_time").asText();
                    out.println("  INSERT INTO time_slots (course_id, day, start_time, end_time) VALUES (v_id, '" + day + "', '" + start + "', '" + end + "') ON CONFLICT DO NOTHING;");
                }
                out.println("END $$;");
            }

            out.println();
        }

        out.close();
        System.out.println("seed.sql generated in supabase/seed.sql");
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("'", "''");
    }
}
