package Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Entity.Application;

public class ApplicationControl {
	private List<Application> applications = new ArrayList<Application>();
	private AuthenticationControl authCtrl;
	private InternshipControl intCtrl;

	// =========================================================
	// Constructor and Initializer

	public ApplicationControl(AuthenticationControl authCtrl, InternshipControl intCtrl) {
		this.authCtrl = authCtrl;
		this.intCtrl = intCtrl;
	}

	//=========================================================
	// Both Student and Career Staff methods

	void checkApplications() {
		// Deprecated: prefer getApplicationsForDisplay() for boundary printing
		// kept as a no-op to avoid printing from control layer
	}

	/** Return application lines for display by the boundary. */
	public List<String> getApplicationsForDisplay() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			out.add(app.toString());
		}
		return out;
	}


	//=========================================================
	// Student methods

	public void loadStudentApplicationFromDB() {
		String studentID = authCtrl.getUserID();
		// Load applications from the database for the given studentID
		final String CSV_FILE = "Database/Applications.csv";
		File file = new File(CSV_FILE);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			// Skip header
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length >= 5 && values[2].equals(studentID)) {
					int applicationNumber = Integer.parseInt(values[0]);
					String internshipID = values[1];
					String company = values.length > 3 ? values[3] : null;
					String status = values.length > 4 ? values[4] : "pending";
					String acceptance = (values.length > 5 && !values[5].isEmpty()) ? values[5] : null;
					String withdrawStatus = (values.length > 6 && !values[6].isEmpty()) ? values[6] : null;
					List<String> studentMajors = (values.length > 7 && !values[7].isEmpty()) ? Arrays.asList(values[7].split(" ")) : null;
					Application app = new Application(
						applicationNumber,
						internshipID,
						company,
						studentID,
						status,
						acceptance,
						withdrawStatus,
						studentMajors
					);
					applications.add(app);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int makeApplication(String internshipID) {
		// Create a new application for the student
		if (!authCtrl.isLoggedIn()) throw new IllegalStateException("User not logged in.");
		if (!"Student".equals(authCtrl.getUserIdentity())) throw new IllegalStateException("Only students can make applications.");
		if (internshipID == null || internshipID.isEmpty()) throw new IllegalArgumentException("Invalid Internship ID.");
		if (!intCtrl.isVisibleAndNotFullAndNotRejected(internshipID)) {
			throw new IllegalStateException("Internship is either not visible or already full or is rejected by staff.");
		}
		if (!intCtrl.studentFitsRequirements(authCtrl.getUserID(), internshipID)) {
			throw new IllegalStateException("You do not meet the requirements for this internship.");
		}
		if (applications.stream().anyMatch(app -> app.getInternshipID().equals(internshipID))) {
			throw new IllegalStateException("You have already applied for this internship.");
		}
		if (applications.stream().anyMatch(app -> app.getApplicationStatus().equals("approved"))) {
			throw new IllegalStateException("You have an approved application.");
		}
		String companyName = intCtrl.getInternshipCompany(internshipID);
		List<String> studentMajors = intCtrl.getStudentMajors();
		Application app = new Application(applications.size() + 1, internshipID, companyName, authCtrl.getUserID(), studentMajors);
		applications.add(app);
		saveApplicationsToDB();
		updateInternshipsApplicationsInDB(app.getApplicationNumber(), internshipID, "add");
		return app.getApplicationNumber();
	}
	void getApplicationStatus() {
		// Deprecated: use getApplicationsForDisplay() instead
		return;
	}
	public boolean hasAcceptedOffer() {
		for (Application app : applications) {
			if (app.getAcceptance() != null && app.getAcceptance().equals("yes")) {
				return true;
			}
		}
		return false;
	}
	public boolean hasApprovedApplication() {
		for (Application app : applications) {
			if (app.getApplicationStatus().equals("approved")) {
				return true;
			}
		}
		return false;
	}
	public List<String> getApprovedApplicationInternshipCompaniesAndIDs() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			if (app.getApplicationStatus().equals("approved")) {
				out.add(app.getCompany() + " (ID: " + app.getInternshipID() + ")");
			}
		}
		return out;
	}
	public void acceptOffer(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null || !app.getApplicationStatus().equals("approved")) {
			throw new IllegalArgumentException("Application not found or not approved.");
		}
		app.setAcceptanceYes();
		withdrawOtherApplicationsOfApprovedStudent(app.getStudentID());
	}
	public void rejectOffer(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null || !app.getApplicationStatus().equals("approved")) {
			throw new IllegalArgumentException("Application not found or not approved.");
		}
		app.setAcceptanceNo();
	}
	public void requestWithdrawApplication(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found.");
		String ws = app.getWithdrawStatus();
		if (ws != null && ws.equals("rejected")) {
			throw new IllegalStateException("Previous withdrawal request was rejected. You are refrained from making changes.");
		}
		app.setApplicationWithdrawRequested();
		saveApplicationsToDB();
	}
	public List<String> viewInternshipsAppliedTo() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			String internID = app.getInternshipID();
			List<String> details = intCtrl.getInternshipDetails(internID);
			out.add("Application Number: " + app.getApplicationNumber());
			out.addAll(details);
		}
		return out;
	}

	/** Return applications which have a pending withdrawal request */
	public List<String> getPendingWithdrawals() {
		List<String> pending = new ArrayList<>();
		for (Application app : applications) {
			if (app.getWithdrawStatus() != null && app.getWithdrawStatus().equals("pending")) {
				pending.add("Application No: " + app.getApplicationNumber() + " | Internship ID: " + app.getInternshipID() + " | Student: " + app.getStudentID());
			}
		}
		return pending;
	}

	void rejectWithdrawalByNumber(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found: " + appNum);
		app.setApplicationWithdrawnStatus(); // mark as rejected
		saveApplicationsToDB();
	}

	/** UI-friendly wrapper: accepts application number as String, parses and delegates */
	public void approveWithdrawal(String appNumStr) {
		if (appNumStr == null || appNumStr.isEmpty()) {
			throw new IllegalArgumentException("Invalid application number.");
		}
		try {
			int appNum = Integer.parseInt(appNumStr.trim());
			approveWithdrawal(appNum);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid application number format: " + appNumStr);
		}
	}

	/** UI-friendly wrapper: accepts application number as String, parses and delegates */
	public void rejectWithdrawal(String appNumStr) {
		if (appNumStr == null || appNumStr.isEmpty()) {
			throw new IllegalArgumentException("Invalid application number.");
		}
		try {
			int appNum = Integer.parseInt(appNumStr.trim());
			rejectWithdrawalByNumber(appNum);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid application number format: " + appNumStr);
		}
	}

	/**
	 * Wrapper that accepts an application number string and a decision string (approve/reject-like)
	 */
	void handleWithdrawalDecision(String appNumStr, String decisionStr) {
		if (appNumStr == null || appNumStr.isEmpty()) {
			throw new IllegalArgumentException("Invalid application number.");
		}
		Boolean decision = ControlUtils.parseBooleanLike(decisionStr);
		if (decision == null) {
			throw new IllegalArgumentException("Invalid decision value: '" + decisionStr + "'. Use approve/reject or y/n.");
		}
		if (decision) {
			approveWithdrawal(appNumStr);
		} else {
			rejectWithdrawal(appNumStr);
		}
	}

	// =========================================================
	// Company Representative / Staff helpers

	/** Approve an application by application number (called by Company Rep via InternshipControl)
	 * This will set the application status to approved and persist applications.
	 */
	void approveApplicationByNumber(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found: " + appNum);
		app.setApplicationStatusSuccess();
		saveApplicationsToDB();
		// Also ensure internship data is updated via InternshipControl if available
		if (intCtrl != null) {
			intCtrl.approveApplicationNumberForInternship(appNum, app.getInternshipID());
		} else {
			throw new IllegalStateException("InternshipControl not set; cannot approve application.");
		}
	}
	void rejectApplicationByNumber(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found: " + appNum);
		app.setApplicationStatusFail();
		saveApplicationsToDB();
		if (intCtrl != null) {
			intCtrl.rejectApplicationNumberForInternship(appNum, app.getInternshipID());
		} else {
			throw new IllegalStateException("InternshipControl not set; cannot reject application.");
		}
	}

	// =========================================================
	// Career Staff methods
	void approveWithdrawal(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found: " + appNum);
		app.setApplicationWithdrawn();
		// persist change
		saveApplicationsToDB();
		// remove application number from associated internship
		if (intCtrl != null) {
			intCtrl.removeApplicationNumberFromInternshipOpportunity(appNum, app.getInternshipID());
		}
	}
	// removed unused stub methods: rejectWithdrawal(Application), addApplicationToPendingList(Application), removeApplicationFromPendingList(Application)

	// =========================================================
	// Company Representative methods
	
	
	// ========================================================
	// Other methods

	void withdrawOtherApplicationsOfApprovedStudent(String studentID) {
		for (Application app : applications) {
			if (app.getStudentID().equals(studentID) && app.getApplicationStatus().equals("approved")) {
				continue; // Skip the approved application
			}
			if (app.getStudentID().equals(studentID)) {
				app.setApplicationWithdrawn();
				updateInternshipsApplicationsInDB(app.getApplicationNumber(), app.getInternshipID(), "remove");
			}
			saveApplicationsToDB();
		}
	}
	
	//=========================================================
	// Private Helpers

	Application getApplicationByNumber(int appNumber) {
		for (Application app : applications) {
			if (app.getApplicationNumber() == appNumber) {
				return app;
			}
		}
		return null;
	}
	private void saveApplicationsToDB() {
		final String CSV_FILE = "Database/Applications.csv";
		try (FileWriter writer = new FileWriter(CSV_FILE)) {
			// Updated header to include company, acceptance, withdrawStatus, studentMajors (space-separated)
			writer.append("ApplicationNumber,InternshipID,StudentID,Company,Status,Acceptance,WithdrawStatus,StudentMajor\n");
			for (Application app : applications) {
				writer.append(String.valueOf(app.getApplicationNumber())).append(",")
					.append(app.getInternshipID()).append(",")
					.append(app.getStudentID()).append(",")
					.append(app.getCompany() != null ? app.getCompany() : "").append(",")
					.append(app.getApplicationStatus() != null ? app.getApplicationStatus() : "pending").append(",")
					.append(app.getAcceptance() != null ? app.getAcceptance() : "").append(",")
					.append(app.getWithdrawStatus() != null ? app.getWithdrawStatus() : "").append(",")
					.append(app.getStudentMajors() != null ? String.join(" ", app.getStudentMajors()) : "")
					.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void updateInternshipsApplicationsInDB(int applicationNumber, String internshipID, String action) {
		if (action.equals("add")) {
			intCtrl.addApplicationNumberToInternshipOpportunity(applicationNumber, internshipID);
		} else if (action.equals("remove")) {
			intCtrl.removeApplicationNumberFromInternshipOpportunity(applicationNumber, internshipID);
		}
	}
}
