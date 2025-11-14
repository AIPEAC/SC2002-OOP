package Entity;

/**
 * Represents a user's login credentials.
 * This entity encapsulates authentication data separate from user profile information,
 * following the principle of separation of concerns. Login credentials are stored
 * separately from user entities (Student, CareerStaff, CompanyRepresentative) to
 * maintain security and performance.
 * 
 * @author Allen
 * @version 1.0
 */
public class LoginCredential {
    /** The type of user (e.g., "Student", "Staff", "CompanyRepresentative") */
    private String identity;
    
    /** The user's unique identifier (userID or email) */
    private String userID;
    
    /** The hashed password (SHA-256 with salt) */
    private String passwordHash;
    
    /** The salt used for password hashing */
    private String salt;
    
    /** The account status (e.g., "approved", "pending", "rejected", or empty string for other user types) */
    private String status;

    /**
     * Constructs a LoginCredential with all required fields.
     * 
     * @param identity the type of user (Student, Staff, CompanyRepresentative)
     * @param userID the user's unique identifier
     * @param passwordHash the hashed password
     * @param salt the salt used in hashing
     * @param status the account status
     */
    public LoginCredential(String identity, String userID, String passwordHash, String salt, String status) {
        this.identity = identity;
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.status = status;
    }

    /**
     * Gets the identity of the user.
     * 
     * @return the identity
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets the identity of the user.
     * 
     * @param identity the identity to set
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * Gets the user ID.
     * 
     * @return the user ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the user ID.
     * 
     * @param userID the user ID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Gets the password hash.
     * 
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash.
     * 
     * @param passwordHash the password hash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the salt.
     * 
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the salt.
     * 
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Converts the LoginCredential to a string array for CSV output.
     * Order: [identity, userID, passwordHash, salt, status]
     * 
     * @return a string array representation of this credential
     */
    public String[] toArray() {
        return new String[]{identity, userID, passwordHash, salt, status};
    }

    /**
     * Returns a string representation of this credential (for debugging).
     * 
     * @return a string representation
     */
    @Override
    public String toString() {
        return "LoginCredential{" +
                "identity='" + identity + '\'' +
                ", userID='" + userID + '\'' +
                ", passwordHash='" + (passwordHash != null ? "[HASHED]" : "null") + '\'' +
                ", salt='" + (salt != null ? "[SALT]" : "null") + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
