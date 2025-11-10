package Backend.Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Backend.Entity.Application;

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

	/** Return application lines for display by the boundary. */
	public List<String> getApplicationsForDisplay() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			out.add(app.toString());
		}
		return out;
	}


	//=========================================================
	// Staff methods
	
	/** Load all applications from database (for Career Staff to review withdrawals, etc.) */
	public void loadAllApplicationsFromDB() {
		applications.clear();
		final String CSV_FILE = "Code/Backend/Lib/application_list.csv";
		File file = new File(CSV_FILE);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			// Skip header
			br.readLine();
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				String[] values = line.split(",");
				if (values.length >= 5) {
					int applicationNumber = Integer.parseInt(values[0]);
					String internshipID = values[1];
					String studentID = values[2];
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

	//=========================================================
	// Student methods

	public void loadStudentApplicationFromDB() {
		String studentID = authCtrl.getUserID();
		// Clear existing applications to avoid duplicates
		applications.clear();
		// Load applications from the database for the given studentID
		final String CSV_FILE = "Code/Backend/Lib/application_list.csv";
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
		
		// Check if student has already accepted an internship opportunity (from student.csv)
		if (getStudentAcceptanceStatus(authCtrl.getUserID())) {
			throw new IllegalStateException("You have already accepted an internship offer. You cannot apply for new internships.");
		}
		
		if (!intCtrl.isVisibleAndNotFullAndNotRejected(internshipID)) {
			throw new IllegalStateException("Internship is either not visible or already full or is rejected by staff.");
		}
		if (!intCtrl.studentFitsRequirements(authCtrl.getUserID(), internshipID)) {
			throw new IllegalStateException("You do not meet the requirements for this internship.");
		}
		// Check if student has an active (non-rejected, non-withdrawn) application for this internship
		if (applications.stream().anyMatch(app -> 
			app.getInternshipID().equals(internshipID) 
			&& !app.getApplicationStatus().equals("rejected")
			&& (app.getWithdrawStatus() == null || !app.getWithdrawStatus().equals("approved")))) {
			throw new IllegalStateException("You have already applied for this internship.");
		}
		if (applications.stream().anyMatch(app -> app.getApplicationStatus().equals("approved"))) {
			throw new IllegalStateException("You have an approved application.");
		}
		String companyName = intCtrl.getInternshipCompany(internshipID);
		List<String> studentMajors = intCtrl.getStudentMajors();
		int newAppNumber = getNextApplicationNumber();
		Application app = new Application(newAppNumber, internshipID, companyName, authCtrl.getUserID(), studentMajors);
		applications.add(app);
		saveApplicationsToDB();
		updateInternshipsApplicationsInDB(app.getApplicationNumber(), internshipID, "add");
		return app.getApplicationNumber();
	}
	void getApplicationStatus() {
		// Ditched: use getApplicationsForDisplay() instead
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
		saveApplicationsToDB();
		// Update student's hasAcceptedInternshipOpportunity in student.csv
		updateStudentAcceptanceStatus(app.getStudentID(), true);
		withdrawOtherApplicationsOfApprovedStudent(app.getStudentID());
	}
	public void rejectOffer(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null || !app.getApplicationStatus().equals("approved")) {
			throw new IllegalArgumentException("Application not found or not approved.");
		}
		app.setAcceptanceNo();
		saveApplicationsToDB();
		// Note: Don't set hasAcceptedInternshipOpportunity to false here
		// Student might have other approved applications they haven't responded to yet
	}
	public void requestWithdrawApplication(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found.");
		String ws = app.getWithdrawStatus();
		if (ws != null && ws.equals("rejected")) {
			throw new IllegalStateException("Previous withdrawal request was rejected. You are refrained from making changes.");
		}
		if (ws != null && ws.equals("pending")) {
			throw new IllegalStateException("Withdrawal request already pending. Please wait for staff approval.");
		}
		if (ws != null && ws.equals("approved")) {
			throw new IllegalStateException("Application already withdrawn.");
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

	/** Return applications with internship details in structured format for table display */
	public List<String> getApplicationsWithInternshipDetails() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			String internID = app.getInternshipID();
			String internTitle = intCtrl.getInternshipTitle(internID);
			String internLevel = intCtrl.getInternshipLevel(internID);
			String internCompany = app.getCompany();
			String internMajors = intCtrl.getInternshipPreferredMajors(internID);
			
			String line = "applicationNumber=" + app.getApplicationNumber() +
			              " | internshipID=" + internID +
			              " | internshipTitle=" + (internTitle != null ? internTitle : "N/A") +
			              " | internshipLevel=" + (internLevel != null ? internLevel : "N/A") +
			              " | companyName=" + (internCompany != null ? internCompany : "N/A") +
			              " | preferredMajors=" + (internMajors != null ? internMajors : "N/A") +
			              " | status=" + app.getApplicationStatus() +
			              " | acceptance=" + (app.getAcceptance() != null ? app.getAcceptance() : "N/A") +
			              " | withdrawStatus=" + (app.getWithdrawStatus() != null ? app.getWithdrawStatus() : "N/A");
			out.add(line);
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
		// Ensure the application is in the applications list for saving
		if (!applications.contains(app)) {
			applications.add(app);
		}
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
		// Ensure the application is in the applications list for saving
		if (!applications.contains(app)) {
			applications.add(app);
		}
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
		
		// Check if student had accepted this offer - if so, need to update their status
		boolean hadAccepted = "yes".equalsIgnoreCase(app.getAcceptance());
		
		app.setApplicationWithdrawn();
		// persist change
		saveApplicationsToDB();
		
		// If student had accepted this offer, update their hasAcceptedInternshipOpportunity to false
		if (hadAccepted) {
			updateStudentAcceptanceStatus(app.getStudentID(), false);
		}
		
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
	// Helpers

	Application getApplicationByNumber(int appNumber) {
		// First check in loaded applications
		for (Application app : applications) {
			if (app.getApplicationNumber() == appNumber) {
				return app;
			}
		}
		// If not found, load from database directly
		return loadApplicationByNumberFromDB(appNumber);
	}
	
	/**
	 * Get the next available application number by reading all applications from the database
	 * and finding the maximum application number, then returning max + 1.
	 */
	private int getNextApplicationNumber() {
		int maxAppNumber = 0;
		final String CSV_FILE = "Code/Backend/Lib/application_list.csv";
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				String[] values = line.split(",");
				if (values.length < 1) continue;
				try {
					int appNum = Integer.parseInt(values[0].trim());
					if (appNum > maxAppNumber) {
						maxAppNumber = appNum;
					}
				} catch (NumberFormatException e) {
					// Skip invalid lines
					continue;
				}
			}
		} catch (IOException e) {
			// If file doesn't exist or can't be read, start from 1
			return 1;
		}
		return maxAppNumber + 1;
	}
	
	private Application loadApplicationByNumberFromDB(int appNumber) {
		final String CSV_FILE = "Code/Backend/Lib/application_list.csv";
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length < 1) continue;
				int appNum = Integer.parseInt(values[0].trim());
				if (appNum != appNumber) continue;
				
				// Found the application, parse it
				String internshipID = values.length > 1 ? values[1].trim() : "";
				String studentID = values.length > 2 ? values[2].trim() : "";
				String company = values.length > 3 ? values[3].trim() : "";
				String status = values.length > 4 ? values[4].trim() : "pending";
				String acceptance = values.length > 5 ? values[5].trim() : null;
				String withdrawStatus = values.length > 6 ? values[6].trim() : null;
				List<String> studentMajors = null;
				if (values.length > 7 && !values[7].trim().isEmpty()) {
					studentMajors = Arrays.asList(values[7].trim().split(" "));
				}
				
				if (acceptance != null && acceptance.isEmpty()) acceptance = null;
				if (withdrawStatus != null && withdrawStatus.isEmpty()) withdrawStatus = null;
				
				return new Application(appNum, internshipID, company, studentID, status, acceptance, withdrawStatus, studentMajors);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private void saveApplicationsToDB() {
		final String CSV_FILE = "Code/Backend/Lib/application_list.csv";
		
		// Load ALL applications from database first
		List<Application> allApplications = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				String[] values = line.split(",");
				if (values.length < 3) continue;
				
				int appNum = Integer.parseInt(values[0].trim());
				String internshipID = values[1].trim();
				String studentID = values[2].trim();
				String company = values.length > 3 ? values[3].trim() : "";
				String status = values.length > 4 ? values[4].trim() : "pending";
				String acceptance = (values.length > 5 && !values[5].trim().isEmpty()) ? values[5].trim() : null;
				String withdrawStatus = (values.length > 6 && !values[6].trim().isEmpty()) ? values[6].trim() : null;
				List<String> studentMajors = (values.length > 7 && !values[7].trim().isEmpty()) ? Arrays.asList(values[7].trim().split(" ")) : null;
				
				Application app = new Application(appNum, internshipID, company, studentID, status, acceptance, withdrawStatus, studentMajors);
				allApplications.add(app);
			}
		} catch (IOException e) {
			// File might not exist yet, that's okay
		}
		
		// Update applications in the allApplications list with those in memory
		// This handles both student applications and company rep/staff updates
		for (Application memApp : applications) {
			// Find and replace the application in allApplications
			boolean found = false;
			for (int i = 0; i < allApplications.size(); i++) {
				if (allApplications.get(i).getApplicationNumber() == memApp.getApplicationNumber()) {
					allApplications.set(i, memApp);
					found = true;
					break;
				}
			}
			// If not found, add it (new application)
			if (!found) {
				allApplications.add(memApp);
			}
		}
		
		// Write everything back
		try (FileWriter writer = new FileWriter(CSV_FILE)) {
			writer.append("ApplicationNumber,InternshipID,StudentID,Company,Status,Acceptance,WithdrawStatus,StudentMajor\n");
			for (Application app : allApplications) {
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
	
	/**
	 * Get the student's hasAcceptedInternshipOpportunity status from student.csv
	 */
	private boolean getStudentAcceptanceStatus(String studentID) {
		final String CSV_FILE = "Code/Backend/Lib/student.csv";
		
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
			String line;
			// Skip header
			br.readLine();
			
			// Read student data
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				String[] values = line.split(",", -1);
				
				// Check if this is the student we're looking for
				if (values.length > 0 && values[0].equals(studentID)) {
					// Get hasAcceptedInternshipOpportunity field (index 5)
					if (values.length > 5) {
						return Boolean.parseBoolean(values[5]);
					}
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Update the student's hasAcceptedInternshipOpportunity field in student.csv
	 */
	private void updateStudentAcceptanceStatus(String studentID, boolean hasAccepted) {
		final String CSV_FILE = "Code/Backend/Lib/student.csv";
		List<String> allLines = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
			String line;
			// Read header
			String header = br.readLine();
			if (header != null) {
				allLines.add(header);
			}
			
			// Read and update student data
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				String[] values = line.split(",", -1); // -1 to preserve empty fields
				
				// Check if this is the student we're looking for
				if (values.length > 0 && values[0].equals(studentID)) {
					// Update the hasAcceptedInternshipOpportunity field (index 5)
					if (values.length > 5) {
						values[5] = String.valueOf(hasAccepted);
					} else {
						// Extend array if needed
						String[] newValues = new String[6];
						System.arraycopy(values, 0, newValues, 0, values.length);
						for (int i = values.length; i < 5; i++) {
							newValues[i] = "";
						}
						newValues[5] = String.valueOf(hasAccepted);
						values = newValues;
					}
					line = String.join(",", values);
				}
				allLines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// Write everything back
		try (FileWriter writer = new FileWriter(CSV_FILE)) {
			for (String line : allLines) {
				writer.write(line);
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
