import org.example.ai_integration.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SignupTests {

    @Test
    public void testValidSignup() {
        User user = new User("jane.doe@example.com", "Jane", "Doe",
                LocalDate.of(1995, 5, 10), "secure123");
        assertTrue(User.signupValid(user));
    }

    @Test
    public void testInvalidNameWithNumbers() {
        User user = new User("bad@example.com", "Jane123", "Doe",
                LocalDate.of(1995, 5, 10), "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testInvalidNameWithSymbols() {
        User user = new User("bad@example.com", "Jane@", "Doe",
                LocalDate.of(1995, 5, 10), "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testInvalidEmailFormat() {
        User user = new User("not-an-email", "Jane", "Doe",
                LocalDate.of(1995, 5, 10), "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testFutureDobInvalid() {
        User user = new User("jane.doe@example.com", "Jane", "Doe",
                LocalDate.now().plusDays(1), "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testShortPasswordInvalid() {
        User user = new User("jane.doe@example.com", "Jane", "Doe",
                LocalDate.of(1995, 5, 10), "123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testNullNameFails() {
        User user = new User("jane.doe@example.com", null, "Doe",
                LocalDate.of(1995, 5, 10), "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testNullEmailFails() {
        User user = new User(null, "Jane", "Doe",
                LocalDate.of(1995, 5, 10), "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testNullDobFails() {
        User user = new User("jane.doe@example.com", "Jane", "Doe",
                null, "secure123");
        assertFalse(User.signupValid(user));
    }

    @Test
    public void testNullPasswordFails() {
        User user = new User("jane.doe@example.com", "Jane", "Doe",
                LocalDate.of(1995, 5, 10), null);
        assertFalse(User.signupValid(user));
    }
}
