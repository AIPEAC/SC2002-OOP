package Entity.Users;

public class CareerStaff extends User {
    private String department;
    @SuppressWarnings("unused")
    private String role;

    public CareerStaff(String userID, String name, String email, String department, String role) {
        super(userID, name, email);
        // Additional fields for CareerStaff
        this.department = department;
        this.role = role;
    }


    public String getDepartment() {
        return department;
    }
}

