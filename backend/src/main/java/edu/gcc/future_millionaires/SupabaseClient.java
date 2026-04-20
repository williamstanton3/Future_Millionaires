package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseClient {

    private static final String SUPABASE_URL = "http://127.0.0.1:54321";
    private static final String SUPABASE_KEY = "sb_secret_N7UND0UgjKTVK-Uodkm0Hg_xSvEMPvz";

    private final HttpClient client = HttpClient.newHttpClient();
    final ObjectMapper mapper = new ObjectMapper();

    public JsonNode get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + path))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .GET()
                .build();
        String body = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
        return mapper.readTree(body);
    }

    public JsonNode post(String path, String body, String prefer) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + path))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json");
        if (prefer != null && !prefer.isEmpty())
            builder.header("Prefer", prefer);
        builder.POST(HttpRequest.BodyPublishers.ofString(body));
        String res = client.send(builder.build(), HttpResponse.BodyHandlers.ofString()).body();
        return mapper.readTree(res);
    }

    public void patch(String path, String body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + path))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    public void delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + path))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .DELETE()
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}