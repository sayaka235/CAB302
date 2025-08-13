package org.example.ai_integration;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;

public class QuizAPI {
    private static final String API_KEY = "AIzaSyAMCztiPTzwmxVrlY7Q3uwwYBVtBaw2aCM";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static String generateQuiz(String content, String quizType) {
        String prompt = buildPrompt(content, quizType);

        JsonObject contents = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);

        JsonObject contentWrapper = new JsonObject();
        contentWrapper.add("parts", parts);

        JsonArray contentsArray = new JsonArray();
        contentsArray.add(contentWrapper);

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
            return parseResponse(responseBody, quizType);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private static String buildPrompt(String content, String quizType) {
        switch (quizType) {
            case "Multiple Choice":
                return "Create a multiple choice quiz with 5 questions based on the following content. " +
                        "Each question should have 4 options labeled A, B, C, and D, and indicate the correct answer. " +
                        "Format strictly as JSON with 'question', 'options', and 'answer' fields.\n\n" + content;
            case "True/False":
                return "Create a true/false quiz with 5 questions based on the following content. " +
                        "Each question should have a statement and an answer of either 'True' or 'False'. " +
                        "Format strictly as JSON with 'question' and 'answer' fields.\n\n" + content;
            case "Fill in the Blank":
                return "Create a fill-in-the-blank quiz with 5 questions based on the following content. " +
                        "Use a blank (____) in each sentence and provide the correct word as the answer. " +
                        "Format strictly as JSON with 'question' and 'answer' fields.\n\n" + content;
            default:
                return "Please select a valid quiz type.";
        }
    }

    private static String parseResponse(String json, String quizType) {
        JsonObject root = gson.fromJson(json, JsonObject.class);
        try {
            String textBlock = root.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();

            textBlock = textBlock.replace("```json", "").replace("```", "").trim();

            JsonElement parsedElement = gson.fromJson(textBlock, JsonElement.class);
            StringBuilder formatted = new StringBuilder();

            if (parsedElement.isJsonArray()) {
                // ✅ Gemini returned a raw array
                JsonArray questionsArray = parsedElement.getAsJsonArray();
                int i = 1;
                for (JsonElement qElem : questionsArray) {
                    JsonObject qObj = qElem.getAsJsonObject();
                    formatted.append(i++).append(". ")
                            .append(qObj.get("question").getAsString()).append("\n");
                    formatted.append("Answer: ")
                            .append(qObj.get("answer").getAsString()).append("\n\n");
                }
            } else if (parsedElement.isJsonObject()) {
                JsonObject parsed = parsedElement.getAsJsonObject();
                if (parsed.has("questions")) {
                    int i = 1;
                    for (JsonElement qElem : parsed.getAsJsonArray("questions")) {
                        JsonObject qObj = qElem.getAsJsonObject();
                        formatted.append(i++).append(". ")
                                .append(qObj.get("question").getAsString()).append("\n");

                        if (quizType.equals("Multiple Choice") && qObj.has("options")) {
                            char optionLabel = 'A';
                            for (JsonElement opt : qObj.getAsJsonArray("options")) {
                                formatted.append("   ").append(optionLabel++).append(") ")
                                        .append(opt.getAsString()).append("\n");
                            }
                        }

                        formatted.append("Answer: ")
                                .append(qObj.get("answer").getAsString()).append("\n\n");
                    }
                } else {
                    formatted.append("Unexpected object structure:\n").append(textBlock);
                }
            } else {
                formatted.append("Unrecognized response format:\n").append(textBlock);
            }

            return formatted.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to parse response:\n" + json;
        }
    }
}
