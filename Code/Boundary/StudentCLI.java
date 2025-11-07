
package Boundary;
import Control.*;
import Entity.Application;
import Interface.*;
import java.util.*;
public class StudentCLI implements InterfaceCLI{
    private LoginControl loginCtrl;
    private ApplicationControl appCtrl;
    private Scanner sc = new Scanner(System.in);

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
        System.out.println("\n=== Check Application Status ===");

        System.out.println("Enter Application ID: ");
        String id = sc.nextLine();

        appCtrl.loadApplicationFromDB();

        Application dummyApp = new Application(0, null, id);
        String status = dummyApp.getApplicationStatus();

        if (status != null) {
            System.out.println("Application " + id + " Status: " + status);
        } else {
            System.out.print("Application " + id + " does not exist!");
        }

    }

    public void acceptInternshipOpportunity(Application app) {
        System.out.println("\n=== Accept Internship Opportunity ===");
        String status = appCtrl.getApplicationStatus(app);

        if (!"approved".equalsIgnoreCase(status)) {
            System.out.println("You can only accept applications marked as successful");
        }

        System.out.println("Internship accepted successfully! Other pending applications will be withdrawn.");

    }

    public void rejectInternshipOpportunity(Application app) {
        System.out.println("\n=== Reject Internship Opportunity ===");
        String status = appCtrl.getApplicationStatus(app);

        if(!"approved".equalsIgnoreCase(status)) {
            System.out.println("You cna only reject applications marked as successful");
        }

        System.out.println("You have rejected this internship offer.");
    }
}
