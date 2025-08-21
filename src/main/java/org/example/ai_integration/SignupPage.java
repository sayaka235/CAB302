package org.example.ai_integration;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HexFormat;

public class SignupPage extends Application {
    private TextField emailField, firstNameField, lastNameField;
    private DatePicker dobPicker;
    private PasswordField passField;
    private Button submitButton;

    @Override
    public void start(Stage stage) throws Exception {
        // 1) Ensure all tables exist ONCE at startup
        Schema.initAll();

        // 2) Build minimal UI
        emailField     = new TextField();
        firstNameField = new TextField();
        lastNameField  = new TextField();
        dobPicker      = new DatePicker();
        passField      = new PasswordField();
        submitButton   = new Button("Sign up");

        VBox root = new VBox(10,
                new Label("Email"), emailField,
                new Label("First name"), firstNameField,
                new Label("Last name"), lastNameField,
                new Label("DOB (YYYY-MM-DD)"), dobPicker,
                new Label("Password"), passField,
                submitButton
        );

        // 3) Insert on background thread, confirm by selecting the row back
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String first = firstNameField.getText();
            String last  = lastNameField.getText();
            LocalDate dob = dobPicker.getValue();
            String hash  = sha256Hex(passField.getText());

            new Thread(() -> {
                try {
                    long id = insertUser(email, first, last, dob, hash);
                    String row = fetchUserRow(id);
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.INFORMATION, "User inserted!\n" + row).showAndWait()
                    );
                } catch (SQLException ex) {
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.ERROR, "Insert failed: " + ex.getMessage()).showAndWait()
                    );
                }
            }, "InsertUser").start();
        });

        stage.setScene(new Scene(root, 480, 420));
        stage.setTitle("Sign up");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }

    // ---- DB ops kept small here (you can move to a UsersDao later) ----
    private long insertUser(String email, String first, String last, LocalDate dob, String passwordHash) throws SQLException {
        if (dob == null) throw new SQLException("DOB is required");
        String sql = "INSERT INTO Users (email, firstName, lastName, dob, passwordHash) VALUES (?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, first);
            ps.setString(3, last);
            ps.setString(4, dob.toString());
            ps.setString(5, passwordHash);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                rs.next(); return rs.getLong(1);
            }
        }
    }

    private String fetchUserRow(long id) throws SQLException {
        String sql = "SELECT ID,email,firstName,lastName,dob,createdAt FROM Users WHERE ID=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "(not found)";
                return String.format("Error signing up, please try again");
            }
        }
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }
}

