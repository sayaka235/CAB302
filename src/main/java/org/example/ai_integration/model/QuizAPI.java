package org.example.ai_integration.model;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;

public class QuizAPI
{
    private static String getGeminiApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Missing GEMINI_API_KEY (set it as an environment variable).");
        }
        return key.trim();
    }
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static class McqItem
    {
        public String question;
        public java.util.List<String> options;
        public int correct_index;
    }

    public static java.util.List<McqItem> parseMcqArray(String raw)
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
            throw new RuntimeException("Could not parse MCQ array. Preview of payload: " + preview, e);
        }
    }


    public static String generateQuiz(String content, String quizType, int numQuestions)
    {
        String prompt = buildPrompt(content, quizType, numQuestions);

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

        int arrStart = t.indexOf('[');
        int arrEnd   = t.lastIndexOf(']');
        if (arrStart >= 0 && arrEnd > arrStart) {
            t = t.substring(arrStart, arrEnd + 1).trim();
        }

        return t;
    }

    private static java.util.List<QuizAPI.McqItem> parseArray(String jsonArray) {
        java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<java.util.List<QuizAPI.McqItem>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }

    private static String buildPrompt(String content, String quizType, int numQuestions)
    {
        switch (quizType)
        {
            case "Multiple Choice":
                return "Create a " + numQuestions + "-question MULTIPLE CHOICE quiz from the content below.\n\n" +
                        "Output STRICTLY as raw JSON (no prose, no markdown fences):\n\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"question\": \"string\",\n" +
                        "    \"options\": [\"opt1\",\"opt2\",\"opt3\",\"opt4\"],   // exactly 4\n" +
                        "    \"correct_index\": 1                                   // 1..4 (1-based)\n" +
                        "  }\n" +
                        "]\n\n" + content;

            case "True/False":
                return "Create a " + numQuestions + "-question TRUE/FALSE quiz from the following content.\n" +
                        "Each question should have a 'question' field and an 'answer' field with either 'True' or 'False'.\n\n" +
                        content;

            case "Fill in the Blank":
                return "Create a " + numQuestions + "-question FILL-IN-THE-BLANK quiz from the following content.\n" +
                        "Use a blank (____) in each sentence and provide the correct word as the 'answer'.\n\n" +
                        content;

            default:
                return "Please select a valid quiz type.";
        }
    }
}
