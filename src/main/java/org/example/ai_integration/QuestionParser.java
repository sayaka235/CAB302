package org.example.ai_integration;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.*;

public class QuestionParser {

    public static List<Question> parse(String json) throws Exception {
        JsonReader fullReader = new JsonReader(new StringReader(json));
        fullReader.setLenient(true);

        JsonElement rootElement = JsonParser.parseReader(fullReader);
        JsonObject root = rootElement.getAsJsonObject();

        String textBlock = root.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        textBlock = textBlock.replace("```json", "")
                .replace("```", "")
                .trim();

        JsonReader reader = new JsonReader(new StringReader(textBlock));
        reader.setLenient(true);
        JsonElement parsed = JsonParser.parseReader(reader);

        List<Question> questions = new ArrayList<>();

        for (JsonElement elem : parsed.getAsJsonArray()) {
            JsonObject qObj = elem.getAsJsonObject();
            String question = qObj.get("question").getAsString();
            String answer = qObj.get("answer").getAsString();

            JsonObject optionsObj = qObj.getAsJsonObject("options");
            Map<String, String> options = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : optionsObj.entrySet()) {
                options.put(entry.getKey(), entry.getValue().getAsString());
            }

            questions.add(new Question(question, options, answer));
        }

        return questions;
    }
}
