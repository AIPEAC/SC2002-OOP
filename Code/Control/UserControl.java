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
		java.util.List<CompanyRepresentative> pending = new java.util.ArrayList<>();
		String csvFile = "Code/Lib/company_representative.csv";
		java.io.File file = new java.io.File(csvFile);
		if (!file.exists()) return pending;
		try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
			String line;
			br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 5) {
					String userID = parts[0];
					String name = parts.length > 1 ? parts[1] : "";
					String email = parts.length > 2 ? parts[2] : "";
					String position = parts.length > 3 ? parts[3] : "";
					String status = parts.length > 4 ? parts[4] : "";
					String companyName = parts.length > 5 ? parts[5] : "";
					String department = parts.length > 6 ? parts[6] : "";
					if ("pending".equalsIgnoreCase(status)) {
						CompanyRepresentative rep = new CompanyRepresentative(userID, name, email, position, status, companyName, department);
						pending.add(rep);
					}
				}
			}
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return pending;
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
		return getPendingCompanyRepList();
	}
}