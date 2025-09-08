import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import org.example.ai_integration.model.User;
public class SignupTest {
    private static final String firstname = "John";
    private static final String firstnameTwo = "Jane";
    private static final String lastname = "Doe";
    private static final String lastnameTwo = "Doe";
    private static final String email = "john@gmail.com";
    private static final String emailTwo = "jane@gmail.com";
    private static final LocalDate dob = "";
    private static final LocalDate dobTwo = "";
    private static final String password = "password";
    private static final String passwordTwo = "password2";

    private User signUp;
    private User signUpTwo;

    @BeforeEach
    public void setUp() {
        signUp = new User(firstname, lastname, email, dob, password);
        signUpTwo = new User(firstnameTwo, lastnameTwo, emailTwo, dobTwo, passwordTwo);
    }
    @Test
    public void testSetId() {
        signUp.setId(1);
        assertEquals(1, signUp.getId());
    }
}
