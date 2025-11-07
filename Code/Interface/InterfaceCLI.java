package Interface;

import Control.LoginControl;
import Control.AuthenticationControl;
import Control.InternshipControl;


import Entity.InternshipOpportunity;
import java.util.List;

public abstract class InterfaceCLI {
    LoginControl loginCtrl;
    AuthenticationControl authCtrl;
    InternshipControl intCtrl;

    public InterfaceCLI(LoginControl loginCtrl, AuthenticationControl authCtrl, InternshipControl intCtrl) {
        this.loginCtrl = loginCtrl;
        this.authCtrl = authCtrl;
        this.intCtrl = intCtrl;
    }

    public void login(String userID, String password){
        //should I pass the logincontrol class as the attr list?
    }

    public void logout() {
        //
    }

    public void changePassword(String originalPassword, String newPassword){
        //
    }

    public List<InternshipOpportunity> filterInternshipOpportunities(String filterType, boolean ascending, String[] filterOptions) {
        //
        return null;
    }

    public void viewFilteredInternshipOpportunities(String filterType, boolean ascending, String[] filterOptions) {
        //
    }
}