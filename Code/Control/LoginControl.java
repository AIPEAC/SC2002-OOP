package Control;
//when registered, the password is set to "password". Remind them to change.

import Entity.UserDirectory;
import Entity.Users.User;
import Entity.Users.CompanyRepresentative;

public class LoginControl {
    private AuthenticationControl authCtrl;
    private UserDirectory userDir;
    

    public LoginControl(AuthenticationControl authCtrl, UserDirectory userDir) {
        this.authCtrl = authCtrl;
        this.userDir = userDir;
    }

    public void handleLogin(String userID, String password){
        /* 
         * to do: UserDirectory
        */

        User user=userDir.verifyUser(userID, password);
        if (user==null){
            System.out.println("Login failed. Please check your ID and password input.");
            return;
        }
        authCtrl.setLoggedin(user);
        System.out.println("Login successful.");
    }
    public String handleRegister(String name,String companyName,String department,String postion,String email){
        if (name==null || companyName==null || department==null || postion==null || email==null){
            System.out.println("Please fill in the essential information");
            return null;
        }else{
            
        }
        return null;
    }
}
