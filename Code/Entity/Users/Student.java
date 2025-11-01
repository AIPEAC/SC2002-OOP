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
        //
    }

    public void setAcceptanceStatusToFalse() {
        //
    }

    public void getMajor() {
        //
    }

    public void getYear() {
        //
    }

}
