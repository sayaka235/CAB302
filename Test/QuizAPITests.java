import org.example.ai_integration.model.QuizAPI;
import org.example.ai_integration.model.QuizAPI.McqItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizAPITests {

    @Test
    public void testParseValidMcqJson() {
        String sampleJson = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "[{ \\"question\\": \\"What is 2+2?\\", \\"options\\": [\\"1\\", \\"2\\", \\"3\\", \\"4\\"], \\"correct_index\\": 4 }]"
                  }
                ]
              }
            }
          ]
        }
        """;

        List<McqItem> items = QuizAPI.parseMcqArray(sampleJson);
        assertEquals(1, items.size());
        McqItem q = items.get(0);
        assertEquals("What is 2+2?", q.question);
        assertEquals(4, q.options.size());
        assertEquals("4", q.options.get(3));
        assertEquals(4, q.correct_index);
    }

    @Test
    public void testParseThrowsOnInvalidOptions() {
        String badJson = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "[{ \\"question\\": \\"Broken?\\", \\"options\\": [\\"only one option\\"], \\"correct_index\\": 1 }]"
                  }
                ]
              }
            }
          ]
        }
        """;

        assertThrows(IllegalStateException.class, () -> QuizAPI.parseMcqArray(badJson));
    }

    @Test
    public void testParseThrowsOnMissingAnswer() {
        String badJson = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "[{ \\"question\\": \\"What is 2+2?\\", \\"options\\": [\\"1\\", \\"2\\", \\"3\\", \\"4\\"] }]"
                  }
                ]
              }
            }
          ]
        }
        """;

        assertThrows(IllegalStateException.class, () -> QuizAPI.parseMcqArray(badJson));
    }

    //bullshit tests
    @Test
    public void parseDirectJsonArray() {
        String json = """
        [
          { "question": "A?", "options": ["1","2","3","4"], "correct_index": 1 },
          { "question": "B?", "options": ["w","x","y","z"], "correct_index": 3 }
        ]
        """;
        List<McqItem> items = QuizAPI.parseMcqArray(json);
        assertEquals(2, items.size());
        assertEquals("A?", items.get(0).question);
        assertEquals("y", items.get(1).options.get(2));
    }

    @Test
    public void parseQuotedJsonArray() {
        String json = """
        "[{ \\"question\\": \\"Q\\", \\"options\\": [\\"a\\", \\"b\\", \\"c\\", \\"d\\"], \\"correct_index\\": 2 }]"
        """;
        List<McqItem> items = QuizAPI.parseMcqArray(json);
        assertEquals(1, items.size());
        assertEquals("Q", items.get(0).question);
    }

    @Test
    public void parseEmptyJsonArray() {
        String payload = "[]";
        List<McqItem> items = QuizAPI.parseMcqArray(payload);
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    public void parseNoCandidates() {
        String payload = """
        { "candidates": [] }
        """;
        assertThrows(RuntimeException.class, () -> QuizAPI.parseMcqArray(payload));
    }

    @Test
    public void parseMissingParts() {
        String payload = """
        { "candidates": [ { "content": { } } ] }
        """;
        assertThrows(RuntimeException.class, () -> QuizAPI.parseMcqArray(payload));
    }

    @Test
    public void parseMissingTextField() {
        String payload = """
        {
          "candidates": [
            { "content": { "parts": [ { "not_text": "oops" } ] } }
          ]
        }
        """;
        assertThrows(RuntimeException.class, () -> QuizAPI.parseMcqArray(payload));
    }

    @Test
    public void parseInvalidJson() {
        String payload = "This is not JSON";
        RuntimeException ex = assertThrows(RuntimeException.class, () -> QuizAPI.parseMcqArray(payload));
        assertTrue(ex.getMessage().contains("Could not parse MCQ array"));
    }

    @Test
    public void parseNullInput() {
        assertThrows(RuntimeException.class, () -> QuizAPI.parseMcqArray(null));
    }

    @Test
    public void parseLargeInvalidInput() {
        String big = "X".repeat(1000);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> QuizAPI.parseMcqArray(big));
        assertTrue(ex.getMessage().length() > 0);
    }

    @Test
    public void parseOutOfRangeCorrectIndex() {
        String payload = """
        [
          { "question": "Bad index", "options": ["a","b","c","d"], "correct_index": 5 }
        ]
        """;
        List<McqItem> items = QuizAPI.parseMcqArray(payload);
        assertEquals(1, items.size());
        assertEquals(5, items.get(0).correct_index);
    }
}
