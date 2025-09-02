package org.example.ai_integration;

import com.google.gson.stream.JsonReader;
import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.io.StringReader;

public class QuizAPI
{
    private static final String API_KEY = "AIzaSyAMCztiPTzwmxVrlY7Q3uwwYBVtBaw2aCM";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static class McqItem
    {
        public String question;
        public java.util.List<String> options;
        public int correct_index;
    }

    public static java.util.List<McqItem> parseMcqArray(String rawJson)
    {
        JsonObject root = gson.fromJson(rawJson, JsonObject.class);

        String text = root.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        text = text.replace("```json", "").replace("```", "").trim();

        JsonElement payload = JsonParser.parseString(text);
        JsonArray items;
        if (payload.isJsonArray())
        {
            items = payload.getAsJsonArray();
        }
        else if (payload.isJsonObject() && payload.getAsJsonObject().has("questions"))
        {
            items = payload.getAsJsonObject().getAsJsonArray("questions");
        }
        else
        {
            throw new IllegalStateException("Unexpected quiz JSON: " + text);
        }

        java.util.List<McqItem> out = new java.util.ArrayList<>();
        for (JsonElement e : items)
        {
            JsonObject o = e.getAsJsonObject();
            McqItem m = new McqItem();
            m.question = o.get("question").getAsString();

            if (o.has("options") && o.get("options").isJsonArray())
            {
                JsonArray arr = o.getAsJsonArray("options");
                if (arr.size() != 4) throw new IllegalStateException("options must have 4 items");
                m.options = new java.util.ArrayList<>(4);
                for (int i = 0; i < 4; i++) m.options.add(arr.get(i).getAsString());
            }
            else if (o.has("options") && o.get("options").isJsonObject()) {
                JsonObject opts = o.getAsJsonObject("options");
                String[] order = {"A","B","C","D"};
                m.options = new java.util.ArrayList<>(4);
                for (String k : order)
                {
                    if (!opts.has(k)) throw new IllegalStateException("options missing key: " + k);
                    m.options.add(opts.get(k).getAsString());
                }
            }
            else
            {
                throw new IllegalStateException("Missing options");
            }

            if (o.has("correct_index"))
            {
                m.correct_index = o.get("correct_index").getAsInt();
            }
            else if (o.has("answer"))
            {
                switch (o.get("answer").getAsString().trim().toUpperCase())
                {
                    case "A": m.correct_index = 1; break;
                    case "B": m.correct_index = 2; break;
                    case "C": m.correct_index = 3; break;
                    case "D": m.correct_index = 4; break;
                    default: throw new IllegalStateException("Unknown answer letter");
                }
            }
            else
            {
                throw new IllegalStateException("Missing correct_index/answer");
            }

            if (m.correct_index < 1 || m.correct_index > 4)
                throw new IllegalStateException("correct_index out of range");

            out.add(m);
        }
        return out;
    }

    public static String generateQuiz(String content, String quizType)
    {
        String prompt = buildPrompt(content, quizType);

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
        JsonObject gen = new JsonObject();
        gen.addProperty("responseMimeType", "application/json");
        req.add("generationConfig", gen);

        RequestBody body = RequestBody.create(gson.toJson(req), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(ENDPOINT)
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

    private static String buildPrompt(String content, String quizType)
    {
        switch (quizType)
        {
            case "Multiple Choice":
                return """
            Create a 5-question MULTIPLE CHOICE quiz from the content below.

            Output STRICTLY as raw JSON (no prose, no markdown fences):

            [
              {
                "question": "string",
                "options": ["opt1","opt2","opt3","opt4"],   // exactly 4
                "correct_index": 1                           // 1..4 (1-based)
              }
            ]
            """ + "\n\n" + content;
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

    private static String parseResponse(String json, String quizType)
    {
        JsonObject root = gson.fromJson(json, JsonObject.class);
        try
        {
            String textBlock = root.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();

            textBlock = textBlock.replace("```json", "").replace("```", "").trim();
            JsonReader reader = new JsonReader(new StringReader(textBlock));
            reader.setLenient(true);
            JsonElement parsedElement = gson.fromJson(textBlock, JsonElement.class);
            StringBuilder formatted = new StringBuilder();

            if (parsedElement.isJsonArray())
            {
                JsonArray questionsArray = parsedElement.getAsJsonArray();
                int i = 1;
                for (JsonElement qElem : questionsArray)
                {
                    JsonObject qObj = qElem.getAsJsonObject();
                    formatted.append(i++).append(". ")
                            .append(qObj.get("question").getAsString()).append("\n");
                    formatted.append("Answer: ")
                            .append(qObj.get("answer").getAsString()).append("\n\n");
                }
            }
            else if (parsedElement.isJsonObject())
            {
                JsonObject parsed = parsedElement.getAsJsonObject();
                if (parsed.has("questions"))
                {
                    int i = 1;
                    for (JsonElement qElem : parsed.getAsJsonArray("questions"))
                    {
                        JsonObject qObj = qElem.getAsJsonObject();
                        formatted.append(i++).append(". ")
                                .append(qObj.get("question").getAsString()).append("\n");

                        if (quizType.equals("Multiple Choice") && qObj.has("options"))
                        {
                            char optionLabel = 'A';
                            for (JsonElement opt : qObj.getAsJsonArray("options"))
                            {
                                formatted.append("   ").append(optionLabel++).append(") ")
                                        .append(opt.getAsString()).append("\n");
                            }
                        }

                        formatted.append("Answer: ")
                                .append(qObj.get("answer").getAsString()).append("\n\n");
                    }
                }
                else
                {
                    formatted.append("Unexpected object structure:\n").append(textBlock);
                }
            }
            else
            {
                formatted.append("Unrecognized response format:\n").append(textBlock);
            }

            return formatted.toString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Failed to parse response:\n" + json;
        }
    }
}
