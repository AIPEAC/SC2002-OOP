package Backend.Entity.Users;

/**
 * Represents a company representative user in the Internship Placement Management System.
 * Company representatives must register and be approved by Career Center Staff before accessing the system.
 * They can create up to 5 internship opportunities for their company and manage applications.
 * Their user ID is their company email address.
 * 
 * @author Allen
 * @version 1.0
 */
public class CompanyRepresentative extends User {
    /** The position/title of the representative in the company */
    @SuppressWarnings("unused")
    private String position;
    
    /** Account status: "pending", "accepted", or "rejected" */
    private String accountStatus = "pending";
    
    /** The name of the company this representative works for */
    private String companyName;
    
    /** The department within the company */
    private String department = null;

    /**
     * Constructs a CompanyRepresentative with the specified details.
     * 
     * @param userID the company email address used as unique identifier
     * @param name the full name of the representative
     * @param email the company email address
     * @param position the job position/title in the company
     * @param accountStatus the approval status ("pending", "accepted", or "rejected")
     * @param companyName the name of the company
     * @param department the department within the company (can be null)
     */
    public CompanyRepresentative(String userID, String name, String email, String position, String accountStatus, String companyName, String department) {
        super(userID, name, email);
        this.position = position;
        this.accountStatus = accountStatus;
        this.companyName = companyName;
        this.department = department;
    }

    /**
     * Approves the company representative's account, allowing them to log in.
     */
    public void setStatusToAuthorized() {
        accountStatus = "accepted";
    }

    /**
     * Rejects the company representative's account registration.
     */
    public void setStatusToRejected() {
        accountStatus = "rejected";
    }

    /**
     * Gets the current account status.
     * 
     * @return the account status ("pending", "accepted", or "rejected")
     */
    public String getStatus() {
        return accountStatus;
    }

    /**
     * Gets the name of the company this representative works for.
     * 
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Gets the department within the company.
     * 
     * @return the department name, or null if not specified
     */
    public String getDepartment() {
        return department;
    }
    /* 
    public String getPosition() {
        return position;
    }
    */
}
