package Entity.Users;

public class CompanyRepresentative extends User {
    private String position;
    private String accountStatus;
    private String companyName;
    private String department;

    public CompanyRepresentative(String userID, String name, String email, String position, String accountStatus, String companyName, String department) {
        super(userID, name, email);
        this.position = position;
        this.accountStatus = accountStatus;
        this.companyName = companyName;
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }
}
