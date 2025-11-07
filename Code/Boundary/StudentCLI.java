
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
    public void displayMenu() {
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
                    changePassword();
                    break;
                case "2":
                    viewFilteredInternshipOpportunities();
                    System.out.println("\n");
                    System.out.println("Do you want to apply for any internship? (y to submit): ");
                    String applyChoice = sc.nextLine();
                    if ("y".equalsIgnoreCase(applyChoice)) {
                        submitApplication();
                    }
                    break;
                case "4":
                    checkMyApplicationStatus();
                    break;
                case "5":
                    System.out.println("Logging out...");
                    return; // Exit the menu loop
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void submitApplication() {
        System.out.println("Enter Internship ID: (refer to the internship list above)");
        String id = sc.nextLine();
        appCtrl.makeApplication(id);
    }

    private void checkMyApplicationStatus() {
        System.out.println("\n=== Check My Application Status ===");
        List<Application> myApplications = appCtrl.getApplicationsByStudent();

        if (myApplications.isEmpty()) {
            System.out.println("You have no internship applications.");
            return;
        }

        System.out.printf("%-15s %-20s %-15s %-20s\n", "Application ID", "Internship ID", "Status", "Withdrawn Status");
        for (Application app : myApplications) {
            String status = appCtrl.getApplicationStatus(app);
            System.out.printf("%-15s %-20s %-15s\n", app.getApplicationIndex(), app.getInternshipID(), status);
        }

        System.out.println("Do you want to withdraw an application? (y to withdraw): ");
        String withdrawChoice = sc.nextLine();
        if ("y".equalsIgnoreCase(withdrawChoice)) {
            System.out.println("Enter Application ID to withdraw: ");
            String appIdStr = sc.nextLine();
            Application appToWithdraw = null;
            for (Application app : myApplications) {
                if (String.valueOf(app.getApplicationIndex()).equals(appIdStr)) {
                    appToWithdraw = app;
                    break;
                }
            }
            if (appToWithdraw != null) {
                withdrawApplication(appToWithdraw);
            } else {
                System.out.println("Invalid Application ID.");
            }
        }
    }

    private void withdrawApplication(Application app) {
        System.out.println("\n=== Withdrawing Intership Application ===");
        appCtrl.requestWithdrawApplication(app);
        appCtrl.addApplicationToPendingList(app);
        System.out.println("Request to withdraw is submitted. Awaiting approval from Career Staff Centre");
    }

    

    private void acceptInternshipOpportunity(Application app) {
        System.out.println("\n=== Accept Internship Opportunity ===");
        String status = appCtrl.getApplicationStatus(app);

        if (!"approved".equalsIgnoreCase(status)) {
            System.out.println("You can only accept applications marked as successful");
        }

        System.out.println("Internship accepted successfully! Other pending applications will be withdrawn.");

    }

    private void rejectInternshipOpportunity(Application app) {
        System.out.println("\n=== Reject Internship Opportunity ===");
        String status = appCtrl.getApplicationStatus(app);

        if(!"approved".equalsIgnoreCase(status)) {
            System.out.println("You can only reject applications marked as successful");
        }

        System.out.println("You have rejected this internship offer.");
    }
}
