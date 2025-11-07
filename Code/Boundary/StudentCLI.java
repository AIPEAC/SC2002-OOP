
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
        appCtrl.loadStudentApplicationFromDB();
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
                    return;
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
        System.out.println("\n=== Your Application Status ===");
        appCtrl.checkApplications();
    }

    private void withdrawApplication() {
        System.out.println("\n=== Withdrawing Intership Application ===");
        System.out.println("Please note that this is irreversible once submitted.");
        System.out.print("Enter Application Number to withdraw: ");
        int appNumber = Integer.parseInt(sc.nextLine());
        Application app = appCtrl.getApplicationByNumber(appNumber);
        if (app != null) {
            appCtrl.requestWithdrawApplication(app);
            appCtrl.addApplicationToPendingList(app);
            System.out.println("Request to withdraw is submitted. Awaiting approval from Career Staff Centre");
        } else {
            System.out.println("Application not found.");
        }
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
