package Boundary;
import Control.*;
import Interface.*;
import java.util.List;
import java.util.Date;
import Entity.InternshipOpportunity;

public class CompanyRepresentativeCLI extends InterfaceCLI{
    private LoginControl loginCtrl;
    private AuthenticationControl authCtrl;
    private InternshipControl intCtrl;

    public CompanyRepresentativeCLI(LoginControl logCtrl, AuthenticationControl authCtrl, InternshipControl intCtrl) {
        super(logCtrl, authCtrl, intCtrl);
        this.loginCtrl = logCtrl;
        this.authCtrl = authCtrl;
        this.intCtrl = intCtrl;
    }

    public void register(String name, String companyName, String department, String position, String email) {
        //
    }

    public void createInternshipOpportunity(String internshipTitle, String title, String internshipLevel, List<String> preferredMajors, Date openDate, Date closeDate, String companyName, int numberOfSlots) {
        //remember to get self name passed to control
        //as companyRepInCharge is required for InternshipOpportunity Control
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
