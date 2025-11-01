package Entity;

import Entity.Users.Student;

public class Application {

    private int applicationNumber;
    private String internshipID;
    private String studentID;
    private String status = "pending";
    private String withdrawStatus = null;

    public Application(int applicationNumber, Student student, InternshipOpportunity internshipOpportunity) {
        //
    }

    public String getStudentID() {
        //
        return null;
    }

    public String getInternshipID() {
        //
        return null;
    }

    public String getApplicationStatus() {
        //
        return null;
    }

    public String getWithdrawStatus() {
        //
        return null;
    }

    public void setApplicationStatusSuccess() {
        //
    }

    public void setApplicationStatusFail() {
        //
    }

    public void setApplicationWithdrawn() {
        //
    }

    public void setApplicationWithdrawnStatus() {
        //
    }
}