package Entity.Users;

public class Student extends User {
    private String major;
    private int year;
    private boolean hasAcceptedInternshipOpportunity = false;

    public Student(String userID, String name, String email, String major, int year, boolean hasAcceptedInternshipOpportunity) {
        super(userID, name, email);
        // Additional fields for Student
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

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

}
