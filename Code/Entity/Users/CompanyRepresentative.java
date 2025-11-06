package Entity.Users;



public class CompanyRepresentative extends User {

    private String position;
    private String accountStatus = "pending";
    private String companyName;
    private String department;

    public CompanyRepresentative(String userID, String name, String email, String position, String companyName, String department) {
        super(userID, name, email);
        this.position = position;
        this.companyName = companyName;
        this.department = department;
    }


    public void setStatusToAuthorized() {
        accountStatus = "accepted";
    }

    public void setStatusToRejected() {
        accountStatus = "rejected";
    }

    public String getStatus() {
        return accountStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }
}
