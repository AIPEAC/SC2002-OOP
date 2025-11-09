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

	public void checkApplications() {
		for (Application app : applications) {
			System.out.println(app.toString());
		}
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
	public void makeApplication(String internshipID) {
		// Create a new application for the student
		if (!authCtrl.isLoggedIn()) {
			System.out.println("User not logged in.");
			return;
		}
		if (!authCtrl.getUserIdentity().equals("Student")) {
			System.out.println("Only students can make applications.");
			return;
		}
		if (!intCtrl.isVisibleAndNotFullAndNotRejected(internshipID)) {
			System.out.println("Internship is either not visible or already full or is rejected by staff.");
			return;
		}
		if (!intCtrl.studentFitsRequirements(authCtrl.getUserID(), internshipID)) {
			System.out.println("You do not meet the requirements for this internship.");
			return;
		}
		if (internshipID == null || internshipID.isEmpty()) {
			System.out.println("Invalid Internship ID.");
			return;
		}else if (applications.stream().anyMatch(app -> app.getInternshipID().equals(internshipID))) {
			System.out.println("You have already applied for this internship.");
			return;
		}else if(applications.stream().anyMatch(app -> app.getApplicationStatus().equals("approved"))) {
			System.out.println("You have an approved application.");
			return;
		}
	String companyName= intCtrl.getInternshipCompany(internshipID);
	List<String> studentMajors = intCtrl.getStudentMajors();
	Application app = new Application(applications.size() + 1, internshipID, companyName, authCtrl.getUserID(), studentMajors);
		applications.add(app);
		saveApplicationsToDB();
		updateInternshipsApplicationsInDB(app.getApplicationNumber(), internshipID, "add");
		System.out.println("Application is submitted successfully");
	}
	public void getApplicationStatus() {
		for (Application app : applications) {
			System.out.println(app.toString());
		}
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
	public void getApprovedApplicationInternshipCompaniesAndIDs() {
		for (Application app : applications) {
			if (app.getApplicationStatus().equals("approved")) {
				System.out.println(app.getCompany() + " (ID: " + app.getInternshipID() + ")");
			}
		}
		return;
	}
	public void acceptOffer(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app != null && app.getApplicationStatus().equals("approved")) {
			System.out.println("Offer accepted for Application Number: " + appNum);
			app.setAcceptanceYes();
			withdrawOtherApplicationsOfApprovedStudent(app.getStudentID());
		} else {
			System.out.println("Application not found or not approved.");
		}
	}
	public void rejectOffer(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app != null && app.getApplicationStatus().equals("approved")) {
			app.setAcceptanceNo();
			System.out.println("Offer rejected for Application Number: " + appNum);
		} else {
			System.out.println("Application not found or not approved.");
		}
	}
	public void requestWithdrawApplication(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app != null) {
			String ws = app.getWithdrawStatus();
			if (ws != null && ws.equals("rejected")){
				System.out.println("Previous withdrawal request was rejected. You are refrained from making changes.");
			}
			app.setApplicationWithdrawRequested();
			saveApplicationsToDB();
			System.out.println("Withdrawal request submitted for Application Number: " + app.getApplicationNumber());
		} else {
			System.out.println("Application not found.");
		}
	}
	public void viewInternshipsAppliedTo() {
		for (Application app : applications) {
			intCtrl.getInternshipByID(app.getInternshipID());
		}
	}

	// =========================================================
	// Company Representative / Staff helpers

	/** Approve an application by application number (called by Company Rep via InternshipControl)
	 * This will set the application status to approved and persist applications.
	 */
	public void approveApplicationByNumber(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) {
			System.out.println("Application not found: " + appNum);
			return;
		}
		app.setApplicationStatusSuccess();
		saveApplicationsToDB();
		// Also ensure internship data is updated via InternshipControl if available
		if (intCtrl != null) {
			intCtrl.approveApplicationNumberForInternship(appNum, app.getInternshipID());
		}
		System.out.println("Application " + appNum + " approved.");
	}

	public void rejectApplicationByNumber(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) {
			System.out.println("Application not found: " + appNum);
			return;
		}
		app.setApplicationStatusFail();
		saveApplicationsToDB();
		if (intCtrl != null) {
			intCtrl.rejectApplicationNumberForInternship(appNum, app.getInternshipID());
		}
		System.out.println("Application " + appNum + " rejected.");
	}

	// =========================================================
	// Career Staff methods

	public void loadPendingWithdrawalApplications(Application app) {
		
	}	
	public void approveWithdrawal(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app != null) {
			app.setApplicationWithdrawn();
			System.out.println("Withdrawal approved for Application Number: " + appNum);
		} else {
			System.out.println("Application not found.");
		}
	}
	public void rejectWithdrawal(Application app) {
		
	}
	public void addApplicationToPendingList(Application app) {
		
	}
	public void removeApplicationFromPendingList(Application app) {

	}

	// =========================================================
	// Company Representative methods
	
	
	// ========================================================
	// Other methods

	public void withdrawOtherApplicationsOfApprovedStudent(String studentID) {
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

	protected Application getApplicationByNumber(int appNumber) {
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
