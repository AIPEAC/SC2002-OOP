package Frontend_Console.Boundary;

import java.util.Map;
import java.util.List;
import java.util.Scanner;

import Backend.Control.*;

public class CareerStaffCLI extends AbstractCLI {
    private ApplicationControl appCtrl;
    private ReportControl reportCtrl;
    private UserControl userCtrl;
    
    public CareerStaffCLI(Scanner sc, ApplicationControl appCtrl, InternshipControl intCtrl, ReportControl reportCtrl, UserControl userCtrl) {
        super(sc, intCtrl);
        this.appCtrl = appCtrl;
        this.reportCtrl = reportCtrl;
        this.userCtrl = userCtrl;
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
                        System.out.print("Approve (a) or Reject (r)? ");
                        String resp3 = sc.nextLine();
                        if ("a".equalsIgnoreCase(resp3)) approveWithdrawal(appNumStr);
                        else if ("r".equalsIgnoreCase(resp3)) rejectWithdrawal(appNumStr);
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
    List<String> pending = userCtrl.getPendingCompanyRepList();
        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending company representative registrations.");
            return;
        }
        System.out.println("Pending Company Representative Registrations:");
        for (String line : pending) {
            System.out.println(line);
        }
    }

    public void approveRegister(String id) {
        try {
            userCtrl.approveRegister(id);
            System.out.println("Approved company representative registration: " + id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void rejectRegister(String id) {
        try {
            userCtrl.rejectRegister(id);
            System.out.println("Rejected company representative registration: " + id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewPendingInternshipOpp() {
    List<String> pending = intCtrl.getPendingInternshipOpportunities();
        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending internship opportunities.");
            return;
        }
        System.out.println("Pending Internship Opportunities:");
        for (String line : pending) {
            System.out.println(line);
        }
    }

    public void approveInternshipCreated(String oppID) {
        try {
            intCtrl.approveInternshipCreationByID(oppID);
            System.out.println("Approved internship creation: " + oppID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void rejectInternshipCreated(String oppID) {
        try {
            intCtrl.rejectInternshipCreationByID(oppID);
            System.out.println("Rejected internship creation: " + oppID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewPendingWithdrawal() {
    List<String> pending = appCtrl.getPendingWithdrawals();
        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }
        System.out.println("Pending Withdrawal Requests:");
        for (String line : pending) {
            System.out.println(line);
        }
    }

    public void approveWithdrawal(String appNum) {
        try {
            appCtrl.approveWithdrawal(appNum);
            System.out.println("Approved withdrawal for application: " + appNum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void rejectWithdrawal(String appNum) {
        try {
            appCtrl.rejectWithdrawal(appNum);
            System.out.println("Rejected withdrawal for application: " + appNum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void generateReportOverview(boolean optToSaveReport){
        try {
            List<String> lines = reportCtrl.generateReportOverview(optToSaveReport);
            for (String l : lines) System.out.println(l);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void generateReportSpecific(boolean optToSaveReport,Map<String,List<String>> filterIn){
        try {
            List<String> lines = reportCtrl.generateReportSpecific(optToSaveReport, filterIn);
            for (String l : lines) System.out.println(l);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}