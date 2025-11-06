package Control;
import Entity.InternshipOpportunity;
import java.util.List;
import java.util.Date;
import Entity.Users.CompanyRepresentative;
import Entity.Application;

public class InternshipControl {
    private List<String> pendingInternshipOppID;
    private AuthenticationControl authCtrl;

    //when initialize the internships. read from csv.
    //the last column will be several int seperated by spaces.
    //read those int and initialize the internships with List<application>, using the int get from the csv.

    public InternshipControl(AuthenticationControl authCtrl) {
        //
    }

    public void requestCreateInternshipOpportunity(int internshipID, String internshipTitle, String description, String internshipLevel, List<String> preferredMajors, Date openDate, Date closeDate, String companyName, String companyRepInChargeID, int numberOfSlots) {
        //
    }

    public List<InternshipOpportunity> getInternshipOpportunities(String oppID) {
        //
        return null;
    }

    public List<InternshipOpportunity> getPendingInternshipOpportunities() {
        //
        return null;
    }

    public List<InternshipOpportunity> getAllInternshipOpportunities(){
        return null;
    }

    public List<Application> getInternshipStatus(InternshipOpportunity opp) {
        //
        return null;
    }

    public void approveInternshipCreation(InternshipOpportunity opp) {
        //
    }

    public void rejectInternshipCreation(InternshipOpportunity rejectInternshipCreation) {
        //
    }

    public void approve(Application app) {
        //
    }

    public void loadInternshipOpportunityFromDB() {
        //
    }

    public void withdrawEveryOtherApplication(String studentID) {
        //
    }

    public void reject(Application app) {
        //
    }

    public void reject(InternshipOpportunity opp) {
        //
    }

    public void changeVisibility(InternshipOpportunity opp) {
        //
    }

    public void addOpportunityToPendingList(InternshipOpportunity opp) {
        //
    }

    public void removeOpportunityToPendingList(InternshipOpportunity opp) {
        //
    }
}
