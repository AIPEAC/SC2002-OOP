package Entity;


import Entity.Users.Student;

public class Application {

    private int applicationNumber;
    private String internshipID;
    private String studentID;
    private String status = "pending";
    private String withdrawStatus = null;

    public Application(int applicationNumber, Student student, InternshipOpportunity internshipOpportunity) {
        this.applicationNumber = applicationNumber;
        this.studentID = student.getUserID();
        this.internshipID = (String) internshipOpportunity.getDetails().get(0);
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

    public void setApplicationWithdrawn() {
        withdrawStatus = "approved";
    }

    public void setApplicationWithdrawnStatus() {
        withdrawStatus = "rejected";
    }
}