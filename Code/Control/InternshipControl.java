package Control;
import Entity.InternshipOpportunity;
import java.util.List;
import java.util.Date;
import Entity.Application;

public class InternshipControl {
    private List<String> pendingInternshipOppID;
    private AuthenticationControl authCtrl;

    //when initialize the internships. read from csv.
    //the last column will be several int seperated by spaces.
    //read those int and initialize the internships with List<application>, using the int get from the csv.

    public InternshipControl(AuthenticationControl authCtrl) {
        //implementation
    }

    public void requestCreateInternshipOpportunity(int internshipID, String internshipTitle, String description, String internshipLevel, List<String> preferredMajors, Date openDate, Date closeDate, String companyName, String companyRepInChargeID, int numberOfSlots) {
        //implementation
    }

    public List<InternshipOpportunity> getInternshipOpportunities(String oppID) {
        //implementation
        return null;
    }

    public List<InternshipOpportunity> getPendingInternshipOpportunities() {
        //implementation
        return null;
    }

    public List<InternshipOpportunity> getAllVisibleInternshipOpportunities() {
        //implementation
        return null;
    }

    public List<InternshipOpportunity> getAllInternshipOpportunities(){
        //implementation
        return null;
    }
    
    public List<Object> getInternshipDetails(InternshipOpportunity opp) {
        if (authCtrl.isLoggedIn()){
            //implementation
            return opp.getDetailsForViewing();
        }
        System.out.println("Please login to view internship details.");
        return null;
    }

    public List<Application> getInternshipStatus(InternshipOpportunity opp) {
        //implementation
        return null;
    }

    public void approveInternshipCreation(InternshipOpportunity opp) {
        //implementation
    }

    public void rejectInternshipCreation(InternshipOpportunity rejectInternshipCreation) {
        //implementation
    }

    public void approve(Application app) {
        //implementation
    }

    public void loadInternshipOpportunityFromDB() {
        //implementation
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
