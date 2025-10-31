package Entity.Users;

public class Student extends User {
    public Student(String userID, String name, String email, String major, int year, boolean hasAcceptedInternshipOpportunity) {
        super(userID, name, email);
        // Additional fields for Student
    }

}
