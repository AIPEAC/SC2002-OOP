package Backend.Control;
//when registered, the password is set to "password". Remind them to change.

import Backend.Entity.Users.User;

/**
 * Controls login, registration, and password management functionality.
 * Handles user authentication by coordinating with AuthenticationControl and UserLoginDirectoryControl.
 * Company representatives register with default password "password" and must change it after first login.
 * 
 * @author Allen
 * @version 1.0
 */
public class LoginControl {
    /** Authentication controller for managing logged-in user session */
    private AuthenticationControl authCtrl;
    
    /** User directory controller for verifying credentials and managing user data */
    private UserLoginDirectoryControl userLoginDirCtrl;
    

    /**
     * Constructs a LoginControl with required dependencies.
     * 
     * @param authCtrl the authentication controller
     * @param userLoginDirCtrl the user login directory controller
     */
    LoginControl(AuthenticationControl authCtrl, UserLoginDirectoryControl userLoginDirCtrl) {
        this.authCtrl = authCtrl;
        this.userLoginDirCtrl = userLoginDirCtrl;
    }

    /**
     * Handles user login by verifying credentials and setting up the session.
     * Company representatives must be approved before they can log in.
     * 
     * @param userID the user's ID
     * @param password the user's password
     * @throws IllegalArgumentException if credentials are invalid
     * @throws IllegalStateException if account is pending or rejected
     */
    public void handleLogin(String userID, String password){
        String identity = userLoginDirCtrl.verifyUser(userID, password);
        if (identity == null) throw new IllegalArgumentException("Login failed. Please check your ID and password input.");
        if (identity.equals("pending")) throw new IllegalStateException("Your account is pending approval. Please contact the administrator.");
        if (identity.equals("rejected")) throw new IllegalStateException("Your account has been rejected. Please contact the administrator.");

        User user = userLoginDirCtrl.createUser(userID, identity);
        authCtrl.setLoggedin(user);
        if ("CompanyRepresentative".equals(identity) || "companyRepresentative".equals(identity)) {
            String companyName = userLoginDirCtrl.getCompanyRepsCompany(userID);
            authCtrl.setCompanyName(companyName);
        }
    }
    
    /**
     * Handles company representative registration.
     * Creates a new account with email as userID and default password "password".
     * Account must be approved by career center staff before login is allowed.
     * 
     * @param name the representative's full name
     * @param companyName the company name
     * @param department the department within the company
     * @param postion the position/title
     * @param email the company email address (becomes the userID)
     * @return the assigned userID (which is the email)
     * @throws IllegalArgumentException if required fields are missing or invalid
     * @throws Exception if user creation fails
     */
    public String handleRegister(String name,String companyName,String department,String postion,String email) throws IllegalArgumentException, Exception{
        /*
         * assign id after register. 
         * password is default to "password" and will only be changed if requested. 
         * when registering, no choice to set password
         */
        
        if (name == null || companyName == null || email == null) {
            throw new IllegalArgumentException("Please fill in the essential information: name, company name, and email.");
        }
        String userID = userLoginDirCtrl.requestRegisterCompanyRep(name, companyName, department, postion, email);
        if (userID != null) {
            return userID;
        }
        throw new Exception("bug: LoginControl.java : handleRegister: no UserID is created. possibly fail to create a User");
    }
    
    /**
     * Changes the password for the currently logged-in user.
     * Requires the original password for verification.
     * 
     * @param originalPassword the current password
     * @param newPassword the new password to set
     * @throws IllegalStateException if no user is logged in
     * @throws IllegalArgumentException if original password is incorrect
     */
    public void changePassword(String originalPassword, String newPassword) throws IllegalStateException, IllegalArgumentException {
        if (!authCtrl.isLoggedIn()) throw new IllegalStateException("You are not logged in.");
        String userID = authCtrl.getUserID();
        if (userLoginDirCtrl.verifyUser(userID, originalPassword) == null) throw new IllegalArgumentException("Original password is incorrect.");
        userLoginDirCtrl.changePassword(userID, newPassword);
    }

}
