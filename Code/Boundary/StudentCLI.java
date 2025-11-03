//i am here
package Boundary;
import Control.*;
import Entity.Application;
import Interface.*;

public class StudentCLI implements InterfaceCLI{
    private LoginControl loginCtrl;
    private ApplicationControl appCtrl;

    StudentCLI(LoginControl loginCtrl, AuthenticationControl authCtrl, ApplicationControl appCtrl){
        
    }

    public StudentCLI(LoginControl logCtrl, ApplicationControl appCtrl) {
        this.loginCtrl = logCtrl;
        this.appCtrl = appCtrl;
    }

    public void submitApplication() {
        //
    }

    public void withdrawApplication(Application app) {
        //
    }

    public void checkMyApplicationStatus() {
        //
    }

    public void acceptInternshipOpportunity(Application app) {
        //
    }

    public void rejectInternshipOpportunity(Application app) {
        //
    }
}
