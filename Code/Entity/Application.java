package Entity;

import java.util.List;

/**
 * Represents a student's application for an internship opportunity.
 * An application tracks the student's request, the company's response (approve/reject),
 * the student's acceptance decision, and any withdrawal requests.
 * 
 * @author Allen
 * @version 1.0
 */
public class Application {
    /** Unique identifier for this application */
    private int applicationNumber;
    
    /** The ID of the internship opportunity being applied to */
    private String internshipID;
    
    /** The name of the company offering the internship */
    private String company;
    
    /** The ID of the student who submitted this application */
    private String studentID;
    
    /** List of the student's majors at the time of application */
    private List<String> studentMajors = null;
    
    /** Application status: "pending", "approved", or "rejected" */
    private String status = "pending";
    
    /** Student's acceptance decision: "yes", "no", or null if not yet decided */
    private String acceptance = null;
    
    /** Withdrawal request status: "pending", "approved", "rejected", or null */
    private String withdrawStatus = null;
    
    /**
     * Constructs a new Application with default status "pending".
     * 
     * @param applicationNumber unique identifier for this application
     * @param internshipID the ID of the internship being applied to
     * @param company the company offering the internship
     * @param studentID the ID of the applying student
     * @param studentMajors list of the student's majors
     */
    public Application(int applicationNumber, String internshipID, String company, String studentID, List<String> studentMajors) {
        this.applicationNumber = applicationNumber;
        this.internshipID = internshipID;
        this.company = company;
        this.studentID = studentID;
        this.studentMajors = studentMajors;
    }

    /**
     * Constructs an Application with all fields specified (used when loading from database).
     * 
     * @param applicationNumber unique identifier for this application
     * @param internshipID the ID of the internship being applied to
     * @param company the company offering the internship
     * @param studentID the ID of the applying student
     * @param status the application status ("pending", "approved", or "rejected")
     * @param acceptance student's acceptance decision ("yes", "no", or null)
     * @param withdrawStatus withdrawal request status ("pending", "approved", "rejected", or null)
     * @param studentMajors list of the student's majors
     */
    public Application(int applicationNumber, String internshipID, 
        String company, String studentID, 
        String status, String acceptance, 
        String withdrawStatus, List<String> studentMajors) {

        this.applicationNumber = applicationNumber;
        this.studentID = studentID;
        this.studentMajors = studentMajors;
        this.internshipID = internshipID;
        this.company = company;
        this.status = status;
        this.acceptance = acceptance;
        this.withdrawStatus = withdrawStatus;
        
    }

    @Override
    public String toString() {
        return "Application Number: " + applicationNumber +
               ", Internship ID: " + internshipID +
               ", Status: " + status +
               ", Company: " + company +
               ", Acceptance: " + (acceptance != null ? acceptance : "N/A") +
               ", Withdraw Status: " + (withdrawStatus != null ? withdrawStatus : "N/A");
    }

    /**
     * Gets the student ID who made this application.
     * 
     * @return The student ID
     */
    public String getStudentID() {
        return studentID;
    }
    
    /**
     * Gets the majors of the student who made this application.
     * 
     * @return List of student majors
     */
    public List<String> getStudentMajors() {
        return studentMajors;
    }

    /**
     * Gets the internship opportunity ID for this application.
     * 
     * @return The internship ID
     */
    public String getInternshipID() {
        return internshipID;
    }
    
    /**
     * Gets the company name for this application.
     * 
     * @return The company name
     */
    public String getCompany() {
        return company;
    }

    /**
     * Gets the application status (pending, approved, rejected).
     * 
     * @return The application status
     */
    public String getApplicationStatus() {
        return status;
    }

    /**
     * Gets the withdrawal status of the application.
     * 
     * @return The withdrawal status (null, pending, approved, rejected)
     */
    public String getWithdrawStatus() {
        return withdrawStatus;
    }
    
    /**
     * Gets whether the student has accepted or rejected the approved application.
     * 
     * @return "yes" if accepted, "no" if rejected, null if not yet decided
     */
    public String getAcceptance() {
        return acceptance;
    }

    /**
     * Gets the unique application number.
     * 
     * @return The application number
     */
    public int getApplicationNumber() {
        return applicationNumber;
    }
    
    /**
     * Approves the application by setting status to "approved".
     */
    public void setApplicationStatusSuccess() {
        status = "approved";
    }

    /**
     * Rejects the application by setting status to "rejected".
     */
    public void setApplicationStatusFail() {
        status = "rejected";
    }
    
    /**
     * Marks that the student has accepted the approved application offer.
     */
    public void setAcceptanceYes() {
        acceptance = "yes";
    }
    
    /**
     * Marks that the student has rejected the approved application offer.
     */
    public void setAcceptanceNo() {
        acceptance = "no";
    }
    
    /**
     * Sets the withdrawal status to "pending" when student requests withdrawal.
     */
    public void setApplicationWithdrawRequested() {
        withdrawStatus = "pending";
    }
    
    /**
     * Approves the withdrawal request by setting withdrawal status to "approved".
     */
    public void setApplicationWithdrawn() {
        withdrawStatus = "approved";
    }

    /**
     * Rejects the withdrawal request by setting withdrawal status to "rejected".
     */
    public void setApplicationWithdrawnStatus() {
        withdrawStatus = "rejected";
    }
}