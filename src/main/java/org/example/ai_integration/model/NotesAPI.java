package org.example.ai_integration.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class NotesAPI {
    private static String getGeminiApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Missing GEMINI_API_KEY (set it as an environment variable).");
        }
        return key.trim();
    }
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    public static String parseSummary(String raw){
        String jsonPayload = extractJsonArrayString(raw);
        return parseJson(jsonPayload);
    }
    private static String extractJsonArrayString(String text) {
        if (text == null) throw new IllegalArgumentException("Empty text from model.");

        String t = text.trim();

        int fenceStart = t.indexOf("```");
        if (fenceStart >= 0) {
            int firstNL = t.indexOf('\n', fenceStart);
            int fenceEnd = t.indexOf("```", Math.max(fenceStart + 3, firstNL >= 0 ? firstNL + 1 : fenceStart + 3));
            if (firstNL >= 0 && fenceEnd > firstNL) {
                t = t.substring(firstNL + 1, fenceEnd).trim();
            }
        }

        int arrStart = t.indexOf('{');
        int arrEnd   = t.lastIndexOf('}');
        if (arrStart >= 0 && arrEnd > arrStart) {
            t = t.substring(arrStart, arrEnd + 1).trim();
        }

        return t;
    }
    private static String buildPrompt(String content){
        return  "Create a notes summary from the content below. Max tokens 500.\n\n" +
                "\"Output STRICTLY as raw JSON (no prose, no markdown fences):\\n\\n\""+
                "{\n"+
                "\"title\": \"string\",\n"+
                "\"summary\": \"string\",\n"+
                "\n}"+ content;
    }
    public static String generateSummary(String content){
        String prompt = buildPrompt(content);

        JsonArray contentsArray = new JsonArray();
        JsonObject partsWrapper = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);
        partsWrapper.add("parts", parts);
        contentsArray.add(partsWrapper);

        JsonObject req = new JsonObject();
        req.add("contents", contentsArray);
        RequestBody body = RequestBody.create(gson.toJson(req), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent")
                .addHeader("x-goog-api-key", getGeminiApiKey())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("DEBUG: " + response.code() + " - " + responseBody);
            if (!response.isSuccessful()) return "Error from Gemini: " + responseBody;
            return responseBody;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

    }
    public static String parseJson(String json) {
        Gson gson = new Gson();
        Content content = gson.fromJson(json, Content.class);
        return content.getSummary();
    }
    public static class Content {
        String title;
        String summary;


        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }
}




