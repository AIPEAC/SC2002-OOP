package Interface;

import Control.LoginControl;
import Control.AuthenticationControl;
import Control.InternshipControl;


import Entity.InternshipOpportunity;
import java.util.List;
import java.util.Scanner;

public abstract class InterfaceCLI {
    Scanner sc;
    LoginControl loginCtrl;
    AuthenticationControl authCtrl;
    InternshipControl intCtrl;

    public InterfaceCLI(Scanner sc, AuthenticationControl authCtrl, InternshipControl intCtrl) {
        this.sc = sc;
        this.authCtrl = authCtrl;
        this.intCtrl = intCtrl;
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