package Boundary;

import Control.*;
import Interface.*;


public class CareerStaffCLI implements InterfaceCLI {
    private UserLoginDirectoryControl userLoginDirectoryControl;
    private LoginControl loginCtrl;
    private AuthenticationControl authCtrl;

    public CareerStaffCLI() {
        this.userLoginDirectoryControl = new UserLoginDirectoryControl();
        this.authCtrl = new AuthenticationControl();
        this.loginCtrl = new LoginControl(authCtrl, userLoginDirectoryControl);
    }

    public CareerStaffCLI(LoginControl loginCtrl, AuthenticationControl authCtrl, InternshipControl intCtrl){
        
    }
    @Override
    public void login(String userID, String password) {
        //
    }
    @Override
    public void changePassowrd(String originalPassword, String newPassword){
        //
    }
    

    
}