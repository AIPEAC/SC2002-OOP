package Entity;

import java.util.Date;
import java.util.List;
import Entity.Users.CompanyRepresentative;
import Entity.Users.Student;

public class InternshipOpportunity {
    private String internshipID;
    private String internshipTitle;
    private String description;
    private String internshipLevel;
    private List<String> preferredMajors;
    private Date openingDate;
    private Date closeDate;
    private String status = "pending";
    private String companyName;
    private CompanyRepresentative companyRepInCharge;
    private int numOfSlots = 1;
    private List<Application> applicationList;

    public InternshipOpportunity(int internshipID, String internshipTitle, String description, List<String> preferredMajors, String internshipLevel, Date openingDate, Date closeDate, String companyName, String companyRepInCharge, int numOfSlots) {
        //
    }

    public String getStatus() {
        //
        return null;
    }

    public List<Application> getApplications() {
        //
        return null;
    }

    public List<Object> getDetails() {
        //
        return null;
    }

    public void setStatusToApproved() {
        //
    }

    public void setStatusToRejected() {
        //
    }

    public void approveApplication(Application application) {
        //
    }

    public void rejectApplication(Application application) {
        //
    }

    public void addSelectedApplicant(Student student) {
        //
    }

    public void removeSelectedApplicant(Student student) {
        //
    }
}