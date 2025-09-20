package org.example.ai_integration.tests;

import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class validations {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z-]+$");
    private static final Pattern EMAIL_BASIC = Pattern.compile("^[^@]+@[^@]+$");
    private static final Pattern LOCAL_PART = Pattern.compile("^(?!\\.)(?!.*\\.\\.)[A-Za-z0-9+_.-]+(?<!\\.)$");
    private static final Pattern DOMAIN_LABEL = Pattern.compile("^(?!-)[A-Za-z0-9-]{1,63}(?<!-)$");
    private static final Pattern TLD = Pattern.compile("^[A-Za-z]{2,63}$");

    private final userRepo repo;

    public validations(userRepo repo) {
        this.repo = repo;
    }

    public void signup(User newUser) throws SQLException {
        String email = safe(newUser.getEmail()).toLowerCase();
        String first = safe(newUser.getFirst());
        String last  = safe(newUser.getLast());
        LocalDate dob = newUser.getDob();
        String passwordHash = safe(newUser.getPassword());

        if (email.isEmpty() || first.isEmpty() || last.isEmpty() || dob == null || passwordHash.isEmpty()) {
            throw new SQLException("Missing fields", "All fields must be completed.");
        }

        if (!NAME_PATTERN.matcher(first).matches()) {
            throw new SQLException("Firstname invalid", "First name can only contain letters and hyphens.");
        }
        if (!NAME_PATTERN.matcher(last).matches()) {
            throw new SQLException("Lastname invalid", "Last name can only contain letters and hyphens.");
        }

        if (!EMAIL_BASIC.matcher(email).matches()) {
            throw new SQLException("Email invalid", "Email must contain a local part,'@', and a domain.");
        }
        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];

        if (!LOCAL_PART.matcher(local).matches()) {
            throw new SQLException("Email invalid",
                    "Local part cannot start/end with a dot and cannot contain consecutive dots.");
        }

        String[] labels = domain.split("\\.");
        if (labels.length < 2) {
            throw new SQLException("Email invalid", "Domain must contain at least one dot (e.g. example.com).");
        }

        for (int i = 0; i < labels.length - 1; i++) {
            if (!DOMAIN_LABEL.matcher(labels[i]).matches()) {
                throw new SQLException("Email invalid",
                        "Domain labels may have letters/digits/hyphens and cannot start/end with a hyphen.");
            }
        }

        String tld = labels[labels.length - 1];
        if (!TLD.matcher(tld).matches()) {
            throw new SQLException("Email invalid", "Please enter a valid domain (e.g. example.com)");
        }

        if (!dob.isBefore(LocalDate.now())) {
            throw new SQLException("Date of birth invalid", "DOB must be in the past.");
        }

        try (Connection c = Database.getConnection()) {
            repo.insert(c, new User(email, first, last, dob, passwordHash));
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
