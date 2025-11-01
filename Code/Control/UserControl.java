package Control;

import java.util.List;
import Entity.Users.CompanyRepresentative;

public class UserControl {
	private UserLoginDirectoryControl userDir;
	private AuthenticationControl authCtrl;
	private List<String> pendingCompanyRepID;

	public UserControl(UserLoginDirectoryControl userDir, AuthenticationControl authCtrl) {
		// implementation
	}

	public void loadUserDataFromDB() {
		// implementation
	}

	public List<CompanyRepresentative> getPendingCompanyRepList() {
		// implementation
		return null;
	}

	public void approveRegister(String companyID) {
		// implementation
	}

	public void rejectRegister(String companyID) {
		// implementation
	}

	public void addCompanyRepFromPending(CompanyRepresentative rep) {
		// implementation
	}

	public void removeCompanyRepFromPending(CompanyRepresentative rep) {
		// implementation
	}

	public List<CompanyRepresentative> getPendingCompanyRep() {
		// implementation
		return null;
	}
}