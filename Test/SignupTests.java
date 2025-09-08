import com.example.addressbook.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContactTest {

    private static final String FIRST_NAME = "John";
    private static final String FIRST_NAME_TWO = "Jane";
    private static final String LAST_NAME = "Doe";
    private static final String LAST_NAME_TWO = "Doe";
    private static final String EMAIL = "john@gmail.com";
    private static final String EMAIL_TWO = "jane@gmail.com";
    private static final String dob = "123456";
    private static final String dobTwo = "654321";


    private Contact contact;
    private Contact contactTwo;

    @BeforeEach
    public void setUp() {
        contact = new Contact(FIRST_NAME, LAST_NAME, EMAIL, PHONE);
        contactTwo = new Contact(FIRST_NAME_TWO, LAST_NAME_TWO, EMAIL_TWO, PHONE_TWO);
    }

    @Test
    public void testSetId() {
        contact.setId(1);
        assertEquals(1, contact.getId());
    }

    @Test
    public void testGetFirstName() {
        assertEquals(FIRST_NAME, contact.getFirstName());
    }
    /* @Test
     public void testEmailLength(){
         assertEquals(20).testEmailLength();
     }*/
    @Test
    public void testSetFirstName() {
        contact.setFirstName(FIRST_NAME_TWO);
        assertEquals(FIRST_NAME_TWO, contact.getFirstName());
    }
    @Test
    public void testGetLastName() {
        assertEquals(LAST_NAME, contact.getLastName());
    }
    @Test
    public void testSetLastName() {
        contact.setLastName(LAST_NAME_TWO);
        assertEquals(LAST_NAME_TWO, contact.getLastName());
    }
    @Test
    public void testGetEmail() {
        assertEquals(EMAIL, contact.getEmail());
    }
    @Test
    public void testSetEmail() {
        contact.setEmail(EMAIL_TWO);
        assertEquals(EMAIL_TWO, contact.getEmail());
    }
    @Test
    public void testGetPhone() {
        assertEquals(PHONE, contact.getPhone());
    }
    @Test
    public void testSetPhone() {
        contact.setPhone(PHONE_TWO);
        assertEquals( PHONE_TWO, contact.getPhone());
    }
    @Test
    public void testGetFullName() {
        String[] firstContact = {FIRST_NAME, LAST_NAME};
        String[] secondContact = {FIRST_NAME_TWO, LAST_NAME_TWO};
        assertEquals(String.join(" ", firstContact),  contact.getFullName());
        assertEquals(String.join(" ", secondContact),  contactTwo.getFullName());
    }


}
