package Control;
//when registered, the password is set to "password". Remind them to change.

import Entity.Users.User;


public class LoginControl {
    private AuthenticationControl authCtrl;
    private UserLoginDirectoryControl userLoginDirCtrl;
    

    public LoginControl(AuthenticationControl authCtrl, UserLoginDirectoryControl userLoginDirCtrl) {
        this.authCtrl = authCtrl;
        this.userLoginDirCtrl = userLoginDirCtrl;
    }

    public void handleLogin(String userID, String password){
        String identity=userLoginDirCtrl.verifyUser(userID, password);
        if (identity == null){
            System.out.println("Login failed. Please check your ID and password input.");
            return;
        } else if (identity.equals("ACCOUNT_PENDING")) {
            System.out.println("Your account is pending approval. Please contact the administrator.");
            return;
        } else if (identity.equals("ACCOUNT_REJECTED")) {
            System.out.println("Your account has been rejected. Please contact the administrator.");
            return;
        }
        User user=userLoginDirCtrl.createUser(userID,identity);
        authCtrl.setLoggedin(user);
        System.out.println("Login successful.");
    }
    public String handleRegister(String name,String companyName,String department,String postion,String email){
        /*
         * assign id after register. 
         * password is default to "password" and will only be changed if requested. 
         * when registering, no choice to set password
         */
        
        if (name==null || companyName==null || department==null || postion==null || email==null){
            System.out.println("Please fill in the essential information.");
            return null;
        }else{
            String userID=userLoginDirCtrl.requestRegisterCompanyRep(name, companyName, department, postion, email);
            try{
                if (userID!=null){
                    System.out.println("successfully created");
                    return userID;
                }
                else{
                    throw new Exception("bug: LoginControl.java : handleRegister: no UserID is created. possibly fail to create a User");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
    public boolean changePassword(String originalPassword, String newPassword){
        if (authCtrl.isLoggedIn()){
            String userID=authCtrl.getUserID();
            
            if (userLoginDirCtrl.verifyUser(userID, originalPassword)!=null){
                userLoginDirCtrl.changePassword(userID, newPassword);
                System.out.println("Password changed successfully.");
                return true;
            }else{
                System.out.println("Original password is incorrect.");
                return false;
            }
        }else{
            System.out.println("You are not logged in.");
            return false;
        }
    }
}
