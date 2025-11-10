package Backend.Entity.Users;

/**
 * Represents a career center staff member in the Internship Placement Management System.
 * Career staff are responsible for approving company representatives, internship opportunities,
 * and student withdrawal requests. They can also generate reports on internship opportunities.
 * Their user ID is their NTU account.
 * 
 * @author Allen
 * @version 1.0
 */
public class CareerStaff extends User {
    /** The department where the staff member works */
    private String department;
    
    /** The role or position of the staff member */
    @SuppressWarnings("unused")
    private String role;

    /**
     * Constructs a CareerStaff with the specified details.
     * 
     * @param userID the NTU account used as unique identifier
     * @param name the full name of the staff member
     * @param email the staff member's email address
     * @param department the department where the staff member works
     * @param role the role or position title
     */
    public CareerStaff(String userID, String name, String email, String department, String role) {
        super(userID, name, email);
        // Additional fields for CareerStaff
        this.department = department;
        this.role = role;
    }

    /**
     * Gets the department where this staff member works.
     * 
     * @return the department name
     */
    public String getDepartment() {
        return department;
    }
}

