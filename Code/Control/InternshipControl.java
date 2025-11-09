package Control;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import Entity.Application;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;

import Entity.InternshipOpportunity;
import Entity.Users.Student;


public class InternshipControl{
    private List<InternshipOpportunity> internshipOpportunities = new ArrayList<InternshipOpportunity>();
    
    private List<InternshipOpportunity> companyRepsInternshipOpps = null;
    private AuthenticationControl authCtrl;
    private ApplicationControl appCtrl=null;
    private Student student=null;

    // =========================================================
    // Constructor and Initializer

    public InternshipControl(AuthenticationControl authCtrl) {
        this.authCtrl = authCtrl;
        loadInternshipOpportunityFromDB();
    }
    public void setApplicationControl(ApplicationControl appCtrl) {
        this.appCtrl = appCtrl;
    }
    private void loadInternshipOpportunityFromDB() {
        String CSV_FILE = "Lib/internship_opportunity_list.csv";
        //read from csv and initialize internship opportunities
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String internshipID = values[0];
                String title = values[1];
                String description = values[2];
                String level = values[3];
                List<String> preferredMajors = Arrays.asList(values[4].split(" "));
                Date openingDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[5]);
                Date closeDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[6]);
                String status = values[7];
                String companyName = values[8];
                String companyRepInChargeID = values[9];
                int numberOfSlots = Integer.parseInt(values[10]);
                // applicationNumberList is space-separated integers
                List<Integer> applicationNumberList = Arrays.stream(values[11].split(" "))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                // acceptedApplicantNumbers is space-separated integers
                List<Integer> acceptedApplicantNumbers = Arrays.stream(values[12].split(" "))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                boolean visibility = Boolean.parseBoolean(values[13]);
                InternshipOpportunity opp = new InternshipOpportunity(
                        internshipID, title, description, level, preferredMajors,
                        openingDate, closeDate, status, companyName,
                        companyRepInChargeID, numberOfSlots,
                        applicationNumberList, acceptedApplicantNumbers,
                        visibility
                    );
                internshipOpportunities.add(opp);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    //=========================================================
    // All Users methods
    public List<InternshipOpportunity> getAllVisibleInternshipOpportunities() {
        List<InternshipOpportunity> visible = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getVisibility() && !"rejected".equalsIgnoreCase(opp.getStatus())) {
                visible.add(opp);
            }
        }
        return visible;
    }
    public List<Object> getInternshipDetails(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (authCtrl.isLoggedIn()){
            //implementation
            return opp.getDetailsForViewing();
        }
        System.out.println("Please login to view internship details.");
        return null;
    }

    // =========================================================
    // Company Rep methods

    public void requestCreateInternshipOpportunity(
        String internshipTitle, String description, 
        String internshipLevel, List<String> preferredMajors, 
        String openDateStr, String closeDateStr, String numberOfSlotsStr) {
        if (!authCtrl.isLoggedIn()) {
            System.out.println("User not logged in.");
            return;
        }

        // Parse dates and slots here (backend handles conversions)
        java.util.Date openDate = new java.util.Date();
        java.util.Date closeDate = new java.util.Date();
        int numberOfSlots = 1;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        if (openDateStr != null && !openDateStr.trim().isEmpty()) {
            try {
                openDate = sdf.parse(openDateStr.trim());
            } catch (java.text.ParseException e) {
                System.out.println("Invalid opening date format, defaulting to today.");
                openDate = new java.util.Date();
            }
        }
        if (closeDateStr != null && !closeDateStr.trim().isEmpty()) {
            try {
                closeDate = sdf.parse(closeDateStr.trim());
            } catch (java.text.ParseException e) {
                System.out.println("Invalid closing date format, defaulting to today.");
                closeDate = new java.util.Date();
            }
        }
        if (numberOfSlotsStr != null && !numberOfSlotsStr.trim().isEmpty()) {
            try {
                numberOfSlots = Integer.parseInt(numberOfSlotsStr.trim());
                if (numberOfSlots <= 0) numberOfSlots = 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of slots, defaulting to 1.");
                numberOfSlots = 1;
            }
        }

        String companyName = authCtrl.getCompanyName();
        String companyRepID = authCtrl.getUserID();
        String internshipID = autoAssignInternshipID();
        InternshipOpportunity newOpp = new InternshipOpportunity(
            internshipID, internshipTitle, description, 
            preferredMajors, internshipLevel, 
            openDate, closeDate,
            companyName, companyRepID, numberOfSlots);
        internshipOpportunities.add(newOpp);
        updateInternshipInDB();
    }
    public void getInternshipStatus() {
        if (!authCtrl.isLoggedIn() || !authCtrl.getUserIdentity().equals("Company Representative")) {
            System.out.println("User not logged in or not a company representative.");
            return;
        }
        String companyRepID = authCtrl.getUserID();
        companyRepsInternshipOpps = getInternshipsByCompanyRepID(companyRepID);
        if (companyRepsInternshipOpps.isEmpty()) {
            System.out.println("No internship opportunities found for this company representative.");
            return;
        }
        for (InternshipOpportunity opp : companyRepsInternshipOpps) {
            System.out.println(opp);
        }
    }
    public void viewApplications(String internshipID) {
        if (!authCtrl.isLoggedIn()|| !authCtrl.getUserIdentity().equals("Company Representative")) {
            System.out.println("User not logged in or not a company representative.");
            return;
        }
        System.out.println("Please kindly informed that to ensure data privacy, you can only view the major of the students.");
        String companyRepID = authCtrl.getUserID();
        List<Integer> applicationNumbers;
        if (internshipID == null || internshipID.trim().isEmpty()) {
            applicationNumbers = gatherApplication(companyRepID);
        } else {
            InternshipOpportunity opp = getInternshipByID(internshipID);
            if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
                System.out.println("No such internship under your account or not authorized.");
                return;
            }
            applicationNumbers = opp.getApplicationNumberList();
        }
        if (applicationNumbers == null || applicationNumbers.isEmpty()) {
            System.out.println("No applications for the selected internship(s).");
            return;
        }
        for (Integer appNum : applicationNumbers) {
            Application app = appCtrl.getApplicationByNumber(appNum);
            if (app == null) continue;
            InternshipOpportunity opp = getInternshipByID(app.getInternshipID());
            String title = opp != null ? opp.getInternshipTitle() : "(unknown)";
            System.out.println("Internship Title: " + title + " | Application No. " + app.getApplicationNumber());
            // only show majors for privacy
            System.out.println("Student Majors: " + (app.getStudentMajors() != null ? app.getStudentMajors() : "N/A") );
            System.out.println("Status: " + app.getApplicationStatus());
        }
    }
    
    /** Approve an application: mark application approved and add to internship accepted list. */
    public void approveApplicationNumberForInternship(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) {
            System.out.println("Internship not found: " + internshipID);
            return;
        }
        if (opp.isFull()) {
            System.out.println("Cannot approve; internship is already full.");
            return;
        }
        // move application number from pending to accepted list
        opp.approveApplicationNumber(applicationNumber);
        // persist changes to internships CSV
        updateInternshipInDB();
    }

    /** Reject an application for an internship. */
    public void rejectApplicationNumberForInternship(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) {
            System.out.println("Internship not found: " + internshipID);
            return;
        }
        opp.rejectApplicationNumber(applicationNumber);
        updateInternshipInDB();
    }

    /** Convenience wrappers so a CompanyRepresentative CLI (which may not hold an ApplicationControl reference)
     * can request approve/reject actions that also update the ApplicationControl.
     */
    public void approveApplicationAsCompanyRep(int applicationNumber) {
        if (appCtrl != null) {
            appCtrl.approveApplicationByNumber(applicationNumber);
        } else {
            System.out.println("ApplicationControl not set; cannot approve application.");
        }
    }
    public void rejectApplicationAsCompanyRep(int applicationNumber) {
        if (appCtrl != null) {
            appCtrl.rejectApplicationByNumber(applicationNumber);
        } else {
            System.out.println("ApplicationControl not set; cannot reject application.");
        }
    }
    public void approve(Application app) {
        //implementation
    }
    public void reject(Application app) {
        //
    }

    // =========================================================
    // Student methods
    private void loadStudentFromDB(String studentID) {
        if (student != null && student.getUserID().equals(studentID)) {
            return; // Already loaded
        }
    String CSV_FILE = "Lib/student_list.csv";
        //read from csv and initialize student
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(studentID)) continue;
                String[] values = line.split(",");
                String name = values[1];
                String email = values[2];
                List<String> majors = Arrays.asList(values[3].split(" "));
                boolean hasAcceptedInternshipOpportunity = Boolean.parseBoolean(values[5]);
                int year = Integer.parseInt(values[4]);
                student = new Student(studentID, name, email, majors, year, hasAcceptedInternshipOpportunity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isVisibleAndNotFullAndNotRejected(String oppID) {
        InternshipOpportunity opp = getInternshipByID(oppID);
        if (opp != null) {
            if (opp.getVisibility() 
                && opp.getNumOfSlots() > 0 
                && !opp.getStatus().equals("rejected")) {
                return true;
            }
        }
        return false;
    }
    public boolean studentFitsRequirements(String studentID, String oppID) {
        loadStudentFromDB(studentID);
        InternshipOpportunity opp = getInternshipByID(oppID);
        if (opp != null && student != null) {
            List<String> preferredMajors = opp.getPreferredMajors();
            String level = opp.getInternshipLevel();
            boolean levelMatch = false;
            if (level.equals("Basic")) {
                levelMatch = true; // All students eligible
            } else if (level.equals("Intermediate")|| level.equals("Advanced")) {
                if (student.getYear() == 3 || student.getYear() == 4) {
                    levelMatch = true;
                }
            }
            if (levelMatch) {
                // Check if any of the student's majors are in preferred majors
                boolean anyMatch = false;
                if (student.getMajors() != null) {
                    for (String m : student.getMajors()) {
                        if (preferredMajors.contains(m)) {
                            anyMatch = true;
                            break;
                        }
                    }
                }
                if (!anyMatch) {
                    System.out.println("Note: You do not meet the major preferences.\n");
                    System.out.println("preferred majors:"+preferredMajors+"\n");
                    System.out.println("You still can apply.\n");
                }
                return true;
            }
            System.out.println("level requirement:"+level+"\n");
            System.out.println("You do not meet the level requirement.\n");
            return false;
        }
        System.out.println("Error in retrieving student or internship details.");
        return false;
    }
    public void addApplicationNumberToInternshipOpportunity(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            opp.addApplicationNumberToInternship(applicationNumber);
            updateInternshipInDB();
        }
    }
    public void removeApplicationNumberFromInternshipOpportunity(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            opp.removeApplicationNumberFromInternship(applicationNumber);
            updateInternshipInDB();
        }
    }
    public void withdrawEveryOtherApplication(String studentID) {
        appCtrl.withdrawOtherApplicationsOfApprovedStudent(studentID);
        updateInternshipInDB();
    }
    public String getInternshipCompany(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            return opp.getCompanyName();
        }
        return null;
    }
    protected List<String> getStudentMajors() {
        if (student != null) {
            return student.getMajors();
        }
        System.out.println("Student not loaded.");
        return null;
    }
    // =========================================================
    // Career Staff methods

    public List<InternshipOpportunity> getPendingInternshipOpportunities() {
        List<InternshipOpportunity> pending = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if ("pending".equalsIgnoreCase(opp.getStatus())) {
                pending.add(opp);
            }
        }
        return pending;
    }
    public List<InternshipOpportunity> getAllInternshipOpportunities(){ //for report
        return new ArrayList<>(internshipOpportunities);
    } 
    public void approveInternshipCreation(InternshipOpportunity opp) {
        if (opp == null) return;
        opp.setStatusToApproved();
        updateInternshipInDB();
    }
    public void rejectInternshipCreation(InternshipOpportunity rejectInternshipCreation) {
        if (rejectInternshipCreation == null) return;
        rejectInternshipCreation.setStatusToRejected();
        updateInternshipInDB();
    }
    public void approveInternshipCreationByID(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) {
            System.out.println("Internship not found: " + internshipID);
            return;
        }
        approveInternshipCreation(opp);
    }
    public void rejectInternshipCreationByID(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) {
            System.out.println("Internship not found: " + internshipID);
            return;
        }
        rejectInternshipCreation(opp);
    }
    public void reject(InternshipOpportunity opp) {
        if (opp == null) return;
        opp.setStatusToRejected();
        updateInternshipInDB();
    }
    public void changeVisibility(InternshipOpportunity opp) {
        if (opp == null) return;
        // toggle
        boolean cur = opp.getVisibility();
        opp.setVisibility(!cur);
        updateInternshipInDB();
    }

    /** Toggle visibility by internship ID (public wrapper for CLIs) */
    public void changeVisibilityByID(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) {
            System.out.println("Internship not found: " + internshipID);
            return;
        }
        changeVisibility(opp);
    }

    /**
     * Set visibility by internship ID using a string value (safe wrapper for external UIs).
     * Accepts boolean-like strings (y/n, yes/no, approve/reject, a/r).
     */
    public void changeVisibilityByID(String internshipID, String visibleStr) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) {
            System.out.println("Internship not found: " + internshipID);
            return;
        }
        Boolean desired = ControlUtils.parseBooleanLike(visibleStr);
        if (desired == null) {
            System.out.println("Invalid visibility value: '" + visibleStr + "'. Use y/n or approve/reject.");
            return;
        }
        opp.setVisibility(desired.booleanValue());
        updateInternshipInDB();
        System.out.println("Visibility for " + internshipID + " set to " + desired);
    }

    //=========================================================
    // Private Helpers / package private
    // for all users
    InternshipOpportunity getInternshipByID(String internshipID) {
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getInternshipID().equals(internshipID)) {
                return opp;
            }
        }
        return null;
    }
    private void updateInternshipInDB() {
        //write the updated internshipOpportunities list back to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("internships.csv"))) {
            for (InternshipOpportunity opp : internshipOpportunities) {
                String line = String.join(",",
                    opp.getInternshipID(),
                    opp.getInternshipTitle(),
                    opp.getDescription(),
                    opp.getInternshipLevel(),
                    String.join(" ", opp.getPreferredMajors()),
                    new SimpleDateFormat("yyyy-MM-dd").format(opp.getOpeningDate()),
                    new SimpleDateFormat("yyyy-MM-dd").format(opp.getCloseDate()),
                    opp.getStatus(),
                    opp.getCompanyName(),
                    opp.getCompanyRepInChargeID(),
                    String.valueOf(opp.getNumOfSlots()),
                    opp.getApplicationNumberList().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")),
                    opp.getAcceptedApplicationNumbers().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")),
                    String.valueOf(opp.getVisibility())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String autoAssignInternshipID(){
        // the internships will have IDs like #INT0001, #INT0002, ...
        String prefix = "#INT";
        String filePath = "Lib/internship_opportunity_list.csv";
        int maxID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String internshipID = values[0];
                if (internshipID.startsWith(prefix)) {
                    int idNum = Integer.parseInt(internshipID.substring(prefix.length()));
                    if (idNum > maxID) {
                        maxID = idNum;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("%s%04d", prefix, maxID + 1);
    }
    private List<InternshipOpportunity> getInternshipsByCompanyRepID(String companyRepID) {
        List<InternshipOpportunity> repsOpps = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getCompanyRepInChargeID().equals(companyRepID)) {
                repsOpps.add(opp);
            }
        }
        return repsOpps;
    }
    private List<Integer> gatherApplication(String companyRepID) {
        List<Integer> applicationNumbers = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getCompanyRepInChargeID().equals(companyRepID)) {
                applicationNumbers.addAll(opp.getApplicationNumberList());
            }
        }
        return applicationNumbers;
    }
}
