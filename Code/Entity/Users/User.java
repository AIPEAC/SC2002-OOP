package Entity.Users;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class User {
    private String userID;
    //private String passwordHash;
    private String name;
    private String email;

    public User(String userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        //this.passwordHash = hashPassword("password"); // Hash the password
    }

    // Getters for public info
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    //public String getPasswordHash() { return passwordHash; }



    

    // Private helper to perform the hashing
    
}




