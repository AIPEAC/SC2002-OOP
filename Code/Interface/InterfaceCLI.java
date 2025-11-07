package Interface;

import Control.LoginControl;
import Control.AuthenticationControl;
import Control.InternshipControl;

import Entity.InternshipOpportunity;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

public abstract class InterfaceCLI {
    Scanner sc;
    LoginControl loginCtrl;
    AuthenticationControl authCtrl;
    InternshipControl intCtrl;

    public InterfaceCLI(Scanner sc, AuthenticationControl authCtrl, InternshipControl intCtrl) {
        this.sc = sc;
        this.authCtrl = authCtrl;
        this.intCtrl = intCtrl;
    }

    public void logout() {
        //
    }

    public void changePassword(String originalPassword, String newPassword){
        //
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

    public void viewFilteredInternshipOpportunities(String filterType, boolean ascending, Map<String,List<String>> filterIn) {
        List<InternshipOpportunity> filteredList = filterInternshipOpportunities(filterType, ascending, filterIn);
        // Display the filtered internship opportunities
        for (InternshipOpportunity opp : filteredList) {
            System.out.println(opp);
        }
    }
}