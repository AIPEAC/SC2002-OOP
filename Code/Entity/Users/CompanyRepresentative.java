package Entity.Users;

public class CompanyRepresentative extends User {
    private String position;
    private String accountStatus = "Pending";
    private String companyName;
    private String department = null;

    public CompanyRepresentative(String userID, String name, String email, String position, String accountStatus, String companyName, String department) {
        super(userID, name, email);
        this.position = position;
        this.accountStatus = accountStatus;
        this.companyName = companyName;
        this.department = department;
    }



    public void setStatusToAuthorized() {
        //
    }

    public void setStatusToRejected() {
        //
    }

    public String getStatus() {
        //
        return null;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }
}
