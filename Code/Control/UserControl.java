package Control;

import java.util.List;
import Entity.Users.CompanyRepresentative;

public class UserControl {
	private UserLoginDirectoryControl userDir;
	private AuthenticationControl authCtrl;
	private List<String> pendingCompanyRepID = new java.util.ArrayList<>();

	public UserControl(UserLoginDirectoryControl userDir, AuthenticationControl authCtrl) {
		this.userDir = userDir;
		this.authCtrl = authCtrl;
	}

	public void loadUserDataFromDB() {
		// implementation
	}

	public List<CompanyRepresentative> getPendingCompanyRepList() {
		// implementation
		return null;
	}

	public void approveRegister(String companyID) {
		if (authCtrl.isLoggedIn() && authCtrl.getUserIdentity().equals("CareerStaff")) {
			userDir.approveCompanyRep(companyID);
		} else if (!authCtrl.isLoggedIn()) {
			System.out.println("You are not logged in.");
		} else {
			System.out.println("You do not have the permission to approve registrations.");
		}
	}

	public void rejectRegister(String companyID) {
		if (authCtrl.isLoggedIn() && authCtrl.getUserIdentity().equals("CareerStaff")) {
			userDir.rejectCompanyRep(companyID);
		} else if (!authCtrl.isLoggedIn()) {
			System.out.println("You are not logged in.");
		} else {
			System.out.println("You do not have the permission to reject registrations.");
		}
	}

	public void addCompanyRepFromPending(CompanyRepresentative rep) {
		pendingCompanyRepID.add(rep.getUserID());
	}

	public void removeCompanyRepFromPending(CompanyRepresentative rep) {
		pendingCompanyRepID.remove(rep.getUserID());
	}

	public List<CompanyRepresentative> getPendingCompanyRep() {
		// implementation
		return null;
	}
}