package Boundary;
import Control.*;
import Interface.*;
import java.util.List;
import java.util.Date;
import Entity.InternshipOpportunity;

public class CompanyRepresentativeCLI implements InterfaceCLI{
    private LoginControl loginCtrl;
    private InternshipControl intCtrl;

    public CompanyRepresentativeCLI(LoginControl logCtrl, InternshipControl intCtrl) {
        this.loginCtrl = logCtrl;
        this.intCtrl = intCtrl;
    }

    @Override
    public void login(String userID, String password) {
        //
    }
    public void changePassword(String originalPassword, String newPassword){
        //
    }

    public void register(String name, String companyName, String department, String position, String email) {
        //
    }

    public void createInternshipOpportunity(String internshipTitle, String title, String internshipLevel, List<String> preferredMajors, Date openDate, Date closeDate, String companyName, int numberOfSlots) {
        //
    }

    public void checkMyInternshipOppStatus() {
        //
    }

    public void approveApplication() {
        //
    }

    public void rejectApplication() {
        //
    }

    public void toggleOppVisibility(InternshipOpportunity opp) {
        //
    }
}
