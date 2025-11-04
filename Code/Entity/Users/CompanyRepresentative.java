package Entity.Users;

import Entity.Enums.Status;

public class CompanyRepresentative extends User {

    private String position;
    private Status accountStatus = Status.PENDING;
    private String companyName;
    private String department;

    public CompanyRepresentative(String userID, String name, String email, String position, String companyName, String department) {
        super(userID, name, email, Role.COMPANY_REP);
        this.position = position;
        this.companyName = companyName;
        this.department = department;
    }


    public void setStatusToAuthorized() {
        accountStatus = Status.APPROVED;
    }

    public void setStatusToRejected() {
        accountStatus = Status.REJECTED;
    }

    public Status getStatus() {
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
