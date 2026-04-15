package edu.gcc.future_millionaires;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

public class RateMyProfApi {
    private final String schoolGraphQLId;
    private final Map<Integer, Professor> professors = new HashMap<>();

    public RateMyProfApi(String schoolGraphQLId) {
        this.schoolGraphQLId = schoolGraphQLId;
        loadAllProfessors();
    }

    //POST GraphQL
    private String postGraphQL(String query, JSONObject variables) throws IOException {
        URL url = new URL("https://www.ratemyprofessors.com/graphql");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        JSONObject body = new JSONObject();
        body.put("query", query);
        if (variables != null) body.put("variables", variables);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) response.append(line);
        }
        return response.toString();
    }

    //find school ID
    public static String findSchoolGraphQLId(String schoolName) {
        try {
            String query = """
                query NewSearchSchoolsQuery($query: SchoolSearchQuery!) {
                  newSearch {
                    schools(query: $query) {
                      edges {
                        node {
                          id
                          legacyId
                          name
                        }
                      }
                    }
                  }
                }
                """;

            JSONObject variables = new JSONObject();
            variables.put("query", new JSONObject().put("text", schoolName));

            String response = new RateMyProfApi("dummy").postGraphQL(query, variables);
            JSONObject json = new JSONObject(response);

            JSONArray edges = json.getJSONObject("data")
                    .getJSONObject("newSearch")
                    .getJSONObject("schools")
                    .getJSONArray("edges");

            if (edges.length() > 0) {
                JSONObject node = edges.getJSONObject(0).getJSONObject("node");
                System.out.println("✅ Found: " + node.getString("name") +
                        " | GraphQL ID: " + node.getString("id"));
                return node.getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("School not found: " + schoolName);
    }

    //load all professors
    private void loadAllProfessors() {
        try {
            String query = """
                query NewSearchTeachers($query: TeacherSearchQuery!, $first: Int!, $after: String) {
                  newSearch {
                    teachers(query: $query, first: $first, after: $after) {
                      edges {
                        node {
                          id
                          legacyId
                          firstName
                          lastName
                          numRatings
                          avgRating
                          avgDifficulty
                          department
                        }
                      }
                      pageInfo {
                        hasNextPage
                        endCursor
                      }
                    }
                  }
                }
                """;

            JSONObject variables = new JSONObject();
            JSONObject searchQuery = new JSONObject()
                    .put("text", "")
                    .put("schoolID", schoolGraphQLId)
                    .put("fallback", true);

            variables.put("query", searchQuery);
            variables.put("first", 500);   // ← higher limit = fewer requests
            variables.put("after", JSONObject.NULL);   // first page

            boolean hasNext = true;
            int totalLoaded = 0;

            while (hasNext) {
                String responseStr = postGraphQL(query, variables);
                JSONObject json = new JSONObject(responseStr);

                JSONObject teachersObj = json.getJSONObject("data")
                        .getJSONObject("newSearch")
                        .getJSONObject("teachers");

                JSONArray edges = teachersObj.getJSONArray("edges");

                for (int i = 0; i < edges.length(); i++) {
                    JSONObject node = edges.getJSONObject(i).getJSONObject("node");

                    Professor p = new Professor(
                            node.getString("id"),
                            node.getInt("legacyId"),
                            node.getString("firstName"),
                            node.getString("lastName"),
                            node.getInt("numRatings"),
                            node.optDouble("avgRating", 0.0),
                            node.optDouble("avgDifficulty", 0.0),
                            node.optString("department", "")
                    );

                    professors.put(p.getLegacyId(), p);
                    totalLoaded++;
                }

                JSONObject pageInfo = teachersObj.getJSONObject("pageInfo");
                hasNext = pageInfo.getBoolean("hasNextPage");

                if (hasNext) {
                    variables.put("after", pageInfo.getString("endCursor"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Professor> getProfessors() {
        return Collections.unmodifiableMap(professors);
    }

    public Professor getProfessorByLastName(String lastName) {
        String lower = lastName.toLowerCase().trim();
        for (Professor p : professors.values()) {
            if (p.getLastName().toLowerCase().trim().equals(lower)) {
                return p;
            }
        }
        return null;
    }
}