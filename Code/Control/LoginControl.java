package Control;
//when registered, the password is set to "password". Remind them to change.

import Entity.UserDirectory;

public class LoginControl {
    private AuthenticationControl authCtrl;
    private UserDirectory userDir;

    public LoginControl(AuthenticationControl authCtrl, UserDirectory userDir) {
        this.authCtrl = authCtrl;
        this.userDir = userDir;
    }

    public void handleLogin(String userID, String password){
        
    }
}
