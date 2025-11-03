package Entity;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import Entity.Users.CompanyRepresentative;


public class InternshipOpportunity {
    private String internshipID;
    private String internshipTitle;
    private String description;
    private String internshipLevel="Basic"; //can be "Basic" "Intermediate" "Advanced"
    private List<String> preferredMajors;
    private Date openingDate;
    private Date closeDate;
    private String status = "pending";
    private String companyName;
    private CompanyRepresentative companyRepInCharge;
    private int numOfSlots = 1;
    private List<Application> applicationList;
    private List<String> selectedApplicantsID = new ArrayList<String>();
    private boolean isFull=false;
    private boolean visiblity=true;



    public InternshipOpportunity(int internshipID, String internshipTitle, String description, List<String> preferredMajors, String internshipLevel, Date openingDate, Date closeDate, String companyName, String companyRepInCharge, int numOfSlots) {
        //implement
    }

    public String getStatus() {
        return status;
    }

    public List<Application> getApplications() {
        //implement
        return null;
    }

    public List<Object> getDetails() {
        List<Object> details=new ArrayList<Object>();
        details.add(internshipID);
        details.add(internshipTitle);
        details.add(description);
        details.add(internshipLevel);
        details.add(preferredMajors);
        details.add(openingDate);
        details.add(closeDate);
        details.add(companyName);
        details.add(companyRepInCharge);
        details.add(numOfSlots);
        details.add(visiblity);
        return details;
    }

    public void setStatusToApproved() {
        status = "approved";
    }

    public void setStatusToRejected() {
        status = "rejected";
    }

    public void approveApplication(Application application) {
        application.setApplicationStatusSuccess();
    }

    public void rejectApplication(Application application) {
        application.setApplicationStatusFail();
    }

    public void addSelectedApplicant(String StudentID) {
        if (isFull()){
            return;
        }
        //implementation
    }

    public void removeSelectedApplicant(String StudentID) {
        //implementation
    }
    

    
    public boolean isFull(){
        return isFull;
    }
}