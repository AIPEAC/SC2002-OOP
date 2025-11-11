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

/**
 * Controls all application-related operations in the system.
 * Manages student applications for internships, including submission, approval/rejection,
 * acceptance decisions, and withdrawal requests. Coordinates with InternshipControl
 * to update internship slot availability.
 * <p>
 * All CSV operations use proper field escaping to handle special characters in
 * application data, ensuring data integrity across the application lifecycle.
 * </p>
 * 
 * @author Allen
 * @version 2.0
 */
public class ApplicationControl {
	/** List of applications currently loaded in memory */
	private List<Application> applications = new ArrayList<Application>();
	
	/** Authentication controller for verifying user permissions */
	private AuthenticationControl authCtrl;
	
	/** Internship controller for updating internship data */
	private InternshipControl intCtrl;

	// =========================================================
	// Constructor and Initializer

	/**
	 * Constructs an ApplicationControl with required dependencies.
	 * 
	 * @param authCtrl the authentication controller
	 * @param intCtrl the internship controller
	 */
	public ApplicationControl(AuthenticationControl authCtrl, InternshipControl intCtrl) {
		this.authCtrl = authCtrl;
		this.intCtrl = intCtrl;
	}

	//=========================================================
	// Both Student and Career Staff methods

	/**
	 * Gets formatted application strings for display in the UI.
	 * Returns all applications currently loaded in memory (either student-specific
	 * or all applications depending on which load method was called).
	 * 
	 * @return list of formatted application strings using {@link Application#toString()}
	 */
	public List<String> getApplicationsForDisplay() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			out.add(app.toString());
		}
		return out;
	}


	//=========================================================
	// Staff methods
	
	/**
	 * Loads all applications from the database regardless of student.
	 * Used by Career Staff to review all applications, handle withdrawals,
	 * and perform system-wide application management.
	 * Uses proper CSV parsing to handle fields with special characters.
	 * Clears existing applications before loading.
	 * 
	 * @see #loadStudentApplicationFromDB() for student-specific loading
	 */
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
				String[] values = ControlUtils.splitCsvLine(line);
				// Unescape fields
				for (int i = 0; i < values.length; i++) {
					values[i] = ControlUtils.unescapeCsvField(values[i]);
				}
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
				if (line.trim().isEmpty()) continue;
				// Use proper CSV parsing that respects quoted fields
				String[] values = ControlUtils.splitCsvLine(line);
				if (values.length >= 5) {
					String sid = ControlUtils.unescapeCsvField(values[2]);
					if (!sid.equals(studentID)) continue;
					
					int applicationNumber = Integer.parseInt(ControlUtils.unescapeCsvField(values[0]));
					String internshipID = ControlUtils.unescapeCsvField(values[1]);
					String company = values.length > 3 ? ControlUtils.unescapeCsvField(values[3]) : null;
					String status = values.length > 4 ? ControlUtils.unescapeCsvField(values[4]) : "pending";
					String acceptance = (values.length > 5 && !ControlUtils.unescapeCsvField(values[5]).isEmpty()) ? ControlUtils.unescapeCsvField(values[5]) : null;
					String withdrawStatus = (values.length > 6 && !ControlUtils.unescapeCsvField(values[6]).isEmpty()) ? ControlUtils.unescapeCsvField(values[6]) : null;
					String majorRaw = values.length > 7 ? ControlUtils.unescapeCsvField(values[7]) : "";
					List<String> studentMajors = null;
					if (!majorRaw.isEmpty()) {
						studentMajors = new ArrayList<>();
						for (String part : majorRaw.split("\\s+")) {
							String t = part.trim();
							if (!t.isEmpty()) studentMajors.add(t);
						}
					}
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
	
	/**
	 * Creates a new internship application for the logged-in student.
	 * Performs multiple validation checks before creating the application:
	 * - User must be logged in as a student
	 * - Student must not have already accepted an internship offer
	 * - Internship must be visible, not full, and not rejected
	 * - Student must meet the internship requirements (year, major, level)
	 * - Student must not have already applied to this internship
	 * - Student must not have more than one approved application
	 * - Student must not have reached the maximum of 3 active applications
	 * 
	 * @param internshipID the ID of the internship to apply for
	 * @return the application number assigned to the new application
	 * @throws IllegalStateException if user is not logged in, not a student, has accepted an offer,
	 *         has an approved application, has reached the 3-application limit, or internship is not available
	 * @throws IllegalArgumentException if internshipID is null or empty
	 */
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
		
		// Check if student has reached the maximum of 3 active applications at once
		long activeApplicationCount = applications.stream()
			.filter(app -> !app.getApplicationStatus().equals("rejected")
				&& (app.getWithdrawStatus() == null || !app.getWithdrawStatus().equals("approved")))
			.count();
		if (activeApplicationCount >= 3) {
			throw new IllegalStateException("You have already applied to 3 internships. Maximum limit reached. Please withdraw an application before applying to a new one.");
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
	
	/**
	 * Checks if the logged-in student has accepted any internship offer.
	 * Looks for applications where acceptance is set to "yes".
	 * 
	 * @return true if student has accepted at least one offer, false otherwise
	 */
	public boolean hasAcceptedOffer() {
		for (Application app : applications) {
			if (app.getAcceptance() != null && app.getAcceptance().equals("yes")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the logged-in student has any approved applications.
	 * Used to enforce the rule that students can only have one approved application
	 * before deciding to accept or reject the offer.
	 * 
	 * @return true if student has at least one approved application, false otherwise
	 */
	public boolean hasApprovedApplication() {
		for (Application app : applications) {
			if (app.getApplicationStatus().equals("approved")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets a list of company names and internship IDs for all approved applications.
	 * Used to display to students which offers they need to respond to.
	 * 
	 * @return list of strings in format "CompanyName (ID: InternshipID)" for each approved application
	 */
	public List<String> getApprovedApplicationInternshipCompaniesAndIDs() {
		List<String> out = new ArrayList<>();
		for (Application app : applications) {
			if (app.getApplicationStatus().equals("approved")) {
				out.add(app.getCompany() + " (ID: " + app.getInternshipID() + ")");
			}
		}
		return out;
	}
	
	/**
	 * Accepts an approved internship offer on behalf of the logged-in student.
	 * Updates the application status, student's acceptance status in the database,
	 * adds the student to the internship's accepted list, and automatically withdraws
	 * (sets withdrawStatus to "approved") all other applications from the same student.
	 * Note: Other applications retain their original status (pending/approved/rejected),
	 * allowing students to reapply if they later withdraw the accepted offer.
	 * 
	 * @param appNum the application number to accept
	 * @throws IllegalArgumentException if application is not found or not approved
	 */
	public void acceptOffer(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null || !app.getApplicationStatus().equals("approved")) {
			throw new IllegalArgumentException("Application not found or not approved.");
		}
		
		// Check if student has already accepted another offer
		if (getStudentAcceptanceStatus(app.getStudentID())) {
			throw new IllegalStateException("You have already accepted an internship offer. You cannot accept another offer.");
		}
		
		// Check if student has any other application with acceptance = "yes"
		boolean hasAcceptedOther = applications.stream()
			.anyMatch(a -> a.getApplicationNumber() != appNum 
				&& a.getStudentID().equals(app.getStudentID()) 
				&& "yes".equalsIgnoreCase(a.getAcceptance()));
		if (hasAcceptedOther) {
			throw new IllegalStateException("You have already accepted another internship offer.");
		}
		
		app.setAcceptanceYes();
		saveApplicationsToDB();
		// Update student's hasAcceptedInternshipOpportunity in student.csv
		updateStudentAcceptanceStatus(app.getStudentID(), true);
		// Add student to internship's acceptedApplicationNumbers list
		if (intCtrl != null) {
			intCtrl.studentAcceptedOfferForInternship(appNum, app.getInternshipID());
		}
		withdrawOtherApplicationsOfApprovedStudent(app.getStudentID(), appNum);
	}
	
	/**
	 * Rejects an approved internship offer on behalf of the logged-in student.
	 * Updates the application's acceptance status to "no". Does not update the
	 * student's overall acceptance status as they may have other pending offers.
	 * 
	 * @param appNum the application number to reject
	 * @throws IllegalArgumentException if application is not found or not approved
	 */
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
	
	/**
	 * Submits a withdrawal request for an application.
	 * Sets the withdraw status to "pending" for staff review.
	 * Cannot withdraw if a previous withdrawal was rejected or already approved.
	 * 
	 * @param appNum the application number to request withdrawal for
	 * @throws IllegalArgumentException if application is not found
	 * @throws IllegalStateException if withdrawal already approved, pending, or previously rejected
	 */
	public void requestWithdrawApplication(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found.");
		
		// Check if application is already rejected - cannot withdraw a rejected application
		if ("rejected".equals(app.getApplicationStatus())) {
			throw new IllegalStateException("Cannot withdraw a rejected application.");
		}
		
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
	
	/**
	 * Gets detailed information about all internships the student has applied to.
	 * Includes application number and full internship details for each application.
	 * 
	 * @return list of formatted strings with application and internship details
	 */
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

	/**
	 * Gets applications with internship details in structured format for table display.
	 * Returns pipe-delimited strings with all relevant application and internship fields.
	 * 
	 * @return list of formatted strings suitable for parsing into UI table rows
	 */
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

	/**
	 * Gets all applications that have pending withdrawal requests.
	 * Used by Career Staff to review and approve/reject withdrawal requests.
	 * 
	 * @return list of formatted strings for each pending withdrawal application
	 */
	public List<String> getPendingWithdrawals() {
		List<String> pending = new ArrayList<>();
		for (Application app : applications) {
			if (app.getWithdrawStatus() != null && app.getWithdrawStatus().equals("pending")) {
				pending.add("Application No: " + app.getApplicationNumber() + " | Internship ID: " + app.getInternshipID() + " | Student: " + app.getStudentID());
			}
		}
		return pending;
	}

	/**
	 * Rejects a withdrawal request for the specified application.
	 * Marks the withdrawal status as rejected.
	 * 
	 * @param appNum the application number to reject withdrawal for
	 * @throws IllegalArgumentException if application is not found
	 */
	void rejectWithdrawalByNumber(int appNum) {
		Application app = getApplicationByNumber(appNum);
		if (app == null) throw new IllegalArgumentException("Application not found: " + appNum);
		app.setApplicationWithdrawnStatus(); // mark as rejected
		saveApplicationsToDB();
	}

	/**
	 * Approves a withdrawal request (UI-friendly wrapper accepting String).
	 * Parses the application number string and delegates to the int version.
	 * 
	 * @param appNumStr the application number as a string
	 * @throws IllegalArgumentException if application number is invalid or not parseable
	 */
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
	 * Handles a withdrawal decision with string inputs.
	 * 
	 * @param appNumStr the application number as a string
	 * @param decisionStr the decision as a string (approve/reject, y/n, etc.)
	 * @throws IllegalArgumentException if inputs are invalid
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

	/**
	 * Approves an application by application number.
	 * 
	 * @param appNum the application number to approve
	 * @throws IllegalArgumentException if application is not found
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
	
	/**
	 * Rejects an application by application number.
	 * 
	 * @param appNum the application number to reject
	 * @throws IllegalArgumentException if application is not found
	 * @throws IllegalStateException if InternshipControl is not set
	 */
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
	
	/**
	 * Approves a withdrawal request for an application.
	 * 
	 * @param appNum the application number
	 * @throws IllegalArgumentException if application is not found
	 */
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

	/**
	 * Withdraws all other applications for a student who has accepted an offer.
	 * When a student accepts one internship offer, all their other applications
	 * are automatically withdrawn (withdrawStatus set to "approved") but their
	 * application status (pending/approved/rejected) remains unchanged.
	 * This allows students to reapply if they later withdraw their accepted offer.
	 * 
	 * @param studentID the student ID
	 * @param acceptedAppNum the application number that was accepted (to skip)
	 */
	void withdrawOtherApplicationsOfApprovedStudent(String studentID, int acceptedAppNum) {
		// Withdraw all other applications from this student (set withdrawStatus to "approved")
		for (Application app : applications) {
			// Skip the application that was just accepted
			if (app.getApplicationNumber() == acceptedAppNum) {
				continue;
			}
			// Withdraw all other applications from this student
			if (app.getStudentID().equals(studentID)) {
				String currentStatus = app.getApplicationStatus();
				String withdrawStatus = app.getWithdrawStatus();
				
				// Skip applications that are already rejected or already withdrawn
				boolean alreadyRejected = "rejected".equals(currentStatus);
				boolean alreadyWithdrawn = "approved".equals(withdrawStatus);
				
				if (!alreadyRejected && !alreadyWithdrawn) {
					// Set withdrawal status to "approved" (automatically withdrawn)
					app.setApplicationWithdrawn();
					// Remove from internship's application list
					updateInternshipsApplicationsInDB(app.getApplicationNumber(), app.getInternshipID(), "remove");
				}
			}
		}
		saveApplicationsToDB();
	}
	
	//=========================================================
	// Helpers

	/**
	 * Gets an application by its number.
	 * 
	 * @param appNumber the application number
	 * @return the Application object or null if not found
	 */
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
	 * Rejects all approved but unanswered applications for a specific internship.
	 * 
	 * @param internshipID the internship ID
	 * @param acceptedApps list of accepted application numbers to exclude
	 */
	void rejectUnansweredApprovedApplicationsForInternship(String internshipID, List<Integer> acceptedApps) {
		// Load all applications from database to check for this internship
		final String CSV_FILE = "Code/Backend/Lib/application_list.csv";
		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				// Use proper CSV parsing that respects quoted fields
				String[] values = ControlUtils.splitCsvLine(line);
				if (values.length >= 5) {
					String iid = ControlUtils.unescapeCsvField(values[1]);
					if (!iid.equals(internshipID)) continue;
					
					int appNum = Integer.parseInt(ControlUtils.unescapeCsvField(values[0]));
					String status = ControlUtils.unescapeCsvField(values[4]);
					String acceptance = (values.length > 5 && !values[5].isEmpty()) ? values[5] : null;
					
					// If approved but not accepted yet, and not in the acceptedApps list, reject it
					if ("approved".equals(status) && acceptance == null && !acceptedApps.contains(appNum)) {
						try {
							rejectApplicationByNumber(appNum);
						} catch (Exception e) {
							// Continue rejecting others even if one fails
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				// Use proper CSV parsing that respects quoted fields
				String[] values = ControlUtils.splitCsvLine(line);
				if (values.length < 1) continue;
				try {
					int appNum = Integer.parseInt(ControlUtils.unescapeCsvField(values[0]).trim());
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
				if (line.trim().isEmpty()) continue;
				// Use proper CSV parsing that respects quoted fields
				String[] values = ControlUtils.splitCsvLine(line);
				if (values.length < 1) continue;
				int appNum = Integer.parseInt(ControlUtils.unescapeCsvField(values[0]).trim());
				if (appNum != appNumber) continue;
				
				// Found the application, parse it
				String internshipID = values.length > 1 ? ControlUtils.unescapeCsvField(values[1]).trim() : "";
				String studentID = values.length > 2 ? ControlUtils.unescapeCsvField(values[2]).trim() : "";
				String company = values.length > 3 ? ControlUtils.unescapeCsvField(values[3]).trim() : "";
				String status = values.length > 4 ? ControlUtils.unescapeCsvField(values[4]).trim() : "pending";
				String acceptance = values.length > 5 ? ControlUtils.unescapeCsvField(values[5]).trim() : null;
				String withdrawStatus = values.length > 6 ? ControlUtils.unescapeCsvField(values[6]).trim() : null;
				String majorRaw = values.length > 7 ? ControlUtils.unescapeCsvField(values[7]).trim() : "";
				List<String> studentMajors = null;
				if (!majorRaw.isEmpty()) {
					studentMajors = new ArrayList<>();
					for (String part : majorRaw.split("\\s+")) {
						String t = part.trim();
						if (!t.isEmpty()) studentMajors.add(t);
					}
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
				// Use proper CSV parsing that respects quoted fields
				String[] values = ControlUtils.splitCsvLine(line);
				if (values.length < 3) continue;
				
				// Unescape all fields
				for (int i = 0; i < values.length; i++) {
					values[i] = ControlUtils.unescapeCsvField(values[i]);
				}
				
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
				// Escape all fields to handle commas properly
				writer.append(ControlUtils.escapeCsvField(String.valueOf(app.getApplicationNumber()))).append(",")
					.append(ControlUtils.escapeCsvField(app.getInternshipID())).append(",")
					.append(ControlUtils.escapeCsvField(app.getStudentID())).append(",")
					.append(ControlUtils.escapeCsvField(app.getCompany() != null ? app.getCompany() : "")).append(",")
					.append(ControlUtils.escapeCsvField(app.getApplicationStatus() != null ? app.getApplicationStatus() : "pending")).append(",")
					.append(ControlUtils.escapeCsvField(app.getAcceptance() != null ? app.getAcceptance() : "")).append(",")
					.append(ControlUtils.escapeCsvField(app.getWithdrawStatus() != null ? app.getWithdrawStatus() : "")).append(",")
					.append(ControlUtils.escapeCsvField(app.getStudentMajors() != null ? String.join(" ", app.getStudentMajors()) : ""))
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
				String[] values = ControlUtils.splitCsvLine(line);
				// Unescape fields
				for (int i = 0; i < values.length; i++) {
					values[i] = ControlUtils.unescapeCsvField(values[i]);
				}
				
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
				String[] values = ControlUtils.splitCsvLine(line);
				// Unescape fields
				for (int i = 0; i < values.length; i++) {
					values[i] = ControlUtils.unescapeCsvField(values[i]);
				}
				
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
					// Rebuild line with escaped fields
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < values.length; i++) {
						if (i > 0) sb.append(",");
						sb.append(ControlUtils.escapeCsvField(values[i]));
					}
					line = sb.toString();
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
