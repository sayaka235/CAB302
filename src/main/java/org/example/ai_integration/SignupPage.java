package org.example.ai_integration;
//sql stuff
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import javafx.util.StringConverter;
import java.time.LocalDate;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class SignupPage extends Application {
    @Override
    public void start(Stage stage) {
        // Define the root node and set the alignment and spacing
        // properties. Also, set the fillWidth property to false
        // so the children are not resized to fill the width of the
        // VBox.
        VBox root = new VBox();
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setSpacing(15.0);
        root.setFillWidth(false);
        // Create a TextField, a Label, and an HBox with appropriate

        Label logHead = new Label("Sign Up");
        //Email
        TextField emailtextField = new TextField();
        Label labelEmail = new Label("Enter Email");
        emailtextField.setText("name@example.com");

        // First and Last name
        Label labelFName = new Label("Enter First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First name");

        Label labelLName = new Label("Enter Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last name");

        // Date of Birth
        Label labelDob = new Label("Date of Birth:");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("dd/MM/yyyy");

        // Disable future dates
        dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        // Optional: force dd/MM/yyyy display & parsing
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dobPicker.setConverter(new StringConverter<LocalDate>() {
            @Override public String toString(LocalDate date) {
                return (date == null) ? "" : fmt.format(date);
            }
            @Override public LocalDate fromString(String s) {
                if (s == null || s.trim().isEmpty()) return null;
                try { return LocalDate.parse(s, fmt); }
                catch (DateTimeParseException e) { return null; }
            }
        });

        // Password
        PasswordField passtextField = new PasswordField();
        passtextField.setPromptText("password:");
        Label labelnew = new Label("Enter Password");


        // The buttons
        VBox vbox = new VBox();
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setSpacing(15.0);
        Button button1 = new Button("Have an account? Log in!");
        Button button2 = new Button("Sign up");

        // Add the buttons to the HBox
        vbox.getChildren().addAll(button1, button2);

        button2.setOnAction(e -> {
            String email = emailtextField.getText().trim();
            String first = firstNameField.getText().trim();
            String last  = lastNameField.getText().trim();
            LocalDate dob = dobPicker.getValue();
            String pwd   = passtextField.getText();

            if (email.isEmpty() || first.isEmpty() || last.isEmpty() || pwd.isEmpty()) {
                System.out.println("Please fill in all required fields.");
                return;
            }

            String passwordHash = sha256Hex(pwd);

            new Thread(() -> {
                try {
                    insertUser(email, first, last, dob, passwordHash);
                    System.out.println("User inserted!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("Insert failed: " + ex.getMessage());
                }
            }).start();
        });


        // Add the children to the root vbox
        root.getChildren().addAll(logHead,
                labelEmail,emailtextField,
                labelFName, firstNameField,
                labelLName, lastNameField,
                labelDob, dobPicker,
                labelnew, passtextField,
                vbox);
        // Define the scene, add to the stage (window) and show the stage
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setTitle("JavaFX Example Scene");
        stage.show();
    }

    public static void main(String[] args) {
        launch();


    }


    private Connection getConnection() throws SQLException {
        // Replace with your DB details
        String url = "jdbc:mysql://127.0.0.1:3306/notesdata?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "Minecr@ft1";
        return DriverManager.getConnection(url, user, pass);
    }
    // Insert the user data into the database from the java form
    private void insertUser(String email, String first, String last, LocalDate dob, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (email, firstName, lastName, dob, passwordHash) VALUES (?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, first);
            ps.setString(3, last);
            if (dob != null) {
                ps.setDate(4, Date.valueOf(dob));  // convert date to right format
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            ps.setString(5, passwordHash);
            ps.executeUpdate();
        }
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
