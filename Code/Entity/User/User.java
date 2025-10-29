package Entity.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class User {
    private String userID;
    private String passwordHash;
    private String name;
    private String email;

    public User(String userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.passwordHash = hashPassword("password"); // Hash the password
    }

    // Getters for public info
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Method to verify a password attempt
    public boolean verify(String password) {
        if (password == null || this.passwordHash == null) {
            return false;
        }
        String attemptHash = hashPassword(password);
        return this.passwordHash.equals(attemptHash);
    }

    public void setPasswordHash(String password) {
        this.passwordHash = hashPassword(password);
    }

    // Private helper to perform the hashing
    private static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }
}




