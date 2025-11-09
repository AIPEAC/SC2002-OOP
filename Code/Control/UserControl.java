package Control;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Entity.Users.CompanyRepresentative;

public class UserControl {
	private UserLoginDirectoryControl userDir;
	private AuthenticationControl authCtrl;
	private List<String> pendingCompanyRepID = new ArrayList<>();

	public UserControl(UserLoginDirectoryControl userDir, AuthenticationControl authCtrl) {
		this.userDir = userDir;
		this.authCtrl = authCtrl;
	}

	void loadUserDataFromDB() {
		// implementation
	}

	/** Return formatted lines describing pending company representative registrations. */
	public List<String> getPendingCompanyRepList() {
		List<String> out = new ArrayList<>();
		String csvFile = "Code/Lib/company_representative.csv";
		File file = new File(csvFile);
		if (!file.exists()) return out;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 5) {
					String userID = parts[0];
					String name = parts.length > 1 ? parts[1] : "";
					//String email = parts.length > 2 ? parts[2] : "";
					//String position = parts.length > 3 ? parts[3] : "";
					String status = parts.length > 4 ? parts[4] : "";
					String companyName = parts.length > 5 ? parts[5] : "";
					String department = parts.length > 6 ? parts[6] : "";
					if ("pending".equalsIgnoreCase(status)) {
						out.add("ID: " + userID + " | Name: " + name + " | Company: " + companyName + " | Dept: " + department);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}

	public void approveRegister(String companyID) {
		if (authCtrl.isLoggedIn() && authCtrl.getUserIdentity().equals("CareerStaff")) {
			userDir.approveCompanyRep(companyID);
		} else if (!authCtrl.isLoggedIn()) {
			throw new IllegalStateException("You are not logged in.");
		} else {
			throw new IllegalStateException("You do not have the permission to approve registrations.");
		}
	}

	public void rejectRegister(String companyID) {
		if (authCtrl.isLoggedIn() && authCtrl.getUserIdentity().equals("CareerStaff")) {
			userDir.rejectCompanyRep(companyID);
		} else if (!authCtrl.isLoggedIn()) {
			throw new IllegalStateException("You are not logged in.");
		} else {
			throw new IllegalStateException("You do not have the permission to reject registrations.");
		}
	}

	void addCompanyRepFromPending(CompanyRepresentative rep) {
		pendingCompanyRepID.add(rep.getUserID());
	}

	void removeCompanyRepFromPending(CompanyRepresentative rep) {
		pendingCompanyRepID.remove(rep.getUserID());
	}

	List<String> getPendingCompanyRep() {
		return getPendingCompanyRepList();
	}
}