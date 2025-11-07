package Entity;


public class Application {
    private int applicationNumber;
    private String internshipID;
    private String studentID;
    private String status = "pending";
    private String withdrawStatus = null;

    public Application(int applicationNumber, String internshipID, String studentID) {
        this.applicationNumber = applicationNumber;
        this.internshipID = internshipID;
        this.studentID = studentID;
    }

    public Application(int applicationNumber, String internshipID, String studentID, String status, String withdrawStatus) {
        this.applicationNumber = applicationNumber;
        this.studentID = studentID;
        this.internshipID = internshipID;
        this.status = status;
        this.withdrawStatus = withdrawStatus;
    }

    @Override
    public String toString() {
        return "Application Number: " + applicationNumber +
               ", Internship ID: " + internshipID +
               ", Status: " + status +
               ", Withdraw Status: " + (withdrawStatus != null ? withdrawStatus : "N/A");
    }

    public String getStudentID() {
        return studentID;
    }

    public String getInternshipID() {
        return internshipID;
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