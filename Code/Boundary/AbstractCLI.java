package Boundary;

import Control.InternshipControl;
import Control.LoginControl;
import Entity.Filter;

import Entity.InternshipOpportunity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public abstract class AbstractCLI {
    protected Scanner sc;
    protected InternshipControl intCtrl;
    protected LoginControl loginCtrl;
    protected Filter filter=null;

    public AbstractCLI(Scanner sc, InternshipControl intCtrl) {
        this.sc = sc;
        this.intCtrl = intCtrl;
    }

    public abstract void displayMenu();

    public void logout() {
        //
    }

    public void changePassword(){
        sc.nextLine(); //consume newline
        System.out.print("Enter your current password: ");
        String currentPassword = sc.nextLine();
        System.out.print("Enter your new password: ");
        String newPassword = sc.nextLine();
        boolean success = loginCtrl.changePassword(currentPassword, newPassword);
        if (success) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password.");
        }
    }

    private List<InternshipOpportunity> filterInternshipOpportunities(Filter filter) {
        String filterType = filter.getFilterType();
        boolean ascending = filter.isAscending();
        Map<String, List<String>> filterIn = filter.getFilterIn();
        // Implementation for filtering internship opportunities based on the criteria
        // can refer to reportControl.comprehensive()
        // filterType: the attribute sequence to sort by
        // e.g., "title"/ "companyName"/ "openDate"/ "numberOfSlots"
        // ascending: true for ascending order, false for descending order
        // filterIn: the filtering criteria, so that only internships matching these criteria are included
        // e.g., {"companyName": ["Company A", "Company B"], "internshipLevel": ["Internship", "Placement"]}
        // first, get all visible internship opportunities, which is done below
        // then, filter according to filterIn
        // finally, sort according to filterType and ascending

        List<InternshipOpportunity> Opplist = intCtrl.getAllVisibleInternshipOpportunities();
        // TODO: implement filtering and sorting logic here
        return null;
    }

    public void viewFilteredInternshipOpportunities() {
        if (filter != null) {
            System.out.println("Do you want to use previously set filter criteria? (Enter to proceed, any other key to set new filters): ");
            if ("".equalsIgnoreCase(sc.nextLine())) {
                List<InternshipOpportunity> filteredList = filterInternshipOpportunities(filter);
                // Display the filtered internship opportunities
                for (InternshipOpportunity opp : filteredList) {
                    System.out.println(opp);
                }
                return;
            }
        }
        System.out.print("Enter filter type (e.g., title, companyName, openDate, numberOfSlots): ");
        String filterType = sc.nextLine();
        System.out.print("Enter sorting order (asc/desc): ");
        String order = sc.nextLine();
        boolean ascending = "asc".equalsIgnoreCase(order);
        Map<String, List<String>> filterIn = new HashMap<>();
        // Collect additional filtering criteria from the user
        // ...
        filter = new Filter(filterType, ascending, filterIn);
        List<InternshipOpportunity> filteredList = filterInternshipOpportunities(filter);
        // Display the filtered internship opportunities
        for (InternshipOpportunity opp : filteredList) {
            System.out.println(opp);
        }
    }
}