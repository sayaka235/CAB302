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
}
