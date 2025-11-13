package Control;

import java.util.List;

import Control.Tool.ControlUtils;
import Entity.Users.CompanyRepresentative;

import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Controls user management operations performed by Career Center Staff.
 * Handles approval and rejection of company representative registrations.
 * Career staff use this controller to authorize new company representatives to access the system.
 * 
 * @author Allen
 * @version 1.0
 */
public class UserControl {
	/** User login directory for managing user data */
	private UserLoginDirectoryControl userDir;
	
	/** Authentication controller for session management */
	private AuthenticationControl authCtrl;
	
	/** List of pending company representative IDs */
	private List<String> pendingCompanyRepID = new ArrayList<>();

	/**
	 * Constructs a UserControl with required dependencies.
	 * 
	 * @param userDir the user login directory controller
	 * @param authCtrl the authentication controller
	 */
	UserControl(UserLoginDirectoryControl userDir, AuthenticationControl authCtrl) {
		this.userDir = userDir;
		this.authCtrl = authCtrl;
	}

	/**
	 * Gets a list of all pending company representative registrations.
	 * Used by career center staff to review and approve/reject new registrations.
	 * 
	 * @return formatted list of pending registrations with ID, name, company, and department
	 */
	public List<String> getPendingCompanyRepList() {
		List<String> out = new ArrayList<>();
		String csvFile = "Code/Backend/Lib/company_representative.csv";
		File file = new File(csvFile);
		if (!file.exists()) return out;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				// Use proper CSV parsing that respects quoted fields
				String[] parts = ControlUtils.splitCsvLine(line);
				if (parts.length >= 5) {
					String userID = ControlUtils.unescapeCsvField(parts[0]);
					String name = parts.length > 1 ? ControlUtils.unescapeCsvField(parts[1]) : "";
					//String email = parts.length > 2 ? ControlUtils.unescapeCsvField(parts[2]) : "";
					//String position = parts.length > 3 ? ControlUtils.unescapeCsvField(parts[3]) : "";
					String status = parts.length > 4 ? ControlUtils.unescapeCsvField(parts[4]) : "";
					String companyName = parts.length > 5 ? ControlUtils.unescapeCsvField(parts[5]) : "";
					String department = parts.length > 6 ? ControlUtils.unescapeCsvField(parts[6]) : "";
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