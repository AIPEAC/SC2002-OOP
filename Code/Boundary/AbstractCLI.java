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
        System.out.println("Logged out (frontend).\n");
    }

    public void changePassword(){
        if (loginCtrl == null) {
            System.out.println("Password change is not available in this context.");
            return;
        }
        System.out.print("Enter your current password: ");
        String currentPassword = sc.nextLine();
        System.out.print("Enter your new password: ");
        String newPassword = sc.nextLine();
        boolean success = loginCtrl.changePassword(currentPassword, newPassword);
        // LoginControl already prints messages; echo concise result here
        if (success) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password.");
        }
    }

    private List<InternshipOpportunity> filterInternshipOpportunities(Filter filter) {
        // Basic filtering and sorting implementation.
        List<InternshipOpportunity> Opplist = intCtrl.getAllVisibleInternshipOpportunities();
        if (Opplist == null) return new java.util.ArrayList<>();

        Map<String, List<String>> criteria = filter.getFilterIn();
        if (criteria != null && !criteria.isEmpty()) {
            Opplist = Opplist.stream().filter(opp -> {
                for (Map.Entry<String, List<String>> e : criteria.entrySet()) {
                    String key = e.getKey();
                    List<String> vals = e.getValue();
                    if (vals == null || vals.isEmpty()) continue;
                    boolean matches = false;
                    switch (key) {
                        case "companyName":
                            for (String v : vals) if (opp.getCompanyName() != null && opp.getCompanyName().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "internshipLevel":
                            for (String v : vals) if (opp.getInternshipLevel() != null && opp.getInternshipLevel().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "preferredMajors":
                            if (opp.getPreferredMajors() != null) {
                                for (String v : vals) if (opp.getPreferredMajors().contains(v)) { matches = true; break; }
                            }
                            break;
                        case "internshipID":
                            for (String v : vals) if (opp.getInternshipID() != null && opp.getInternshipID().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "internshipTitle":
                            for (String v : vals) if (opp.getInternshipTitle() != null && opp.getInternshipTitle().equalsIgnoreCase(v)) matches = true;
                            break;
                        default:
                            // unknown filter key, ignore
                            matches = true;
                    }
                    if (!matches) return false; // must satisfy all provided criteria
                }
                return true;
            }).collect(java.util.stream.Collectors.toList());
        }

        // Sorting
        String filterType = filter.getFilterType();
        boolean ascending = filter.isAscending();
        if (filterType != null && !filterType.isEmpty()) {
            java.util.Comparator<InternshipOpportunity> cmp = null;
            switch (filterType) {
                case "title":
                    cmp = java.util.Comparator.comparing(InternshipOpportunity::getInternshipTitle, java.util.Comparator.nullsLast(String::compareToIgnoreCase));
                    break;
                case "companyName":
                    cmp = java.util.Comparator.comparing(InternshipOpportunity::getCompanyName, java.util.Comparator.nullsLast(String::compareToIgnoreCase));
                    break;
                case "openDate":
                    cmp = java.util.Comparator.comparing(InternshipOpportunity::getOpeningDate, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
                    break;
                case "numberOfSlots":
                    cmp = java.util.Comparator.comparingInt(InternshipOpportunity::getNumOfSlots);
                    break;
                default:
                    // no sorting
            }
            if (cmp != null) {
                if (!ascending) cmp = cmp.reversed();
                Opplist.sort(cmp);
            }
        }

        return Opplist;
    }

    /** Allow injection of LoginControl after construction (Main can set it). */
    public void setLoginControl(LoginControl loginCtrl) {
        this.loginCtrl = loginCtrl;
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