package Boundary;
import Control.*;
import java.util.Scanner;
import java.util.List;

public class CompanyRepresentativeCLI extends AbstractCLI{
    private boolean hasCheckedMyOpps = false;

    public CompanyRepresentativeCLI(Scanner sc, InternshipControl intCtrl) {
        super(sc, intCtrl);
    }
    @Override
    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Company Representative Menu ===");
            System.out.println("1. Change Password");
            System.out.println("2. View Internship Opportunities");
            System.out.println("3. Create Internship Opportunity");
            System.out.println("4. Check My Internship Opportunities' Status");
            if (hasCheckedMyOpps) {
                System.out.println("5. Approve/Reject Applications for My Opportunities");
                System.out.println("6. Toggle Opportunity Visibility / Other Actions");
                System.out.println("7. Logout");
            } else {
                System.out.println("5. Logout");
            }
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
                    createInternshipOpportunity();
                    break;
                case "4":
                    checkMyInternshipOppStatus();
                    hasCheckedMyOpps = true;
                    break;
                case "5":
                    if (hasCheckedMyOpps) {
                        approveApplication();
                    } else {
                        System.out.println("Logging out...");
                        return;
                    }
                    break;
                case "6":
                    if (hasCheckedMyOpps) {
                        System.out.print("Enter Internship ID to toggle visibility (or press Enter to cancel): ");
                        String oppId = sc.nextLine();
                        if (!oppId.isEmpty()) {
                            intCtrl.changeVisibilityByID(oppId);
                            System.out.println("Toggled visibility for: " + oppId);
                        }
                    } else {
                        System.out.println("Invalid option. Please try again.");
                    }
                    break;
                case "7":
                    if (hasCheckedMyOpps) {
                        System.out.println("Logging out...");
                        return;
                    } else {
                        System.out.println("Invalid option. Please try again.");
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }   
    }
    

    private void createInternshipOpportunity() {
        String internshipTitle;
        String description; 
        String internshipLevel; 
    List<String> preferredMajors = new java.util.ArrayList<>(); 
    // we collect raw strings for dates and slots; control will parse/validate
    String openInput;
    String closeInput;
    String slotsInput;

        // Prompt for and read the above details using Scanner (sc)
        System.out.print("Enter Internship Title: ");
        internshipTitle = sc.nextLine();
        System.out.print("Enter Description: ");
        description = sc.nextLine();
        System.out.print("Enter Internship Level (B: Basic/I: Intermediate/A: Advanced): ");
        switch(sc.nextLine()) {
            case "B":
                internshipLevel = "Basic";
                break;
            case "I":
                internshipLevel = "Intermediate";
                break;
            case "A":
                internshipLevel = "Advanced";
                break;
            default:
                internshipLevel = "Basic";
                break;
        }
        // Restrict majors to the list in Code/Lib/majors.csv
        List<String> allMajors = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("Code/Lib/majors.csv"))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // remove surrounding quotes if present
                    String m = line.trim();
                    if (m.startsWith("\"") && m.endsWith("\"")) {
                        m = m.substring(1, m.length() - 1);
                    }
                    allMajors.add(m);
                }
            }
        } catch (java.io.IOException e) {
            // fallback: accept comma separated input
            System.out.print("Enter Preferred Majors (comma separated): ");
            String[] majorsArray = sc.nextLine().split(",");
            preferredMajors = List.of(majorsArray);
            allMajors = null;
        }

        if (allMajors != null) {
            System.out.println("Select preferred majors from the list below (enter numbers comma-separated).\n0 = Any majors (default). Max 5 selections.");
            System.out.println("0) Any majors");
            for (int i = 0; i < allMajors.size(); i++) {
                System.out.println((i + 1) + ") " + allMajors.get(i));
            }
            System.out.print("Your selection: ");
            String sel = sc.nextLine().trim();
            if (sel.isEmpty() || sel.equals("0")) {
                preferredMajors = new java.util.ArrayList<>(); // empty = any
            } else {
                String[] parts = sel.split(",");
                java.util.LinkedHashSet<Integer> picks = new java.util.LinkedHashSet<>();
                for (String p : parts) {
                    if (p.trim().isEmpty()) continue;
                    try {
                        int idx = Integer.parseInt(p.trim());
                        if (idx == 0) {
                            picks.clear();
                            break;
                        }
                        if (idx >= 1 && idx <= allMajors.size()) {
                            picks.add(idx - 1);
                        }
                        if (picks.size() >= 5) break;
                    } catch (NumberFormatException ex) {
                        // ignore invalid entries
                    }
                }
                if (picks.isEmpty()) {
                    preferredMajors = new java.util.ArrayList<>();
                } else {
                    preferredMajors = new java.util.ArrayList<>();
                    for (int i : picks) preferredMajors.add(allMajors.get(i));
                }
            }
        }
        System.out.print("Enter Opening Date (yyyy-MM-dd) (leave empty for today): ");
        openInput = sc.nextLine();
        System.out.print("Enter Closing Date (yyyy-MM-dd) (leave empty for today): ");
        closeInput = sc.nextLine();
        System.out.print("Enter Number of Slots (press Enter for default 1): ");
        slotsInput = sc.nextLine();

        // send raw strings to control; control will validate/parse and apply defaults
        intCtrl.requestCreateInternshipOpportunity(internshipTitle,
            description, internshipLevel,
            preferredMajors, openInput, closeInput, slotsInput);
    }

    private void checkMyInternshipOppStatus() {
        intCtrl.getInternshipStatus();
    }

    public void approveApplication() {
        System.out.print("Enter Internship ID to view applications (or press Enter to view all): ");
        String oppId = sc.nextLine();
        intCtrl.viewApplications(oppId);
        System.out.print("Enter Application Number to approve (or press Enter to cancel): ");
        String input = sc.nextLine();
        if (input.trim().isEmpty()) {
            System.out.println("No action taken.");
            return;
        }
        try {
            int appNum = Integer.parseInt(input.trim());
            intCtrl.approveApplicationAsCompanyRep(appNum);
        } catch (NumberFormatException e) {
            System.out.println("Invalid application number.");
        }
    }

    public void rejectApplication() {
        System.out.print("Enter Internship ID to view applications (or press Enter to view all): ");
        String oppId = sc.nextLine();
        intCtrl.viewApplications(oppId);
        System.out.print("Enter Application Number to reject (or press Enter to cancel): ");
        String input = sc.nextLine();
        if (input.trim().isEmpty()) {
            System.out.println("No action taken.");
            return;
        }
        try {
            int appNum = Integer.parseInt(input.trim());
            intCtrl.rejectApplicationAsCompanyRep(appNum);
        } catch (NumberFormatException e) {
            System.out.println("Invalid application number.");
        }
    }

    public void toggleOppVisibility(String oppID) {
        if (oppID == null || oppID.trim().isEmpty()) {
            System.out.print("Enter Internship ID to toggle visibility: ");
            String id = sc.nextLine();
            if (id.trim().isEmpty()) return;
            intCtrl.changeVisibilityByID(id);
            System.out.println("Toggled visibility for " + id);
            return;
        }
        intCtrl.changeVisibilityByID(oppID);
        System.out.println("Toggled visibility for " + oppID);
    }
}
