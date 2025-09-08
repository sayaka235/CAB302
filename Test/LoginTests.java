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
        users.add(new User(1, "johndoe@example.com", "John", "Doe",
                LocalDate.of(1990, 1, 1), "password123"));
        users.add(new User(2, "janedoe@example.com", "Jane", "Doe",
                LocalDate.of(1992, 2, 2), "qwerty"));
        users.add(new User(3, "aliceg@gmail.com", "Alice", "Graystone",
                LocalDate.of(1995, 3, 3), "letmein"));
    }

    private boolean login(String email, String password) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && user.getPassword().equals(password));
    }

    @Test
    public void testSuccessfulLogin() {
        assertTrue(login("johndoe@example.com", "password123"));
    }

    @Test
    public void testLoginWrongPassword() {
        assertFalse(login("johndoe@example.com", "wrongpass"));
    }

    @Test
    public void testLoginNonexistentUser() {
        assertFalse(login("notfound@example.com", "whatever"));
    }

    @Test
    public void testLoginCaseInsensitiveEmail() {
        assertTrue(login("JANEdoe@example.com", "qwerty"));
    }

    @Test
    public void testEmptyEmailOrPassword() {
        assertFalse(login("", "password123"));
        assertFalse(login("johndoe@example.com", ""));
    }

    @Test
    public void testNullEmailOrPassword() {
        assertFalse(login(null, "password123"));
        assertFalse(login("johndoe@example.com", null));
    }
}
