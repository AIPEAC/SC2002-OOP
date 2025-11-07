
package Boundary;
import Control.*;
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
                    if (appCtrl.hasApprovedApplication()) {
                        appCtrl.getApprovedApplicationInternshipCompaniesAndIDs();
                        System.out.println("You have some approved applications for Internship Companies: ");
                        System.out.print("Enter Application Number to respond to offer: ");
                        int appNum = Integer.parseInt(sc.nextLine());
                        System.out.println("Do you want to accept (1) or reject (2) the offer?");
                        String response = sc.nextLine();   
                        if ("1".equals(response)) {
                            acceptInternshipOpportunity(appNum);
                        } else if ("2".equals(response)) {
                            rejectInternshipOpportunity(appNum);
                        } else {
                            System.out.println("Invalid option. Returning to main menu.");
                        }
                    } else {
                        System.out.println("No approved applications found.");
                    }
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
        appCtrl.loadStudentApplicationFromDB();
        appCtrl.checkApplications();
    }

    private void withdrawApplication() {
        System.out.println("\n=== Withdrawing Intership Application ===");
        System.out.println("Please note that this is irreversible once submitted.");
        System.out.println("Also, the previous automatic withdrawal will not be reverted.");
        System.out.print("Enter Application Number to withdraw: ");
        int appNumber = Integer.parseInt(sc.nextLine());
        appCtrl.requestWithdrawApplication(appNumber);
    }

    

    private void acceptInternshipOpportunity(int appNum) {
        System.out.println("\n=== Accept Internship Opportunity ===");
        appCtrl.acceptOffer(appNum);
        System.out.println("Internship accepted successfully! Other pending applications will be withdrawn.");
    }

    private void rejectInternshipOpportunity(int appNum) {
        System.out.println("\n=== Reject Internship Opportunity ===");
        appCtrl.rejectOffer(appNum);
        System.out.println("You have rejected this internship offer.");
    }
}
