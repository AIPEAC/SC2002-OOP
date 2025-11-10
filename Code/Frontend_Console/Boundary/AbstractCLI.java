package Frontend_Console.Boundary;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Backend.Control.InternshipControl;
import Backend.Control.LoginControl;
import Frontend_Console.Helper.Filter;

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
        try {
            loginCtrl.changePassword(currentPassword, newPassword);
            System.out.println("Password changed successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /** Allow injection of LoginControl after construction (Main can set it). */
    public void setLoginControl(LoginControl loginCtrl) {
        this.loginCtrl = loginCtrl;
    }

    public void viewFilteredInternshipOpportunities() {
        if (filter != null) {
            System.out.println("Do you want to use previously set filter criteria? (Enter to proceed, any other key to set new filters): ");
            if ("".equalsIgnoreCase(sc.nextLine())) {
                List<String> lines = intCtrl.getAllVisibleInternshipOpportunitiesForDisplay(filter.getFilterType(), filter.isAscending(), filter.getFilterIn());
                for (String l : lines) System.out.println(l);
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
        List<String> lines = intCtrl.getAllVisibleInternshipOpportunitiesForDisplay(filter.getFilterType(), filter.isAscending(), filter.getFilterIn());
        for (String l : lines) System.out.println(l);
    }
}