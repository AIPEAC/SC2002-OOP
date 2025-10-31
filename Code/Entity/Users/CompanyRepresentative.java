package Entity.Users;

public class CompanyRepresentative extends User {
    //companys login should contain the logic of ask for login or register.
    public CompanyRepresentative(String userID, String name, String email, String position, String accountStatus, String companyName, String department) {
        super(userID, name, email);
        // Additional fields for CompanyRepresentative
    }
}
