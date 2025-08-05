package org.example.ai_integration;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;

public class QuizAPI {
    private static final String API_KEY = "AIzaSyAMCztiPTzwmxVrlY7Q3uwwYBVtBaw2aCM";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static String generateQuiz(String topic) {
        JsonObject contents = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        // Updated prompt: summarization + quiz
        part.addProperty("text",
                "Summarize the following content first, then generate 5 multiple-choice questions (4 options each) with correct answers. " +
                        "Format strictly as JSON with 'question', 'options', and 'answer' fields.\n\n" + topic
        );
        parts.add(part);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contentsArray = new JsonArray();
        contentsArray.add(content);

        contents.add("contents", contentsArray);

        RequestBody body = RequestBody.create(
                gson.toJson(contents),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("DEBUG: " + response.code() + " - " + responseBody);
            if (!response.isSuccessful()) return "Error from Gemini: " + responseBody;
            return parseResponse(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private static String parseResponse(String json) {
        JsonObject root = gson.fromJson(json, JsonObject.class);
        try {
            String textBlock = root.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();

            // Remove the ```json ``` wrapper
            textBlock = textBlock.replace("```json", "").replace("```", "").trim();

            JsonObject parsed = gson.fromJson(textBlock, JsonObject.class);
            StringBuilder formatted = new StringBuilder();

            if (parsed.has("summary")) {
                formatted.append("Summary:\n").append(parsed.get("summary").getAsString()).append("\n\n");
            }

            if (parsed.has("questions")) {
                int i = 1;
                for (JsonElement qElem : parsed.getAsJsonArray("questions")) {
                    JsonObject qObj = qElem.getAsJsonObject();
                    formatted.append(i++).append(". ").append(qObj.get("question").getAsString()).append("\n");
                    JsonArray options = qObj.getAsJsonArray("options");
                    char optionLabel = 'A';
                    for (JsonElement opt : options) {
                        formatted.append("   ").append(optionLabel++).append(") ").append(opt.getAsString()).append("\n");
                    }
                    formatted.append("Answer: ").append(qObj.get("answer").getAsString()).append("\n\n");
                }
            }

            return formatted.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to parse response: " + json;
        }
    }
}
