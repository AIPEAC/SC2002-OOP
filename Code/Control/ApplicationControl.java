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
					String withdrawStatus = values[4].isEmpty() ? null : values[4];
					Application app = new Application(applicationNumber, internshipID, studentID, status, withdrawStatus);
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
		Application app = new Application(applications.size() + 1, internshipID, authCtrl.getUserID());
		applications.add(app);
		saveApplicationsToDB();
		System.out.println("Application is submitted successfully");
	}
	public void requestWithdrawApplication(Application app) {
		if (app != null) {
			app.setApplicationWithdrawRequested();
			saveApplicationsToDB();
			System.out.println("Withdrawal request submitted for Application Number: " + app.getApplicationNumber());
		} else {
			System.out.println("Application not found.");
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

	
	//=========================================================
	// Private Helpers

	public Application getApplicationByNumber(int appNumber) {
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
		final String CSV_FILE = "Database/Internships.csv";
		try (FileWriter writer = new FileWriter(CSV_FILE)) {
			// read to see which internship to update
			BufferedReader br = new BufferedReader(new FileReader(CSV_FILE));
			String line;
			List<String[]> allLines = new ArrayList<>();
			// Read all lines
			while ((line = br.readLine()) != null) {
				allLines.add(line.split(","));
			}
			// add or remove applications from internships
			if (action.equals("add")) {
				//implementation
				for (String[] fields : allLines) {
					if (fields[0].equals(internshipID)) {
						fields[11] = fields[11] + applicationNumber + ";";
						
					}
					writer.append(String.join(",", fields) + "\n");
				}
			} else if (action.equals("remove")) {
				//implementation
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
