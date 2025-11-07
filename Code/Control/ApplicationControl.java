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
	private static final String CSV_FILE = "Code/Lib/application_list.csv";

	private List<String[]> applicationRows = new ArrayList<>();
	private List<String> pendingWithdrawnApplicationID = new ArrayList<>();
	private AuthenticationControl authCtrl;

	public ApplicationControl(AuthenticationControl authCtrl) {
		this.authCtrl = authCtrl;
		loadApplicationFromDB();
	}

	public void loadApplicationFromDB() {
		applicationRows = new ArrayList<>();
		File file = new File(CSV_FILE);
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
				try (FileWriter writer = new FileWriter(file)) {
					writer.append("applicationIndex,internshipiD,studentID,status,withdrawnStatus\n");
				}
			}
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line = br.readLine(); // header
				while ((line = br.readLine()) != null) {
					String[] cols = line.split(",");
					if (cols.length < 5) {
						String[] expanded = new String[5];
						for (int i = 0; i < cols.length; i++) expanded[i] = cols[i];
						for (int i = cols.length; i < 5; i++) expanded[i] = "";
						cols = expanded;
					}
					applicationRows.add(cols);
					// collect pending withdraw list in-memory
					if ("pending".equalsIgnoreCase(cols[4])) {
						pendingWithdrawnApplicationID.add(cols[0]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getApplicationStatus(Application app) {
		if (app == null) return null;
		int idx = resolveIndex(app);
		if (idx < 0) return null;

		String[] row = rowByIndex(idx);
		if (row == null) return null;

		String status = val(row, 3);
		String withdraw = val(row, 4);
		if (withdraw != null && !withdraw.isEmpty()) {
			return "withdraw-" + withdraw; // e.g., withdraw-approved / withdraw-rejected / withdraw-pending
		}
		return status;
	}

	public void makeApplication(String internshipID) {
		if (!authCtrl.isLoggedIn() || !"Student".equals(authCtrl.getUserIdentity())) {
			System.out.println("Only students can submit applications.");
			return;
		}
		if (internshipID == null || internshipID.trim().isEmpty()) {
			System.out.println("No internship ID provided. Aborting.");
			return;
		}
		String studentID = authCtrl.getUserID();
		int nextIndex = nextIndex();
		String[] row = new String[] {
			String.valueOf(nextIndex),
			internshipID.trim(),
			studentID,
			"pending",
			""
		};
		applicationRows.add(row);
		saveAll();
		System.out.println("Application submitted successfully. Your application ID is: " + nextIndex);
	}

	public void requestWithdrawApplication(Application app) {
		if (!authCtrl.isLoggedIn()) {
			System.out.println("You are not logged in.");
			return;
		}
		int idx = resolveIndex(app);
		if (idx < 0) {
			System.out.println("Application not found.");
			return;
		}
		String[] row = rowByIndex(idx);
		if (row == null) {
			System.out.println("Application not found.");
			return;
		}
		// Only the owner (student) can request withdrawal
		String owner = val(row, 2);
		if (!owner.equals(authCtrl.getUserID())) {
			System.out.println("You can only withdraw your own application.");
			return;
		}
		row[4] = "pending"; // mark withdrawal pending review
		if (!pendingWithdrawnApplicationID.contains(row[0])) pendingWithdrawnApplicationID.add(row[0]);
		saveAll();
		System.out.println("Withdrawal request submitted and pending approval.");
	}

	public List<Application> tendingWithdrawal(Application app) {
		// Not yet wired into UI: return empty list placeholder
		return new ArrayList<>();
	}

	public void approveWithdrawal(Application app) {
		if (!authCtrl.isLoggedIn() || !"CareerStaff".equals(authCtrl.getUserIdentity())) {
			System.out.println("Only Career Staff can approve withdrawals.");
			return;
		}
		int idx = resolveIndex(app);
		if (idx < 0) {
			System.out.println("Application not found.");
			return;
		}
		String[] row = rowByIndex(idx);
		if (row == null) {
			System.out.println("Application not found.");
			return;
		}
		row[4] = "approved";
		pendingWithdrawnApplicationID.remove(row[0]);
		saveAll();
		System.out.println("Withdrawal approved.");
	}

	public void rejectWithdrawal(Application app) {
		if (!authCtrl.isLoggedIn() || !"CareerStaff".equals(authCtrl.getUserIdentity())) {
			System.out.println("Only Career Staff can reject withdrawals.");
			return;
		}
		int idx = resolveIndex(app);
		if (idx < 0) {
			System.out.println("Application not found.");
			return;
		}
		String[] row = rowByIndex(idx);
		if (row == null) {
			System.out.println("Application not found.");
			return;
		}
		row[4] = "rejected";
		pendingWithdrawnApplicationID.remove(row[0]);
		saveAll();
		System.out.println("Withdrawal rejected.");
	}

	public void addApplicationToPendingList(Application app) {
		int idx = resolveIndex(app);
		if (idx < 0) return;
		String key = String.valueOf(idx);
		if (!pendingWithdrawnApplicationID.contains(key)) pendingWithdrawnApplicationID.add(key);
	}

	public void removeApplicationToPendingList(Application app) {
		int idx = resolveIndex(app);
		if (idx < 0) return;
		pendingWithdrawnApplicationID.remove(String.valueOf(idx));
	}


	
	// Helpers
	// Inline safe access
	private String val(String[] arr, int idx) { 
		return (arr == null || idx < 0 || idx >= arr.length || arr[idx] == null) ? "" : arr[idx];
	}

	// Resolve or fallback parse of application index
	private int resolveIndex(Application app) {
		if (app.getApplicationNumber() > 0) return app.getApplicationNumber();
		try { return Integer.parseInt(app.getInternshipID()); } catch (Exception e) { return -1; }
	}

	// Find row by index directly
	private String[] rowByIndex(int appIndex) {
		String key = String.valueOf(appIndex);
		for (String[] r : applicationRows) if (val(r,0).equals(key)) return r;
		return null;
	}

	// Compute next index without separate helper
	private int nextIndex() {
		int max=0; for (String[] r: applicationRows) { try { int v=Integer.parseInt(val(r,0)); if (v>max) max=v; } catch(Exception ignore){} }
		return max+1;
	}

	// Persist entire list
	private void saveAll() {
		File file=new File(CSV_FILE);
		try(FileWriter writer=new FileWriter(file)){
			writer.append("applicationIndex,internshipiD,studentID,status,withdrawnStatus\n");
			for(String[] r: applicationRows){
				writer.append(String.join(",", new String[]{val(r,0),val(r,1),val(r,2),val(r,3),val(r,4)})).append("\n");
			}
		}catch(IOException e){e.printStackTrace();}
	}
}
