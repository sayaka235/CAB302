import org.example.ai_integration.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    private List<User> users;

    @BeforeEach
    public void setUp() {
        users = new ArrayList<>();
        users.add(new User("johndoe@example.com", "John", "Doe",
                LocalDate.of(1990, 1, 1), "password123"));
        users.add(new User("janedoe@example.com", "Jane", "Doe",
                LocalDate.of(1992, 2, 2), "qwerty"));
    }

    // ---------- Login Tests ----------
    private boolean login(String email, String password) {
        return users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email)
                        && u.getPassword().equals(password));
    }

    @Test
    public void testValidLogin() {
        assertTrue(login("johndoe@example.com", "password123"));
    }

    @Test
    public void testLoginWrongPassword() {
        assertFalse(login("johndoe@example.com", "wrongpass"));
    }

    @Test
    public void testLoginNonexistentUser() {
        assertFalse(login("ghost@example.com", "whatever"));
    }
}
