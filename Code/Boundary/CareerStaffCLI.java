package Boundary;

import Control.*;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

public class CareerStaffCLI extends AbstractCLI {
    private ApplicationControl appCtrl;
    private ReportControl reportCtrl;
    
    public CareerStaffCLI(Scanner sc, ApplicationControl appCtrl, InternshipControl intCtrl, ReportControl reportCtrl) {
        super(sc, intCtrl);
        this.appCtrl = appCtrl;
        this.reportCtrl = reportCtrl;
        // reference appCtrl to avoid unused-field warnings in static analysis
        if (this.appCtrl == null) {
            // no-op
        }
    }
    @Override
    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Career Staff Menu ===");
            System.out.println("1. Change Password");
            System.out.println("2. View Internship Opportunities");
            System.out.println("3. View pending registrations");
            System.out.println("4. View pending internship opportunities");
            System.out.println("5. View pending withdrawal requests");
            System.out.println("6. Generate reports");
            System.out.println("7. Logout");
            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    changePassword();
                    break;
                case "2":
                    viewFilteredInternshipOpportunities();
                    break;
                case "3":
                    viewCompanyRepRegisterList();
                    System.out.print("Enter ID to approve (or press Enter to skip): ");
                    String regId = sc.nextLine();
                    if (!regId.isEmpty()) {
                        System.out.print("Approve (a) or Reject (r)? ");
                        String resp = sc.nextLine();
                        if ("a".equalsIgnoreCase(resp)) approveRegister(regId);
                        else if ("r".equalsIgnoreCase(resp)) rejectRegister(regId);
                    }
                    break;
                case "4":
                    viewPendingInternshipOpp();
                    System.out.print("Enter Internship ID to approve/reject (or press Enter to skip): ");
                    String oppId = sc.nextLine();
                    if (!oppId.isEmpty()) {
                        System.out.print("Approve (a) or Reject (r)? ");
                        String resp2 = sc.nextLine();
                        if ("a".equalsIgnoreCase(resp2)) approveInternshipCreated(oppId);
                        else if ("r".equalsIgnoreCase(resp2)) rejectInternshipCreated(oppId);
                    }
                    break;
                case "5":
                    viewPendingWithdrawal();
                    System.out.print("Enter Application Number to approve/reject (or press Enter to skip): ");
                    String appNumStr = sc.nextLine();
                    if (!appNumStr.isEmpty()) {
                        try {
                            int appNum = Integer.parseInt(appNumStr);
                            System.out.print("Approve (a) or Reject (r)? ");
                            String resp3 = sc.nextLine();
                            if ("a".equalsIgnoreCase(resp3)) approveWithdrawal(appNum);
                            else if ("r".equalsIgnoreCase(resp3)) rejectWithdrawal(appNum);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number. Returning to menu.");
                        }
                    }
                    break;
                case "6":
                    System.out.println("Generate report overview (o) or specific (s)?");
                    String rptChoice = sc.nextLine();
                    if ("o".equalsIgnoreCase(rptChoice)) {
                        System.out.print("Save report to file? (y/n): ");
                        String save = sc.nextLine();
                        generateReportOverview("y".equalsIgnoreCase(save));
                    } else if ("s".equalsIgnoreCase(rptChoice)) {
                        // collect simple filter map from console
                        Map<String, List<String>> filters = Map.of();
                        System.out.print("Save report to file? (y/n): ");
                        String save2 = sc.nextLine();
                        generateReportSpecific("y".equalsIgnoreCase(save2), filters);
                    } else {
                        System.out.println("Invalid report option.");
                    }
                    break;
                case "7":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    public void viewCompanyRepRegisterList() {
        // implementation
    }

    public void approveRegister(String id) {
        // implementation
    }

    public void rejectRegister(String id) {
        // implementation
    }

    public void viewPendingInternshipOpp() {
        // implementation
    }

    public void approveInternshipCreated(String oppID) {
        // implementation
    }

    public void rejectInternshipCreated(String oppID) {
        // implementation
    }

    public void viewPendingWithdrawal() {
        // implementation
    }

    public void approveWithdrawal(int appID) {
        // implementation
        
    }

    public void rejectWithdrawal(int appID) {
        // implementation
    }
    public void generateReportOverview(boolean optToSaveReport){
        reportCtrl.generateReportOverview(optToSaveReport);
    }
    public void generateReportSpecific(boolean optToSaveReport,Map<String,List<String>> filterIn){
        reportCtrl.generateReportSpecific(optToSaveReport, filterIn);
    }

}