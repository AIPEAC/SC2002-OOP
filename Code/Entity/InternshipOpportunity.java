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
    
    @SuppressWarnings("unused")
    private String companyRepInChargeID;

    private int numOfSlots = 1;
    private List<Integer> applicationNumberList= new ArrayList<Integer>();
    private List<Integer> acceptedApplicationNumbers = new ArrayList<Integer>();
    private boolean visiblity=true;

    
    public InternshipOpportunity(String internshipID, 
        String internshipTitle, 
        String description, 
        String internshipLevel, 
        List<String> preferredMajors, 
        Date openingDate, 
        Date closeDate, 
        String status,
        String companyName, 
        String companyRepInChargeID, 
        int numOfSlots,
        List<Integer> applicationNumberList,
        List<Integer> acceptedApplicationNumbers,
        boolean visiblity) {
        
        this.internshipID = internshipID;
        this.internshipTitle = internshipTitle;
        this.description = description;
        this.preferredMajors = preferredMajors;
        this.internshipLevel = internshipLevel;
        this.openingDate = openingDate;
        this.closeDate = closeDate;
        this.status = status;
        this.companyName = companyName;
        this.companyRepInChargeID = companyRepInChargeID;
        this.numOfSlots = numOfSlots;
        this.applicationNumberList = applicationNumberList;
        this.acceptedApplicationNumbers = acceptedApplicationNumbers;
        this.visiblity = visiblity;
    }
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
        
        this.internshipID = internshipID;
        this.internshipTitle = internshipTitle;
        this.description = description;
        this.preferredMajors = preferredMajors;
        this.internshipLevel = internshipLevel;
        this.openingDate = openingDate;
        this.closeDate = closeDate;
        this.companyName = companyName;
        this.companyRepInChargeID = companyRepInChargeID;
        this.numOfSlots = numOfSlots;
    }
    


    public String getStatus() {
        return status;
    }

    public List<Integer> getApplications() {
        //implement
        return applicationNumberList;
    }
    public String getInternshipID() {
        return internshipID;
    }
    public String getInternshipTitle() {
        return internshipTitle;
    }
    public String getDescription() {
        return description;
    }
    public String getInternshipLevel() {
        return internshipLevel;
    }
    public List<String> getPreferredMajors() {
        return preferredMajors;
    }
    public Date getOpeningDate() {
        return openingDate;
    }
    public Date getCloseDate() {
        return closeDate;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getCompanyRepInChargeID() {
        return companyRepInChargeID;
    }
    public int getNumOfSlots() {
        return numOfSlots;
    }
    public List<Integer> getApplicationNumberList() {
        return applicationNumberList;
    }
    public List<Integer> getAcceptedApplicationNumbers() {
        return acceptedApplicationNumbers;
    }
    public boolean getVisibility() {
        return visiblity;
    }
    
    public List<Object> getDetailsForViewing() {
        List<Object> details=new ArrayList<Object>();
        details.add(internshipID); //0
        details.add(internshipTitle); //1
        details.add(description); //2
        details.add(internshipLevel); //3
        details.add(preferredMajors); //4
        details.add(openingDate); //5
        details.add(closeDate); //6
        details.add(status); //7
        details.add(companyName); //8
        details.add(numOfSlots); //9
        details.add(visiblity); //10
        details.add(isFull()); //11
        return details;
    }

    public List<Object> getDetailsForReport() {
        List<Object> details=new ArrayList<Object>();
        details.add(internshipID); //0
        details.add(internshipLevel); //1
        details.add(preferredMajors); //2
        details.add(openingDate); //3
        details.add(closeDate); //4
        details.add(companyName); //5
        details.add(numOfSlots); //6
        details.add(visiblity); //7
        details.add(isFull()); //8
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

    public void addSelectedApplication(int applicationID) {
        if (isFull()){
            return;
        }
        acceptedApplicationNumbers.add(applicationID);
    }

    public void removeSelectedApplication(int applicationID) {
        acceptedApplicationNumbers.remove(applicationID);
    }
    

    
    public boolean isFull(){
        return acceptedApplicationNumbers.size() == numOfSlots;
    } 
}