//i am here, I am also here
package Boundary;
import Control.*;
import Entity.Application;
import Entity.Users.Student;
import Interface.*;
import java.util.Scanner;
public class StudentCLI implements InterfaceCLI{
    private LoginControl loginCtrl;
    private ApplicationControl appCtrl;
    private Scanner sc = new Scanner(System.in);
    private Student currentStudent;

    StudentCLI(LoginControl loginCtrl, AuthenticationControl authCtrl, ApplicationControl appCtrl){
        
    }   

    public StudentCLI(LoginControl logCtrl, ApplicationControl appCtrl) {
        this.loginCtrl = logCtrl;
        this.appCtrl = appCtrl;
    }

    public void submitApplication() {
        System.out.println("\n=== Submit Internship Application ===");
        appCtrl.makeApplication();
        System.out.println("Application is submited successfully");
    }

    public void withdrawApplication(Application app) {
        System.out.println("\n=== Withdrawing Intership Application ===");
        appCtrl.requestWithdrawApplication(app);
        appCtrl.addApplicationToPendingList(app);
        System.out.println("Request to withdraw is submitted. Awaiting approval from Career Staff Centre");
    }

    public void checkMyApplicationStatus() {


    }

    public void acceptInternshipOpportunity(Application app) {
        //
    }

    public void rejectInternshipOpportunity(Application app) {
        //
    }
}
