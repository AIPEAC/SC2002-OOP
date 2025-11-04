package Entity.Users;

public class CareerStaff extends User {
    private String department;
    private String role;

    public CareerStaff(String userID, String name, String email, String department, String role) {
        super(userID, name, email);
        // Additional fields for CareerStaff
    }



    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }
}

