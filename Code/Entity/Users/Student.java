package Entity.Users;

import java.util.List;

public class Student extends User {
    private List<String> majors;
    private int year;
    private boolean hasAcceptedInternshipOpportunity = false;

    public Student(String userID, String name, String email, List<String> majors, int year, boolean hasAcceptedInternshipOpportunity) {
        super(userID, name, email);
        // Additional fields for Student
        this.majors = majors;
        this.year = year;
        this.hasAcceptedInternshipOpportunity = hasAcceptedInternshipOpportunity;
    }

    public void setAcceptanceStatusToTrue() {
        hasAcceptedInternshipOpportunity = true;
    }

    public void setAcceptanceStatusToFalse() {
        hasAcceptedInternshipOpportunity = false;
    }

    public boolean getAcceptanceStatus() {
        return hasAcceptedInternshipOpportunity;
    }

    public List<String> getMajors() {
        return majors;
    }

    public int getYear() {
        return year;
    }

}
