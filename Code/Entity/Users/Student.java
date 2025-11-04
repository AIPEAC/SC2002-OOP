package Entity.Users;

public class Student extends User {
    private String major;
    private int year;
    private boolean hasAcceptedInternshipOpportunity = false;

    public Student(String userID, String name, String email, String major, int year) {
        super(userID, name, email, Role.STUDENT);
        // Additional fields for Student
        this.major = major;
        this.year = year;
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
