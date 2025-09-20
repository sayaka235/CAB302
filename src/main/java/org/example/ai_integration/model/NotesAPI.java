package org.example.ai_integration.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * API utility class for handling note summaries with Gemini.
 * <p>
 * Responsible for building prompts, sending requests to Gemini,
 * parsing responses, and extracting usable summaries from the JSON output.
 */
public class NotesAPI {

    /**
     * Gets the Gemini API key from environment variables.
     * <p>
     * Throws an error if the key is missing.
     *
     * @return the Gemini API key as a string
     */
    private static String getGeminiApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Missing GEMINI_API_KEY (set it as an environment variable).");
        }
        return key.trim();
    }

    /** OkHttp client for sending HTTP requests */
    private static final OkHttpClient client = new OkHttpClient();
    /** Gson instance for JSON parsing */
    private static final Gson gson = new Gson();

    /**
     * Parses the raw response from Gemini into a summary string.
     * <p>
     * Handles different possible JSON formats returned by the model.
     *
     * @param raw the raw string returned by Gemini
     * @return a parsed summary string
     */
    public static String parseSummary(String raw) {
        try {
            final com.google.gson.JsonElement root = com.google.gson.JsonParser.parseString(raw);

            if (root.isJsonObject() && root.getAsJsonObject().has("candidates")) {
                String text = extractTextFromGemini(root.getAsJsonObject());
                String jsonPayload = extractJsonArrayString(text);
                return parseJson(jsonPayload);
            }

            if (root.isJsonPrimitive()) {
                String text = root.getAsJsonPrimitive().getAsString();
                String jsonPayload = extractJsonArrayString(text);
                return parseJson(jsonPayload);
            }

            if (root.isJsonArray()) {
                return parseJson(root.toString());
            }

            String jsonPayload = extractJsonArrayString(raw);
            return parseJson(jsonPayload);

        } catch (Exception e) {
            String preview = raw == null ? "null" : raw.substring(0, Math.min(400, raw.length())).replace("\n","\\n");
            throw new RuntimeException("Could not parse MCQ array. Preview of payload: " + preview, e);
        }
    }

    /**
     * Extracts plain text content from a Gemini JSON response.
     *
     * @param obj the Gemini response JSON object
     * @return the text field of the first candidate
     */
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

    /**
     * Extracts the JSON object or array string from text, removing markdown fences if present.
     *
     * @param text the raw text string
     * @return a substring containing valid JSON
     */
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

    /**
     * Builds a prompt to send to Gemini for generating a summary.
     *
     * @param content the raw content to be summarized
     * @return the constructed prompt string
     */
    private static String buildPrompt(String content) {
        return  "Create a notes summary from the content below. Word limit 200.\n\n" +
                "\"Output STRICTLY as raw JSON (no prose, no markdown fences):\\n\\n\""+
                "{\n"+
                "\"title\": \"string\",\n"+
                "\"summary\": \"string\",\n"+
                "\n}"+ content;
    }

    /**
     * Sends a request to Gemini to generate a summary for given content.
     *
     * @param content the content to summarize
     * @return the raw JSON response from Gemini, or an error message
     */
    public static String generateSummary(String content) {
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

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(80, TimeUnit.SECONDS) // Set read timeout to 80 seconds
                .build();

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent")
                .addHeader("x-goog-api-key", getGeminiApiKey())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("DEBUG: " + response.code() + " - " + responseBody);
            if (!response.isSuccessful()) return "Error from Gemini: " + responseBody;
            return responseBody;
        }
        catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Parses a JSON string into a {@link Content} object and returns its summary.
     *
     * @param json the JSON string containing title and summary
     * @return the summary text
     */
    public static String parseJson(String json) {
        Gson gson = new Gson();
        Content content = gson.fromJson(json, Content.class);
        return content.getSummary();
    }

    /**
     * Inner class representing a note summary object returned from Gemini.
     */
    public static class Content {
        /** The title of the summarized content */
        String title;
        /** The summary text of the content */
        String summary;

        /** @return the title of the content */
        public String getTitle() { return title; }
        /** @param title sets the title of the content */
        public void setTitle(String title) { this.title = title; }
        /** @return the summary text */
        public String getSummary() { return summary; }
        /** @param summary sets the summary text */
        public void setSummary(String summary) { this.summary = summary; }
    }
}
