package Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
				if (values[2].equals(studentID)) {
					int applicationNumber = Integer.parseInt(values[0]);
					String internshipID = values[1];
					String status = values[3];
					String company = values[4];
					String acceptance = values[5].isEmpty() ? null : values[5];
					String withdrawStatus = values[6].isEmpty() ? null : values[6];
					Application app = new Application(applicationNumber, internshipID, company, studentID, status, acceptance, withdrawStatus);
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
		if (!intCtrl.isVisibleAndNotFull(internshipID)) {
			System.out.println("Internship is either not visible or already full.");
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
		Application app = new Application(applications.size() + 1, internshipID, companyName, authCtrl.getUserID());
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
			if (app.getWithdrawStatus().equals("rejected")){
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

	//=========================================================
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

	private Application getApplicationByNumber(int appNumber) {
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
			// Write header
			writer.append("ApplicationNumber,InternshipID,StudentID,Status,WithdrawStatus\n");
			for (Application app : applications) {
				writer.append(app.getApplicationNumber() + "," + app.getInternshipID() + "," + app.getStudentID() + "," + app.getApplicationStatus() + "," + (app.getWithdrawStatus() != null ? app.getWithdrawStatus() : "") + "\n");
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
