package Boundary;
import Control.*;
import java.util.Scanner;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import Entity.InternshipOpportunity;


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
        List<String> preferredMajors; 
        Date openDate; 
        Date closeDate; 
        int numberOfSlots;

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
        // TODO: we may want to choose from a predefined list of majors later
        // for now, just accept comma separated input. since it does not reject students
        System.out.print("Enter Preferred Majors (comma separated): ");
        String[] majorsArray = sc.nextLine().split(",");

        preferredMajors = List.of(majorsArray);
        System.out.print("Enter Opening Date (yyyy-MM-dd) (leave empty for today): ");
        String openInput = sc.nextLine();
        if (isValidDate(openInput)) {
            openDate = parseDate(openInput);
        } else {
            System.out.println("Invalid date format. Setting to today's date.");
            openDate = new Date();
        }
        System.out.print("Enter Closing Date (yyyy-MM-dd) (leave empty for today): ");
        String closeInput = sc.nextLine();
        if (isValidDate(closeInput)) {
            closeDate = parseDate(closeInput);
        } else {
            System.out.println("Invalid date format. Setting to today's date.");
            closeDate = new Date();
        }
        numberOfSlots = 1;
        System.out.print("Enter Number of Slots (press Enter for default 1): ");
        String slotsInput = sc.nextLine();
        if (!slotsInput.trim().isEmpty()) {
            try {
                numberOfSlots = Integer.parseInt(slotsInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number; defaulting to 1.");
                numberOfSlots = 1;
            }
        }
        intCtrl.requestCreateInternshipOpportunity(internshipTitle,
            description, internshipLevel, 
            preferredMajors, openDate, closeDate, numberOfSlots);
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

    public void toggleOppVisibility(InternshipOpportunity opp) {
        if (opp == null) {
            System.out.print("Enter Internship ID to toggle visibility: ");
            String id = sc.nextLine();
            if (id.trim().isEmpty()) return;
            intCtrl.changeVisibilityByID(id);
            System.out.println("Toggled visibility for " + id);
            return;
        }
        intCtrl.changeVisibility(opp);
        System.out.println("Toggled visibility for provided internship.");
    }


    // Helpers
    private boolean isValidDate(String dateStr) {
        // Simple validation for date format yyyy-MM-dd
        // allow empty input to default to today's date
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return true;
        }
        return dateStr.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return new Date();
        }
        String[] parts = dateStr.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;   // Month is 0-based
        int day = Integer.parseInt(parts[2]);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }
}
