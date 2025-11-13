package Entity;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents an internship opportunity in the system.
 * An internship opportunity is created by a company representative and must be approved
 * by career center staff before students can view and apply to it.
 * Company representatives can create up to 5 internship opportunities.
 * Each opportunity can have a maximum of 10 slots.
 * 
 * @author Allen
 * @version 1.0
 */
public class InternshipOpportunity {
    /** Unique identifier for this internship opportunity */
    private String internshipID;
    
    /** Title of the internship position */
    private String internshipTitle;
    
    /** Detailed description of the internship */
    private String description;
    
    /** Level of the internship: "Basic", "Intermediate", or "Advanced" */
    private String internshipLevel="Basic";
    
    /** List of preferred majors for applicants (e.g., CSC, EEE, MAE) */
    private List<String> preferredMajors;
    
    /** Date when applications open */
    private Date openingDate;
    
    /** Date when applications close */
    private Date closeDate;
    
    /** Status of the opportunity: "pending", "approved", "rejected", or "filled" */
    private String status = "pending";
    
    /** Name of the company offering this internship */
    private String companyName;
    
    /** ID of the company representative who created this opportunity */
    private String companyRepInChargeID;
    
    /** Number of available slots (maximum 10) */
    private int numOfSlots = 1;
    
    /** List of application numbers that are currently pending */
    private List<Integer> applicationNumberList= new ArrayList<Integer>();
    
    /** List of application numbers where students have accepted the offer */
    private List<Integer> acceptedApplicationNumbers = new ArrayList<Integer>();
    
    /** Whether this opportunity is visible to students */
    private boolean visibility=true;

    /**
     * Constructs a complete InternshipOpportunity with all fields.
     * 
     * @param internshipID Unique identifier for the internship
     * @param internshipTitle Title of the internship position
     * @param description Detailed description of the internship
     * @param internshipLevel Level of the internship (e.g., "Undergraduate", "Graduate")
     * @param preferredMajors List of preferred student majors
     * @param openingDate Date when applications open
     * @param closeDate Date when applications close
     * @param status Status of the internship (pending, approved, rejected)
     * @param companyName Name of the company offering the internship
     * @param companyRepInChargeID ID of the company representative managing this internship
     * @param numOfSlots Maximum number of students that can be accepted
     * @param applicationNumberList List of pending application numbers
     * @param acceptedApplicationNumbers List of accepted application numbers
     * @param visibility Whether this opportunity is visible to students
     */
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
        boolean visibility) {
        
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
        this.visibility = visibility;
    }
    
    /**
     * Constructs a new InternshipOpportunity for creation (initializes empty application lists).
     * 
     * @param internshipID Unique identifier for the internship
     * @param internshipTitle Title of the internship position
     * @param description Detailed description of the internship
     * @param preferredMajors List of preferred student majors
     * @param internshipLevel Level of the internship
     * @param openingDate Date when applications open
     * @param closeDate Date when applications close
     * @param companyName Name of the company offering the internship
     * @param companyRepInChargeID ID of the company representative managing this internship
     * @param numOfSlots Maximum number of students that can be accepted
     */
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
    


    /**
     * Gets the status of the internship.
     * 
     * @return The status of the internship
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the list of pending application numbers for this internship.
     * 
     * @return List of application numbers
     */
    public List<Integer> getApplications() {
        //implement
        return applicationNumberList;
    }
    
    /**
     * Gets the unique internship ID.
     * 
     * @return The internship ID
     */
    public String getInternshipID() {
        return internshipID;
    }
    
    /**
     * Gets the internship title.
     * 
     * @return The internship title
     */
    public String getInternshipTitle() {
        return internshipTitle;
    }
    
    /**
     * Gets the internship description.
     * 
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the internship level (e.g., "Undergraduate", "Graduate").
     * 
     * @return The internship level
     */
    public String getInternshipLevel() {
        return internshipLevel;
    }
    
    /**
     * Gets the list of preferred majors for this internship.
     * 
     * @return List of preferred major names
     */
    public List<String> getPreferredMajors() {
        return preferredMajors;
    }
    
    /**
     * Gets the opening date for applications.
     * 
     * @return The opening date
     */
    public Date getOpeningDate() {
        return openingDate;
    }
    
    /**
     * Gets the closing date for applications.
     * 
     * @return The closing date
     */
    public Date getCloseDate() {
        return closeDate;
    }
    
    /**
     * Gets the company name offering this internship.
     * 
     * @return The company name
     */
    public String getCompanyName() {
        return companyName;
    }
    
    /**
     * Gets the ID of the company representative in charge.
     * 
     * @return The company representative ID
     */
    public String getCompanyRepInChargeID() {
        return companyRepInChargeID;
    }
    
    /**
     * Gets the maximum number of slots available.
     * 
     * @return The number of slots
     */
    public int getNumOfSlots() {
        return numOfSlots;
    }
    
    /**
     * Gets the list of pending application numbers.
     * 
     * @return List of pending application numbers
     */
    public List<Integer> getApplicationNumberList() {
        return applicationNumberList;
    }
    
    /**
     * Gets the list of accepted application numbers.
     * 
     * @return List of accepted application numbers
     */
    public List<Integer> getAcceptedApplicationNumbers() {
        return acceptedApplicationNumbers;
    }
    /**
     * Gets the visibility of the internship.
     * 
     * @return True if the internship is visible to students, false otherwise
     */
    public boolean getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility of the internship.
     * 
     * @param visibility True to make the internship visible to students, false otherwise
     */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    

    /**
     * Gets the details of the internship for report generation.
     * 
     * @return A list of objects containing internship details
     */
    public List<Object> getDetailsForReport() {
        List<Object> details=new ArrayList<Object>();
        details.add(internshipID); //0
        details.add(internshipLevel); //1
        details.add(preferredMajors); //2
        details.add(openingDate); //3
        details.add(closeDate); //4
        details.add(companyName); //5
        details.add(numOfSlots); //6
        details.add(visibility); //7
        details.add(isFull()); //8
        return details;
    }
    
    /**
     * Sets the status of the internship to approved.
     */
    public void setStatusToApproved() {
        status = "approved";
    }

    /**
     * Sets the status of the internship to rejected.
     */
    public void setStatusToRejected() {
        status = "rejected";
    }

    /**
     * Adds a new application number to the pending list.
     * 
     * @param applicationNumber The application number to add
     */
    public void addApplicationNumberToInternship(int applicationNumber) {
        applicationNumberList.add(applicationNumber);
    }
    /**
     * Removes an application number from the internship.
     * 
     * @param applicationNumber The application number to remove
     */
    public void removeApplicationNumberFromInternship(int applicationNumber) {
        applicationNumberList.remove(Integer.valueOf(applicationNumber));
        // Also remove from accepted list if present (for withdrawal scenarios)
        acceptedApplicationNumbers.remove(Integer.valueOf(applicationNumber));
    }

    /**
     * Approves an application by removing it from the pending list.
     * Note: Does not add to accepted list yet - that happens when student accepts the offer.
     * 
     * @param applicationNumber The application number to approve
     */
    public void approveApplicationNumber(int applicationNumber) {
        // Remove from pending list only, don't add to accepted list yet
        // Student will be added to acceptedApplicationNumbers when they accept the offer
        removeApplicationNumberFromInternship(applicationNumber);
    }
    
    /**
     * Adds an application to the accepted list when student accepts the offer.
     * This implements the deferred acceptance workflow.
     * 
     * @param applicationNumber The application number to mark as accepted
     */
    public void studentAcceptedOffer(int applicationNumber) {
        // Called when student accepts an approved offer
        acceptedApplicationNumbers.add(applicationNumber);
    }

    /**
     * Rejects an application by removing it from the pending list.
     * 
     * @param applicationNumber The application number to reject
     */
    public void rejectApplicationNumber(int applicationNumber) {
        removeApplicationNumberFromInternship(applicationNumber);
    }

    /**
     * Adds a selected application to the accepted list (if not full).
     * 
     * @param applicationID The application ID to add
     */
    public void addSelectedApplication(int applicationID) {
        if (isFull()){
            return;
        }
        acceptedApplicationNumbers.add(applicationID);
    }

    /**
     * Removes a selected application from the accepted list.
     * 
     * @param applicationID The application ID to remove
     */
    public void removeSelectedApplication(int applicationID) {
        acceptedApplicationNumbers.remove(applicationID);
    }
    
    
    /**
     * Checks if the internship is full.
     * 
     * @return True if the number of accepted applications equals the number of slots, false otherwise
     */
    public boolean isFull(){
        return acceptedApplicationNumbers.size() == numOfSlots;
    } 
}



