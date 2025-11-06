package Entity;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;


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
    private String companyRepInChargeID;
    private int numOfSlots = 1;
    private List<Application> applicationList= new ArrayList<Application>();
    private List<String> selectedApplicantsID = new ArrayList<String>();
    private boolean isFull=false;
    private boolean visiblity=true;



    public InternshipOpportunity(String internshipID, 
        String internshipTitle, 
        String description, 
        List<String> preferredMajors, 
        String internshipLevel, 
        Date openingDate, 
        Date closeDate, 
        String companyName, 
        String companyRepInChargeID, 
        int numOfSlots) {
        //implement
        this.internshipID = internshipID;
        this.internshipTitle = internshipTitle;
        this.description = description;
        this.preferredMajors = preferredMajors;
        this.internshipLevel = internshipLevel;
        this.openingDate = openingDate;
        this.closeDate = closeDate;
        this.companyName = companyName;
        this.numOfSlots = numOfSlots;
    }

    public String getStatus() {
        return status;
    }

    public List<Application> getApplications() {
        //implement
        return applicationList;
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
        if (isFull()){
            return;
        }else if(selectedApplicantsID.size() + 1 >= numOfSlots){
            isFull = true;
        }
        selectedApplicantsID.add(StudentID);
    }

    public void removeSelectedApplicant(String StudentID) {
        selectedApplicantsID.remove(StudentID);
        isFull = false;
    }
    

    
    public boolean isFull(){
        return isFull;
    }
    
}