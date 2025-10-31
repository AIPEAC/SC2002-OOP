package Control;
//when registered, the password is set to "password". Remind them to change.

import Entity.Users.User;


public class LoginControl {
    private AuthenticationControl authCtrl;
    private UserLoginDirectory userDir;
    

    public LoginControl(AuthenticationControl authCtrl, UserLoginDirectory userDir) {
        this.authCtrl = authCtrl;
        this.userDir = userDir;
    }

    public void handleLogin(String userID, String password){
        /* 
         * to do: UserDirectory
        */

        boolean verifySuccess=userDir.verifyUser(userID, password);
        if (!verifySuccess){
            System.out.println("Login failed. Please check your ID and password input.");
            return;
        }
        User user=userDir.createUser(userID);
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
            String userID=userDir.requestRegisterCompanyRep(name, companyName, department, postion, email);
            try{
                if (userID!=null){
                    System.out.println("successfully created")
                    return userID;
                }else{
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
            User user=authCtrl.getUser();
            if (......){
                ;
                System.out.println("Password changed successfully.");
                return true;
            }else{
                System.out.println("Original password is incorrect.");
                return false;
            }
        }
    }
}
