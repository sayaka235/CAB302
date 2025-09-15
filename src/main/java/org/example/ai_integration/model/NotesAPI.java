package org.example.ai_integration.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

/*public class NotesAPI {
    private static String getGeminiApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Missing GEMINI_API_KEY (set it as an environment variable).");
        }
        return key.trim();
    }
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    public static class NoteItem
    {
        public String notes;
    }

    public static String generateNotes(String content)
    {
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
    public static String parseNotesArray(String raw)
    {
        try {
            final com.google.gson.JsonElement root = com.google.gson.JsonParser.parseString(raw);

            if (root.isJsonObject() && root.getAsJsonObject().has("candidates")) {
                String text = extractTextFromGemini(root.getAsJsonObject());
                String jsonPayload = extractJsonArrayString(text);
                return parseArray(jsonPayload);
            }

            if (root.isJsonPrimitive()) {
                String text = root.getAsJsonPrimitive().getAsString();
                String jsonPayload = extractJsonArrayString(text);
                return parseArray(jsonPayload);
            }

            if (root.isJsonArray()) {
                return parseArray(root.toString());
            }

            String jsonPayload = extractJsonArrayString(raw);
            return parseArray(jsonPayload);

        } catch (Exception e) {
            String preview = raw == null ? "null" : raw.substring(0, Math.min(400, raw.length())).replace("\n","\\n");
            throw new RuntimeException("Could not parse Notes array. Preview of payload: " + preview, e);
        }
    }
    private static String parseArray(String jsonArray) {
        return gson.fromJson(jsonArray, String.class);
    }
    private static String buildPrompt(String content)
    {
        return "Create a notes summary from the content below.\n\n" +
                        "Output STRICTLY as raw JSON (no prose, no markdown fences):\n\n" +
                        "{\n" +
                        "  {\n" +
                        "    \"string\",\n" +
                        "  }\n" +
                        "}\n\n" + content;
    }
    private static String extractTextFromGemini(com.google.gson.JsonObject obj) {
        var candidates = obj.getAsJsonArray("candidates");
        if (candidates == null || candidates.size() == 0)
            throw new IllegalArgumentException("No candidates in Gemini response.");

        var c0 = candidates.get(0).getAsJsonObject();

        var content = c0.getAsJsonObject("content");
        if (content == null) throw new IllegalArgumentException("Missing 'content' in candidate.");

        var parts = content.getAsJsonArray("parts");
        if (parts == null || parts.size() == 0)
            throw new IllegalArgumentException("No 'parts' in content.");

        var p0 = parts.get(0).getAsJsonObject();
        if (!p0.has("text"))
            throw new IllegalArgumentException("First part has no 'text' field.");

        return p0.get("text").getAsString();
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
}
*/