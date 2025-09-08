import org.example.ai_integration.model.SignupClass;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SignupTests {

    private boolean isValidName(String name) {
        return name != null && name.matches("^[A-Za-z-]+$");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidDob(Date dob) {
        return dob != null && dob.before(new Date());
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private boolean signupValid(SignupClass s, String password) {
        return isValidName(s.getFirstName())
                && isValidName(s.getLastName())
                && isValidEmail(s.getEmail())
                && isValidDob(s.getDob())
                && isValidPassword(password);
    }

    @Test
    public void testValidSignup() {
        SignupClass s = new SignupClass("Alice", "Graystone", "alice@example.com",
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertTrue(signupValid(s, "StrongPass1"));
    }

    @Test
    public void testInvalidSignupNameWithNumbers() {
        SignupClass s = new SignupClass("Alice123", "Graystone", "alice@example.com",
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertFalse(signupValid(s, "StrongPass1"));
    }

    @Test
    public void testInvalidSignupEmailFormat() {
        SignupClass s = new SignupClass("Alice", "Graystone", "bad-email",
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertFalse(signupValid(s, "StrongPass1"));
    }

    @Test
    public void testInvalidSignupFutureDob() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        SignupClass s = new SignupClass("Alice", "Graystone", "alice@example.com",
                "123 Lane", cal.getTime());
        assertFalse(signupValid(s, "StrongPass1"));
    }

    @Test
    public void testShortPasswordInvalid() {
        SignupClass s = new SignupClass("Alice", "Graystone", "alice@example.com",
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertFalse(signupValid(s, "123"));
    }

    @Test
    public void testNullFieldsInvalid() {
        SignupClass s1 = new SignupClass(null, "Graystone", "alice@example.com",
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertFalse(signupValid(s1, "StrongPass1"));

        SignupClass s2 = new SignupClass("Alice", null, "alice@example.com",
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertFalse(signupValid(s2, "StrongPass1"));

        SignupClass s3 = new SignupClass("Alice", "Graystone", null,
                "123 Lane", new Date(95, Calendar.JANUARY, 1));
        assertFalse(signupValid(s3, "StrongPass1"));

        SignupClass s4 = new SignupClass("Alice", "Graystone", "alice@example.com",
                "123 Lane", null);
        assertFalse(signupValid(s4, "StrongPass1"));

        assertFalse(signupValid(s4, null)); // null password
    }
}
