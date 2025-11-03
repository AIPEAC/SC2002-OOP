package Entity.Users;

public class CareerStaff extends User {
    private String department;
    private String role;

    public CareerStaff(String userID, String name, String email, String department, String role) {
        super(userID, name, email);
        // Additional fields for CareerStaff
    }

    public CareerStaff(String userID, String passwordHash, String name, String parameter, String department, String role) {
        super(userID, name, parameter); // Assuming parameter is email
        //
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }
}

