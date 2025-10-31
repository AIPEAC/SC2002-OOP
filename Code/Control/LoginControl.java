package Control;
//when registered, the password is set to "password". Remind them to change.

import Entity.UserDirectory;
import Entity.Users.User;

public class LoginControl {
    private AuthenticationControl authCtrl;
    private UserDirectory userDir;

    public LoginControl(AuthenticationControl authCtrl, UserDirectory userDir) {
        this.authCtrl = authCtrl;
        this.userDir = userDir;
    }

    public void handleLogin(String userID, String password){
        User user=userDir.verifyUser(userID, password);
        if (user==null){
            System.out.println("Login failed. Please check your ID and password input.");
            return;
        }
        authCtrl.setLoggedin(user);
        System.out.println("Login successful.");
    }
}
