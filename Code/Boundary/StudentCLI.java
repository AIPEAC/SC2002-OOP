
package Boundary;
import Control.*;
import Entity.Application;
import Interface.*;
import java.util.*;

public class StudentCLI extends InterfaceCLI{
    private ApplicationControl appCtrl;


    public StudentCLI(Scanner sc, ApplicationControl appCtrl, InternshipControl intCtrl) {
        super(sc, intCtrl);
        this.appCtrl = appCtrl;
    }
    @Override
    public void displaytMenu() {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. Change Password");
            System.out.println("2. View Internship Opportunities");
            System.out.println("3. Submit Internship Application");
            System.out.println("4. Check My Application Status");
            System.out.println("5. Logout");
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewFilteredInternshipOpportunities();
                    break;
                case "2":
                    changePassword();
                    break;
                case "3":
                    submitApplication();
                    break;
                case "4":
                    checkMyApplicationStatus();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void submitApplication() {
        System.out.println("\n=== Submit Internship Application ===");
        
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

        String status = appCtrl.getApplicationStatus();

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
