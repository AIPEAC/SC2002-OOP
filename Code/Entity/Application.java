package Entity;


public class Application {
    private int applicationNumber;
    private String internshipID;
    private String company;
    private String studentID;
    private String status = "pending";
    private String acceptance = null;
    private String withdrawStatus = null;

    public Application(int applicationNumber, String internshipID, String company, String studentID) {
        this.applicationNumber = applicationNumber;
        this.internshipID = internshipID;
        this.company = company;
        this.studentID = studentID;
    }

    public Application(int applicationNumber, String internshipID, String company, String studentID, String status, String acceptance, String withdrawStatus) {
        this.applicationNumber = applicationNumber;
        this.studentID = studentID;
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

    public String getStudentID() {
        return studentID;
    }

    public String getInternshipID() {
        return internshipID;
    }
    public String getCompany() {
        return company;
    }

    public String getApplicationStatus() {
        return status;
    }

    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public int getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationStatusSuccess() {
        status = "approved";
    }

    public void setApplicationStatusFail() {
        status = "rejected";
    }
    public void setAcceptanceYes() {
        acceptance = "yes";
    }
    public void setAcceptanceNo() {
        acceptance = "no";
    }
    public void setApplicationWithdrawRequested() {
        withdrawStatus = "pending";
    }
    public void setApplicationWithdrawn() {
        withdrawStatus = "approved";
    }

    public void setApplicationWithdrawnStatus() {
        withdrawStatus = "rejected";
    }
}