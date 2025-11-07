package Interface;

import Control.InternshipControl;
im

import Entity.InternshipOpportunity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public abstract class InterfaceCLI {
    protected Scanner sc;
    protected InternshipControl intCtrl;
    protected LoginControl loginCtrl;

    public InterfaceCLI(Scanner sc, InternshipControl intCtrl) {
        this.sc = sc;
        this.intCtrl = intCtrl;
    }

    public void logout() {
        //
    }

    public void changePassword(){
        sc.nextLine(); //consume newline
        System.out.print("Enter your current password: ");
        String currentPassword = sc.nextLine();
        System.out.print("Enter your new password: ");
        String newPassword = sc.nextLine();
        // Call the control method to change the password
        
    }

    private List<InternshipOpportunity> filterInternshipOpportunities(String filterType, boolean ascending, Map<String,List<String>> filterIn) {
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

    public void viewFilteredInternshipOpportunities(Scanner sc) {
        System.out.print("Enter filter type (e.g., title, companyName, openDate, numberOfSlots): ");
        String filterType = sc.nextLine();
        System.out.print("Enter sorting order (asc/desc): ");
        String order = sc.nextLine();
        boolean ascending = "asc".equalsIgnoreCase(order);
        Map<String, List<String>> filterIn = new HashMap<>();
        // Collect additional filtering criteria from the user
        // ...
        List<InternshipOpportunity> filteredList = filterInternshipOpportunities(filterType, ascending, filterIn);
        // Display the filtered internship opportunities
        for (InternshipOpportunity opp : filteredList) {
            System.out.println(opp);
        }
    }
}