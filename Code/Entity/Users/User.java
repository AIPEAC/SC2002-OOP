package Entity.Users;

/**
 * Abstract base class representing a user in the Internship Placement Management System.
 * All user types (Student, Company Representative, Career Staff) inherit from this class.
 * 
 * @author Allen
 * @version 1.0
 */
public abstract class User {

    /** The unique identifier for the user */
    private String userID;
    
    /** The full name of the user */
    private String name;
    
    /** The email address of the user */
    private String email;

    /**
     * Constructs a User with the specified details.
     * 
     * @param userID the unique identifier for this user
     * @param name the full name of the user
     * @param email the email address of the user
     */
    public User(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
    }

    /**
     * Gets the unique user ID.
     * 
     * @return the user ID
     */
    public String getUserID() { 
        return userID; 
    }
    
    /**
     * Gets the user's full name.
     * 
     * @return the name of the user
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Gets the user's email address.
     * 
     * @return the email address
     */
    public String getEmail() { 
        return email; 
    }
}




