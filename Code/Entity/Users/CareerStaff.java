package Entity.Users;

public class CareerStaff extends User {
    private String department;

    public CareerStaff(String userID, String name, String email, String department) {
        super(userID, name, email);
        // Additional fields for CareerStaff
        this.department = department;
    }


    public String getDepartment() {
        return department;
    }
}

