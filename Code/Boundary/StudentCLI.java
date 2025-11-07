
package Boundary;
import Control.*;
import Entity.Application;
import Interface.*;
import java.util.*;
public class StudentCLI extends InterfaceCLI{
    private Scanner sc;
    private LoginControl loginCtrl;
    private AuthenticationControl authCtrl;
    private InternshipControl intCtrl;

    private ApplicationControl appCtrl;


    public StudentCLI(Scanner sc, LoginControl logCtrl, ApplicationControl appCtrl, AuthenticationControl authCtrl, InternshipControl intCtrl) {
        super(sc, authCtrl, intCtrl);
        this.appCtrl = appCtrl;
    }
    public void displayStudentMenu() {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Internship Opportunities");
            System.out.println("2. Submit Internship Application");
            System.out.println("3. Withdraw Internship Application");
            System.out.println("4. Check My Application Status");
            System.out.println("5. Accept Internship Opportunity");
            System.out.println("6. Reject Internship Opportunity");
            System.out.println("7. Logout");
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewFilteredInternshipOpportunities(sc);
                    break;
                case "2":
                    submitApplication();
                    break;
                case "3":
                    // Assume we have a method to get the application to withdraw
                    Application appToWithdraw = null; // Placeholder
                    withdrawApplication(appToWithdraw);
                    break;
                case "4":
                    checkMyApplicationStatus();
                    break;
                case "5":
                    // Assume we have a method to get the application to accept
                    Application appToAccept = null; // Placeholder
                    acceptInternshipOpportunity(appToAccept);
                    break;
                case "6":
                    // Assume we have a method to get the application to reject
                    Application appToReject = null; // Placeholder
                    rejectInternshipOpportunity(appToReject);
                    break;
                case "7":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
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
