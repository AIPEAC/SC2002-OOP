package Entity;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import Entity.Users.CompanyRepresentative;

public class InternshipOpportunity {

    public enum InternshipLevel { BASIC, INTERMEDIATE, ADVANCED }

    private String internshipID;
    private String internshipTitle;
    private String description;
    private InternshipLevel internshipLevel= InternshipLevel.BASIC; //can be "Basic" "Intermediate" "Advanced"
    private List<String> preferredMajors = new ArrayList<String>();
    private Date openingDate;
    private Date closeDate;
    private String status = "pending";
    private String companyName;
    private CompanyRepresentative companyRepInCharge;
    private int numOfSlots = 1;
    private List<Application> applicationList = new ArrayList<Application>();
    private List<String> selectedApplicantsID = new ArrayList<String>();
    private boolean isFull=false;
    private boolean visiblity=true;

    public InternshipOpportunity(String internshipID, String internshipTitle, String description, List<String> preferredMajors, InternshipLevel internshipLevel, Date openingDate, Date closeDate, String companyName, CompanyRepresentative companyRepInCharge, int numOfSlots) {

        this.internshipID = internshipID;
        this.internshipTitle = internshipTitle;
        this.description = description;
        this.preferredMajors = preferredMajors;
        this.internshipLevel = internshipLevel;
        this.openingDate = openingDate;
        this.closeDate = closeDate;
        this.companyName = companyName;
        this.companyRepInCharge = companyRepInCharge;
        this.numOfSlots = numOfSlots;
    }

    public String getStatus() {
        return status;
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applicationList);
    }

    public List<Object> getDetails() {
        List<Object> details=new ArrayList<Object>();
        details.add(internshipID); //0
        details.add(internshipLevel); //1
        details.add(preferredMajors); //2
        details.add(openingDate); //3
        details.add(closeDate); //4
        details.add(companyName); //5
        details.add(numOfSlots); //6
        details.add(visiblity); //7
        details.add(isFull); //8
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
        if (isFull()) return;
        if (StudentID == null) return;
        if (!selectedApplicantsID.contains(StudentID)) {
            selectedApplicantsID.add(StudentID);
            if (selectedApplicantsID.size() >= numOfSlots) {
                isFull = true;
            }
        }
    }

    public void removeSelectedApplicant(String StudentID) {
        if (StudentID == null) return;
        boolean removed = selectedApplicantsID.remove(StudentID);
        if (removed) {
            isFull = selectedApplicantsID.size() >= numOfSlots;
        }
    }
    
    public boolean isFull(){
        return isFull;
    }

    public String getInternshipID() { return internshipID; }

    public String getInternshipTitle() { return internshipTitle; }

    public String getDescription() { return description; }

    public InternshipLevel getInternshipLevel() { return internshipLevel; }

    public List<String> getPreferredMajors() { return new ArrayList<>(preferredMajors); }

    public Date getOpeningDate() { return openingDate; }

    public Date getCloseDate() { return closeDate; }

    public String getCompanyName() { return companyName; }

    public CompanyRepresentative getCompanyRepInCharge() { return companyRepInCharge; }

    public void setCompanyRepInCharge(CompanyRepresentative rep) { this.companyRepInCharge = rep; }

    public int getNumOfSlots() { return numOfSlots; }

    public List<String> getSelectedApplicantsID() { return new ArrayList<>(selectedApplicantsID); }

    public boolean isVisiblity() { return visiblity; }

    public void setVisiblity(boolean vis) { this.visiblity = vis; }
}