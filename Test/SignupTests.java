import org.example.ai_integration.model.Signup;
import org.example.ai_integration.model.SignupClass;
import org.example.ai_integration.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

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
        Signup signup = new Signup();
        User user = new User("email@gmail", "layla245", "ahern", LocalDate.of(1977,9,17), "password");
        assertThrows(SQLException.class,() -> {
                signup.insertUser(user);
        });
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

    //strict requirements for more tests ahhhh typa shit
    @ParameterizedTest
    @ValueSource(strings = {
            "secret1",
            "P@ss12",
            "Abcdef",
            "123456"
    })
    void password_acceptsAtLeast6Chars(String pw) {
        assertTrue(isValidPassword(pw));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "12345", "     "})
    void password_rejectsTooShort(String pw) {
        assertFalse(isValidPassword(pw));
    }

    private static boolean isPasswordStrong(String pw) {
        if (pw == null) return false;
        return pw.matches("^(?=\\S+$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcdef1!", "Qut2025!", "Good_Pw9", "NoSpaces9!"})
    void password_acceptsStrong(String pw) {
        assertTrue(isPasswordStrong(pw));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password",
            "PASSWORD",
            "passw0rd",
            "Abcdefgh",
            "abcD1",
            "Bad pass1!",
    })
    void password_rejectsWeak(String pw) {
        assertFalse(isPasswordStrong(pw));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "first.last@qut.edu.au",
            "a+b-c_d@sub.domain.co",
            "u@localhost",
            "USER@EXAMPLE.COM"
    })
    void email_validFormats(String email) {
        assertTrue(isValidEmail(email));
    }

    private static final Pattern STRICT_EMAIL = Pattern.compile(
            "^(?=.{1,254}$)(?=.{1,64}@)" +
                    "(?!\\.)(?!.*\\.\\.)[A-Za-z0-9+_.-]+(?<!\\.)@" +
                    "(?:(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+" +
                    "[A-Za-z]{2,63}$"
    );

    private static boolean isEmailStrict(String email) {
        return email != null && STRICT_EMAIL.matcher(email).matches();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ".user@example.com",
            "user.@example.com",
            "us..er@example.com",
            "user@domain",
            "user@domain.c",
            "user@domain..com",
            "user@-domain.com",
            "user@domain-.com"
    })
    void email_rejectsStrictInvalid(String email) {
        assertFalse(isEmailStrict(email));
    }
}
