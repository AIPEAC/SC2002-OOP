package Entity.Users;



public class CompanyRepresentative extends User {
    @SuppressWarnings("unused")
    private String position;
    private String accountStatus = "pending";
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
    /* 
    public String getPosition() {
        return position;
    }
    */
}
