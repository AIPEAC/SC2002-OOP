package Backend.Control;
//when registered, the password is set to "password". Remind them to change.

import Backend.Entity.Users.User;



public class LoginControl {
    private AuthenticationControl authCtrl;
    private UserLoginDirectoryControl userLoginDirCtrl;
    

    public LoginControl(AuthenticationControl authCtrl, UserLoginDirectoryControl userLoginDirCtrl) {
        this.authCtrl = authCtrl;
        this.userLoginDirCtrl = userLoginDirCtrl;
    }

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
    public void changePassword(String originalPassword, String newPassword) throws IllegalStateException, IllegalArgumentException {
        if (!authCtrl.isLoggedIn()) throw new IllegalStateException("You are not logged in.");
        String userID = authCtrl.getUserID();
        if (userLoginDirCtrl.verifyUser(userID, originalPassword) == null) throw new IllegalArgumentException("Original password is incorrect.");
        userLoginDirCtrl.changePassword(userID, newPassword);
    }

}
