package Entity;

import Entity.Enums.Status;
import Entity.Users.Student;

public class Application {

    private String applicationNumber;
    private String internshipID;
    private String studentID;
    private Status status = Status.PENDING;
    private Status withdrawStatus = null;

    public Application(String applicationNumber, Student student, InternshipOpportunity internshipOpportunity) {
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

    public Status getApplicationStatus() {
        return status;
    }

    public Status getWithdrawStatus() {
        return withdrawStatus;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationStatusSuccess() {
        status = Status.APPROVED;
    }

    public void setApplicationStatusFail() {
        status = Status.REJECTED;
    }

    public void setApplicationWithdrawn() {
        withdrawStatus = Status.APPROVED;
    }

    public void setApplicationWithdrawnStatus() {
        withdrawStatus = Status.REJECTED;
    }
}