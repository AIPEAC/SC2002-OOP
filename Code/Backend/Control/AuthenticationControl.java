package Backend.Control;
import Backend.Entity.Users.*;

/**
 * Controls user authentication and session management in the system.
 * Maintains the currently logged-in user and provides methods to check
 * user identity and permissions. Uses polymorphism to handle different user types.
 * 
 * @author Allen
 * @version 1.0
 */
public class AuthenticationControl {
    /** The currently logged-in user, or null if no user is logged in */
    private User currentUser=null;
    
    /** Company name for company representatives (cached for efficiency) */
    private String companyName=null;

    /**
     * Constructs an AuthenticationControl with no user logged in.
     */
    public AuthenticationControl(){
        this.currentUser=null;
    }
    
    

    /**
     * Sets the currently logged-in user.
     * 
     * @param user the user to log in
     */
    protected void setLoggedin(User user){
        currentUser=user;
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn(){
        if (currentUser==null){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Gets the currently logged-in user object.
     * 
     * @return the current user, or null if not logged in
     */
    protected User getUser(){
        return currentUser;
    }
    
    /**
     * Gets the identity/role of the currently logged-in user.
     * Uses instanceof to determine the user type (polymorphism).
     * 
     * @return "CareerStaff", "Student", or "CompanyRepresentative"
     * @throws IllegalStateException if no user is logged in
     * @throws IllegalArgumentException if user type is unknown
     */
    public String getUserIdentity(){
        
        if (currentUser==null){
            throw new IllegalStateException("Not logged in: cannot get user identity.");
        } else if (currentUser instanceof CareerStaff){
            return "CareerStaff";
        } else if (currentUser instanceof Student){
            return "Student";
        } else if (currentUser instanceof CompanyRepresentative){
            return "CompanyRepresentative";
        } else {
            throw new IllegalArgumentException("Unknown user type for current user.");
        }
        
        
    }

    /**
     * Gets the user ID of the currently logged-in user.
     * 
     * @return the user ID
     */
    public String getUserID(){
        return currentUser.getUserID();
    }
    
    /**
     * Sets the company name for a company representative (caching for efficiency).
     * 
     * @param companyName the name of the company
     */
    public void setCompanyName(String companyName){
        this.companyName=companyName;
    }
    
    /**
     * Gets the cached company name for the current company representative.
     * 
     * @return the company name, or null if not set
     */
    public String getCompanyName(){
        return this.companyName;
    }
}
