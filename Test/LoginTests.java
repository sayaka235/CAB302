import org.example.ai_integration.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {

    private List<User> users;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();
        users.add(new User("johndoe@example.com", "John", "Doe",
                LocalDate.of(1990, 1, 1), "P@sword1!"));
        users.add(new User("janedoe@example.com", "Jane", "Doe",
                LocalDate.of(1992, 2, 2), "P@sword2!"));
    }

    private boolean login(String email, String password) {
        return users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email)
                        && u.getPassword().equals(password));
    }

    @Test
    public void testValidLogin() {
        assertTrue(login("johndoe@example.com", "P@sword1!"));
    }

    @Test
    public void testLoginWrongPassword() {
        assertFalse(login("johndoe@example.com", "wrongpass"));
    }

    @Test
    public void testLoginNonexistentUser() {
        assertFalse(login("ghost@example.com", "whatever"));
    }

    @Test
    public void testLoginCaseInsensitiveEmail() {
        assertTrue(login("JANEDOE@example.com", "P@sword2!"));
    }

    @Test
    public void testNullEmailOrPassword() {
        assertFalse(login(null, "P@sword1!"));
        assertFalse(login("johndoe@example.com", null));
    }
}
