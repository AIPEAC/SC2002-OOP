package Backend.Control;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import Backend.Entity.Application;
import Backend.Entity.InternshipOpportunity;
import Backend.Entity.Users.Student;

import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Controls all internship opportunity operations in the system.
 * Manages creation, approval, visibility, and filtering of internship opportunities.
 * Company representatives can create up to 5 opportunities with maximum 10 slots each.
 * Students can view and apply to internships based on their major and year of study.
 * Career staff approve or reject internship opportunities before they become visible to students.
 * <p>
 * All CSV read and write operations use proper field escaping via {@link ControlUtils#escapeCsvField(String)}
 * and {@link ControlUtils#splitCsvLine(String)} to handle special characters (commas, quotes, newlines)
 * in internship titles, descriptions, company names, and preferred majors, ensuring data integrity
 * across the application lifecycle. This prevents CSV injection and parsing errors when fields contain
 * special characters.
 * </p>
 * <p>
 * Student major matching uses proper CSV parsing to support majors with commas and semicolons.
 * Internship ID generation (e.g., #INT0001, #INT0002) uses proper CSV parsing to correctly identify
 * the highest existing ID number, even when CSV fields contain special characters.
 * </p>
 * 
 * @author Allen
 * @version 2.1
 */
public class InternshipControl{
    /** List of all internship opportunities in the system */
    private List<InternshipOpportunity> internshipOpportunities = new ArrayList<InternshipOpportunity>();
    
    /** Cached list of internships for current company representative */
    private List<InternshipOpportunity> companyRepsInternshipOpps = null;
    
    /** Authentication controller for verifying user permissions */
    private AuthenticationControl authCtrl;
    
    /** Application controller for coordinating application data */
    private ApplicationControl appCtrl=null;
    
    /** Currently logged-in student (if applicable) */
    private Student student=null;

    // =========================================================
    // Constructor and Initializer

    /**
     * Constructs an InternshipControl and loads all internship opportunities from database.
     * 
     * @param authCtrl the authentication controller for managing user sessions
     */
    public InternshipControl(AuthenticationControl authCtrl) {
        this.authCtrl = authCtrl;
        loadInternshipOpportunityFromDB();
    }
    
    /**
     * Sets the application controller for coordination between internships and applications.
     * 
     * @param appCtrl the application controller
     */
    public void setApplicationControl(ApplicationControl appCtrl) {
        this.appCtrl = appCtrl;
    }
    private void loadInternshipOpportunityFromDB() {
        String CSV_FILE = "Code/Backend/Lib/internship_opportunity_list.csv"; // corrected absolute path within project
        // Ensure header newline present to avoid concatenation when first opportunity is written
        try {
            ControlUtils.ensureCsvPrepared(CSV_FILE, "internshipID,title,description,level,preferredMajors,openingDate,closeDate,status,CompanyName,companyRepInCharge,numOfSlots,pendingApplicationNumberList,acceptedApplicationNumberList,visibility");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //read from csv and initialize internship opportunities
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Use proper CSV parsing that respects quoted fields
                String[] values = ControlUtils.splitCsvLine(line);
                String internshipID = values.length > 0 ? ControlUtils.unescapeCsvField(values[0]) : "";
                String title = values.length > 1 ? ControlUtils.unescapeCsvField(values[1]) : "";
                String description = values.length > 2 ? ControlUtils.unescapeCsvField(values[2]) : "";
                String level = values.length > 3 ? ControlUtils.unescapeCsvField(values[3]) : "";
                String pmRaw = values.length > 4 ? ControlUtils.unescapeCsvField(values[4]) : "";
                List<String> preferredMajors = parsePreferredMajorsRaw(pmRaw);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String openStr = values.length > 5 ? ControlUtils.unescapeCsvField(values[5]) : "";
                String closeStr = values.length > 6 ? ControlUtils.unescapeCsvField(values[6]) : "";
                Date openingDate = (openStr != null && !openStr.trim().isEmpty()) ? sdf.parse(openStr.trim()) : new Date();
                Date closeDate = (closeStr != null && !closeStr.trim().isEmpty()) ? sdf.parse(closeStr.trim()) : new Date();
                String status = values.length > 7 ? ControlUtils.unescapeCsvField(values[7]) : "pending";
                String companyName = values.length > 8 ? ControlUtils.unescapeCsvField(values[8]) : "";
                String companyRepInChargeID = values.length > 9 ? ControlUtils.unescapeCsvField(values[9]) : "";
                int numberOfSlots = 0;
                try {
                    String slotsStr = values.length > 10 ? ControlUtils.unescapeCsvField(values[10]) : "";
                    numberOfSlots = slotsStr != null && !slotsStr.trim().isEmpty()
                            ? Integer.parseInt(slotsStr.trim()) : 0;
                } catch (NumberFormatException nfe) {
                    numberOfSlots = 0;
                }
                // applicationNumberList is space-separated integers
                List<Integer> applicationNumberList = new ArrayList<>();
                if (values.length > 11) {
                    String appListStr = ControlUtils.unescapeCsvField(values[11]);
                    if (appListStr != null && !appListStr.trim().isEmpty()) {
                        applicationNumberList = Arrays.stream(appListStr.trim().split("\\s+"))
                                .filter(s -> s != null && !s.trim().isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    }
                }
                // acceptedApplicantNumbers is space-separated integers
                List<Integer> acceptedApplicantNumbers = new ArrayList<>();
                if (values.length > 12) {
                    String acceptedStr = ControlUtils.unescapeCsvField(values[12]);
                    if (acceptedStr != null && !acceptedStr.trim().isEmpty()) {
                        acceptedApplicantNumbers = Arrays.stream(acceptedStr.trim().split("\\s+"))
                                .filter(s -> s != null && !s.trim().isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    }
                }
                boolean visibility = false;
                if (values.length > 13) {
                    String visStr = ControlUtils.unescapeCsvField(values[13]);
                    if (visStr != null && !visStr.trim().isEmpty()) {
                        visibility = Boolean.parseBoolean(visStr.trim());
                    }
                }
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
    List<InternshipOpportunity> getAllVisibleInternshipOpportunities() {
        List<InternshipOpportunity> visible = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getVisibility() && !"rejected".equalsIgnoreCase(opp.getStatus())) {
                visible.add(opp);
            }
        }
        return visible;
    }
    /** Return details for display (labelled strings) for a given internship ID. */
    public List<String> getInternshipDetails(String internshipID) {
        if (!authCtrl.isLoggedIn()) throw new IllegalStateException("Please login to view internship details.");
        InternshipOpportunity opp = getInternshipByID(internshipID);
        List<String> out = new ArrayList<>();
        if (opp == null) return out;
        out.add("Internship ID: " + opp.getInternshipID());
        out.add("Title: " + opp.getInternshipTitle());
        out.add("Description: " + opp.getDescription());
        out.add("Level: " + opp.getInternshipLevel());
        out.add("Preferred Majors: " + (opp.getPreferredMajors() != null ? opp.getPreferredMajors() : "N/A"));
        out.add("Opening Date: " + opp.getOpeningDate());
        out.add("Closing Date: " + opp.getCloseDate());
        out.add("Status: " + opp.getStatus());
        out.add("Company: " + opp.getCompanyName());
        out.add("Slots: " + opp.getNumOfSlots());
        out.add("Is Full: " + opp.isFull());
        return out;
    }

    // =========================================================
    // Company Rep methods

    /**
     * Request to create a new internship opportunity by a company representative.
     * All text fields (title, description, company name) are properly escaped to handle
     * special characters like commas and quotes.
     * 
     * @param internshipTitle the title of the internship
     * @param description detailed description of the internship (can contain commas, quotes)
     * @param internshipLevel the level (Basic, Intermediate, Advanced)
     * @param preferredMajors list of preferred majors for applicants
     * @param openDateStr opening date in yyyy-MM-dd format
     * @param closeDateStr closing date in yyyy-MM-dd format
     * @param numberOfSlotsStr number of available slots (max 10)
     * @return the auto-generated internship ID
     * @throws IllegalStateException if user not logged in or max internships reached
     * @throws IllegalArgumentException if number of slots exceeds 10
     */
    public String requestCreateInternshipOpportunity(
        String internshipTitle, String description, 
        String internshipLevel, List<String> preferredMajors, 
        String openDateStr, String closeDateStr, String numberOfSlotsStr) {
        if (!authCtrl.isLoggedIn()) {
            throw new IllegalStateException("User not logged in.");
        }

        // Check if company rep has already created 5 internships
        String companyRepID = authCtrl.getUserID();
        List<InternshipOpportunity> existingOpps = getInternshipsByCompanyRepID(companyRepID);
        if (existingOpps.size() >= 5) {
            throw new IllegalStateException("Cannot create more than 5 internship opportunities per company representative.");
        }

        // Parse dates and slots here (backend handles conversions)
        Date openDate = new Date();
        Date closeDate = new Date();
        int numberOfSlots = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (openDateStr != null && !openDateStr.trim().isEmpty()) {
            try {
                openDate = sdf.parse(openDateStr.trim());
            } catch (ParseException e) {
                openDate = new Date();
            }
        }
        if (closeDateStr != null && !closeDateStr.trim().isEmpty()) {
            try {
                closeDate = sdf.parse(closeDateStr.trim());
            } catch (ParseException e) {
                closeDate = new Date();
            }
        }
        if (numberOfSlotsStr != null && !numberOfSlotsStr.trim().isEmpty()) {
            try {
                numberOfSlots = Integer.parseInt(numberOfSlotsStr.trim());
                if (numberOfSlots <= 0) numberOfSlots = 1;
                if (numberOfSlots > 10) {
                    throw new IllegalArgumentException("Maximum number of slots is 10.");
                }
            } catch (NumberFormatException e) {
                numberOfSlots = 1;
            }
        }

        String companyName = authCtrl.getCompanyName();
        String internshipID = autoAssignInternshipID();
        InternshipOpportunity newOpp = new InternshipOpportunity(
            internshipID, internshipTitle, description, 
            preferredMajors, internshipLevel, 
            openDate, closeDate,
            companyName, companyRepID, numberOfSlots);
        internshipOpportunities.add(newOpp);
        updateInternshipInDB();
        return internshipID;
    }
    /** Return a list of formatted internship lines for display to the company representative. */
    public List<String> getInternshipStatus() {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        String companyRepID = authCtrl.getUserID();
        companyRepsInternshipOpps = getInternshipsByCompanyRepID(companyRepID);
        List<String> out = new ArrayList<>();
        if (companyRepsInternshipOpps.isEmpty()) {
            return out; // empty list signals none found
        }
        for (InternshipOpportunity opp : companyRepsInternshipOpps) {
            out.add(opp.toString());
        }
        return out;
    }

    /**
     * Return company rep's internships formatted for UI table with status.
     * Format: internshipID=... | internshipTitle=... | internshipLevel=... | companyName=... | preferredMajors=[...] | status=...
     */
    public List<String> getMyInternshipsWithStatus() {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        String companyRepID = authCtrl.getUserID();
        List<InternshipOpportunity> myOpps = getInternshipsByCompanyRepID(companyRepID);
        List<String> out = new ArrayList<>();
        String DELIM = " | ";
        for (InternshipOpportunity opp : myOpps) {
            StringBuilder sb = new StringBuilder();
            sb.append("internshipID=").append(opp.getInternshipID() != null ? opp.getInternshipID() : "");
            sb.append(DELIM).append("internshipTitle=").append(opp.getInternshipTitle() != null ? opp.getInternshipTitle() : "");
            sb.append(DELIM).append("internshipLevel=").append(opp.getInternshipLevel() != null ? opp.getInternshipLevel() : "");
            sb.append(DELIM).append("companyName=").append(opp.getCompanyName() != null ? opp.getCompanyName() : "");
            sb.append(DELIM).append("preferredMajors=").append(formatPreferredMajorsForDisplay(opp.getPreferredMajors()));
            sb.append(DELIM).append("status=").append(opp.getStatus() != null ? opp.getStatus() : "");
            out.add(sb.toString());
        }
        return out;
    }

    /**
     * Return applications for a given internship (company rep only).
     * Format per line: applicationNumber=... | studentMajors=[major1, major2] | status=...
     * Does NOT include student name or ID.
     */
    public List<String> getApplicationsForInternship(String internshipID) {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        String companyRepID = authCtrl.getUserID();
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
            throw new IllegalArgumentException("No such internship under your account or not authorized.");
        }
        List<Integer> applicationNumbers = opp.getApplicationNumberList();
        List<String> out = new ArrayList<>();
        if (applicationNumbers == null || applicationNumbers.isEmpty()) {
            return out;
        }
        String DELIM = " | ";
        for (Integer appNum : applicationNumbers) {
            Application app = appCtrl.getApplicationByNumber(appNum);
            if (app == null) continue;
            StringBuilder sb = new StringBuilder();
            sb.append("applicationNumber=").append(appNum);
            sb.append(DELIM).append("studentMajors=");
            if (app.getStudentMajors() != null && !app.getStudentMajors().isEmpty()) {
                sb.append("[").append(String.join(", ", app.getStudentMajors())).append("]");
            } else {
                sb.append("[]");
            }
            sb.append(DELIM).append("status=").append(app.getApplicationStatus() != null ? app.getApplicationStatus() : "pending");
            out.add(sb.toString());
        }
        return out;
    }

    /**
     * Return detailed student information for a specific application (company rep only).
     * Format: studentID=... | studentName=... | studentEmail=... | studentMajors=[...] | studentYear=... | status=...
     */
    public String getDetailedStudentInfoForApplication(int applicationNumber) {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        Application app = appCtrl.getApplicationByNumber(applicationNumber);
        if (app == null) {
            throw new IllegalArgumentException("Application not found.");
        }
        
        // Verify this application is for one of the company rep's internships
        String companyRepID = authCtrl.getUserID();
        InternshipOpportunity opp = getInternshipByID(app.getInternshipID());
        if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
            throw new IllegalArgumentException("Not authorized to view this application.");
        }
        
        String studentID = app.getStudentID();
        String DELIM = " | ";
        StringBuilder sb = new StringBuilder();
        
        // Read student details from CSV
        try (BufferedReader br = new BufferedReader(new FileReader("Code/Backend/Lib/student.csv"))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] vals = ControlUtils.splitCsvLine(line);
                // Unescape fields
                for (int i = 0; i < vals.length; i++) {
                    vals[i] = ControlUtils.unescapeCsvField(vals[i]);
                }
                if (vals.length > 0 && vals[0].equals(studentID)) {
                    String name = vals.length > 1 ? vals[1] : "";
                    String email = vals.length > 2 ? vals[2] : "";
                    String majorsRaw = vals.length > 3 ? vals[3] : "";
                    String year = vals.length > 4 ? vals[4] : "";
                    
                    sb.append("studentID=").append(studentID);
                    sb.append(DELIM).append("studentName=").append(name);
                    sb.append(DELIM).append("studentEmail=").append(email);
                    sb.append(DELIM).append("studentMajors=").append(majorsRaw);
                    sb.append(DELIM).append("studentYear=").append(year);
                    sb.append(DELIM).append("status=").append(app.getApplicationStatus() != null ? app.getApplicationStatus() : "pending");
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading student information: " + e.getMessage());
        }
        
        throw new IllegalArgumentException("Student information not found.");
    }

    /**
     * Approve application by internship ID and application number (company rep only).
     */
    /**
     * Approve application and return a status message.
     * Returns additional notification if internship becomes full.
     */
    public String approveApplicationForInternship(String internshipID, int applicationNumber) {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        String companyRepID = authCtrl.getUserID();
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
            throw new IllegalArgumentException("No such internship under your account or not authorized.");
        }
        // Check if internship is already full (based on students who have accepted offers)
        if (opp.isFull()) {
            throw new IllegalStateException("Cannot approve application - this internship is already full (" + 
                opp.getAcceptedApplicationNumbers().size() + "/" + opp.getNumOfSlots() + " slots filled).");
        }
        if (appCtrl != null) {
            appCtrl.approveApplicationByNumber(applicationNumber);
            // Note: Internship won't be considered full until students actually accept their offers
            return "Approved application: " + applicationNumber;
        } else {
            throw new IllegalStateException("ApplicationControl not set; cannot approve application.");
        }
    }
    
    /**
     * @deprecated Use approveApplicationForInternship which returns status message instead.
     *
     * Check if the internship has reached its capacity. If so, reject all remaining pending applications.
     * Returns a message if internship is full, null otherwise.
     */
    private String checkAndRejectIfFull(InternshipOpportunity opp) {
        if (opp == null) return null;
        
        List<Integer> acceptedApps = opp.getAcceptedApplicationNumbers();
        int numAccepted = (acceptedApps != null) ? acceptedApps.size() : 0;
        int numSlots = opp.getNumOfSlots();
        
        if (numAccepted >= numSlots) {
            // Internship is full - reject all other pending applications
            List<Integer> pendingApps = opp.getApplicationNumberList();
            if (pendingApps != null && !pendingApps.isEmpty()) {
                int rejectedCount = 0;
                for (Integer appNum : pendingApps) {
                    if (appCtrl != null) {
                        try {
                            appCtrl.rejectApplicationByNumber(appNum);
                            rejectedCount++;
                        } catch (Exception e) {
                            // Continue rejecting others even if one fails
                        }
                    }
                }
                if (rejectedCount > 0) {
                    return "This internship is now FULL (" + numAccepted + "/" + numSlots + " slots filled).\n" + rejectedCount + " other pending application(s) have been automatically rejected.";
                }
            }
            // If no pending apps to reject, just notify that it's full
            return "This internship is now FULL (" + numAccepted + "/" + numSlots + " slots filled).";
        }
        return null;
    }

    /**
     * Reject application by internship ID and application number (company rep only).
     */
    public void rejectApplicationForInternship(String internshipID, int applicationNumber) {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        String companyRepID = authCtrl.getUserID();
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
            throw new IllegalArgumentException("No such internship under your account or not authorized.");
        }
        if (appCtrl != null) {
            appCtrl.rejectApplicationByNumber(applicationNumber);
        } else {
            throw new IllegalStateException("ApplicationControl not set; cannot reject application.");
        }
    }

    /** Return a list of formatted application lines for display to the company representative. */
    public List<String> viewApplications(String internshipID) {
        if (!isCompanyRepLoggedIn()) {
            throw new IllegalStateException("User not logged in or not a company representative.");
        }
        List<String> out = new ArrayList<>();
        String companyRepID = authCtrl.getUserID();
        List<Integer> applicationNumbers;
        if (internshipID == null || internshipID.trim().isEmpty()) {
            applicationNumbers = gatherApplication(companyRepID);
        } else {
            InternshipOpportunity opp = getInternshipByID(internshipID);
            if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
                throw new IllegalArgumentException("No such internship under your account or not authorized.");
            }
            applicationNumbers = opp.getApplicationNumberList();
        }
        if (applicationNumbers == null || applicationNumbers.isEmpty()) {
            return out;
        }
        for (Integer appNum : applicationNumbers) {
            Application app = appCtrl.getApplicationByNumber(appNum);
            if (app == null) continue;
            InternshipOpportunity opp = getInternshipByID(app.getInternshipID());
            String title = opp != null ? opp.getInternshipTitle() : "(unknown)";
            out.add("Internship Title: " + title + " | Application No. " + app.getApplicationNumber());
            out.add("Student Majors: " + (app.getStudentMajors() != null ? app.getStudentMajors() : "N/A"));
            out.add("Status: " + app.getApplicationStatus());
        }
        return out;
    }
    
    /** Approve an application: mark application approved (but don't add to accepted list yet). */
    void approveApplicationNumberForInternship(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        // Note: Don't check isFull() here because this is called after approval decision is made
        // The full check happens in approveApplicationForInternship before calling this
        // Remove application number from pending list (but don't add to accepted list yet)
        opp.approveApplicationNumber(applicationNumber);
        updateInternshipInDB();
    }
    
    /** Called when a student accepts an approved offer - add to internship's accepted list. */
    void studentAcceptedOfferForInternship(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        
        // Check if internship would become full after this acceptance
        if (opp.isFull()) {
            throw new IllegalStateException("This internship is already full. Cannot accept more offers.");
        }
        
        opp.studentAcceptedOffer(applicationNumber);
        updateInternshipInDB();
        
        // If internship is now full, reject all other approved applications that haven't been accepted yet
        if (opp.isFull() && appCtrl != null) {
            List<Integer> acceptedApps = opp.getAcceptedApplicationNumbers();
            
            // Ask ApplicationControl to reject all approved applications for this internship
            // that haven't been accepted by students yet
            appCtrl.rejectUnansweredApprovedApplicationsForInternship(internshipID, acceptedApps);
        }
    }

    /** Reject an application for an internship. */
    void rejectApplicationNumberForInternship(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
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
            throw new IllegalStateException("ApplicationControl not set; cannot approve application.");
        }
    }
    public void rejectApplicationAsCompanyRep(int applicationNumber) {
        if (appCtrl != null) {
            appCtrl.rejectApplicationByNumber(applicationNumber);
        } else {
            throw new IllegalStateException("ApplicationControl not set; cannot reject application.");
        }
    }

    // =========================================================
    // Student methods
    
    /**
     * Loads student data from the student.csv database file.
     * Uses proper CSV parsing to handle fields containing special characters
     * such as majors with commas (e.g., "Accountancy (Sustainability Management and Analytics)").
     * Supports multiple majors separated by semicolons or spaces.
     * 
     * @param studentID the student ID to load
     */
    private void loadStudentFromDB(String studentID) {
        if (student != null && student.getUserID().equals(studentID)) {
            return; // Already loaded
        }
    String CSV_FILE = "Code/Backend/Lib/student.csv";
        //read from csv and initialize student
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Use proper CSV parsing that respects quoted fields
                String[] values = ControlUtils.splitCsvLine(line);
                if (values.length < 6) continue;
                
                String id = ControlUtils.unescapeCsvField(values[0]);
                if (!id.equals(studentID)) continue;
                
                String name = ControlUtils.unescapeCsvField(values[1]);
                String email = ControlUtils.unescapeCsvField(values[2]);
                String majorRaw = ControlUtils.unescapeCsvField(values[3]);
                // Majors are separated by semicolons only (not spaces, as major names can contain spaces)
                List<String> majors = new ArrayList<>();
                if (majorRaw.contains(";")) {
                    for (String part : majorRaw.split(";")) {
                        String t = part.trim();
                        if (!t.isEmpty()) majors.add(t);
                    }
                } else {
                    // Single major (no semicolon separator)
                    String t = majorRaw.trim();
                    if (!t.isEmpty()) majors.add(t);
                }
                int year = Integer.parseInt(ControlUtils.unescapeCsvField(values[4]));
                boolean hasAcceptedInternshipOpportunity = Boolean.parseBoolean(ControlUtils.unescapeCsvField(values[5]));
                student = new Student(studentID, name, email, majors, year, hasAcceptedInternshipOpportunity);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    boolean isVisibleAndNotFullAndNotRejected(String oppID) {
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
    boolean studentFitsRequirements(String studentID, String oppID) {
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
                if (student.getMajors() != null && !student.getMajors().isEmpty()) {
                    for (String m : student.getMajors()) {
                        if (preferredMajors != null && preferredMajors.contains(m)) {
                            anyMatch = true;
                            break;
                        }
                    }
                }
                // If no preferred majors specified, all students with correct level are eligible
                if (preferredMajors == null || preferredMajors.isEmpty()) {
                    anyMatch = true;
                }
                return anyMatch;
            }
            return false;
        }
        throw new IllegalStateException("Error in retrieving student or internship details.");
    }
    void addApplicationNumberToInternshipOpportunity(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            opp.addApplicationNumberToInternship(applicationNumber);
            updateInternshipInDB();
        }
    }
    void removeApplicationNumberFromInternshipOpportunity(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            opp.removeApplicationNumberFromInternship(applicationNumber);
            updateInternshipInDB();
        }
    }
    // Note: This method is deprecated - use ApplicationControl.acceptOffer instead
    // which automatically handles rejecting other applications
    // void withdrawEveryOtherApplication(String studentID) {
    //     appCtrl.withdrawOtherApplicationsOfApprovedStudent(studentID, acceptedAppNum);
    //     updateInternshipInDB();
    // }
    String getInternshipCompany(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            return opp.getCompanyName();
        }
        return null;
    }
    
    String getInternshipTitle(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            return opp.getInternshipTitle();
        }
        return null;
    }
    
    String getInternshipLevel(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            return opp.getInternshipLevel();
        }
        return null;
    }
    
    String getInternshipPreferredMajors(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp != null) {
            List<String> majors = opp.getPreferredMajors();
            return majors != null ? majors.toString() : "[]";
        }
        return "[]";
    }
    
    protected List<String> getStudentMajors() {
        if (student != null) {
            return student.getMajors();
        }
        // student not loaded; backend does not print. Caller should handle null.
        return null;
    }
    // =========================================================
    // Career Staff methods

    /** Return formatted lines for pending internship opportunities (for boundary printing). */
    public List<String> getPendingInternshipOpportunities() {
        List<String> out = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if ("pending".equalsIgnoreCase(opp.getStatus())) {
                out.add(opp.getInternshipID() + " | " + opp.getInternshipTitle() + " | Company: " + opp.getCompanyName() + " | Status: " + opp.getStatus());
            }
        }
        return out;
    }
    List<InternshipOpportunity> getAllInternshipOpportunities(){ //for report
        return new ArrayList<>(internshipOpportunities);
    } 

    /** Return formatted lines for visible internships applying the provided filter criteria.
     *  The parameters are unpacked to keep frontend Filter types decoupled from backend.
     *  Any of the parameters may be null (null filterIn means no criteria; empty filterType means no sorting).
     */
    public List<String> getAllVisibleInternshipOpportunitiesForDisplay(String filterType, boolean ascending, Map<String, List<String>> filterIn) {
        List<InternshipOpportunity> Opplist = getAllVisibleInternshipOpportunities();
        if (Opplist == null) return new ArrayList<>();

        if (filterIn != null && !filterIn.isEmpty()) {
            Map<String, List<String>> criteria = filterIn;
            Opplist = Opplist.stream().filter(opp -> {
                for (Map.Entry<String, List<String>> e : criteria.entrySet()) {
                    String key = e.getKey();
                    List<String> vals = e.getValue();
                    if (vals == null || vals.isEmpty()) continue;
                    boolean matches = false;
                    switch (key) {
                        case "companyName":
                            for (String v : vals) if (opp.getCompanyName() != null && opp.getCompanyName().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "internshipLevel":
                            for (String v : vals) if (opp.getInternshipLevel() != null && opp.getInternshipLevel().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "preferredMajors":
                            if (opp.getPreferredMajors() != null) {
                                for (String v : vals) if (opp.getPreferredMajors().contains(v)) { matches = true; break; }
                            }
                            break;
                        case "internshipID":
                            for (String v : vals) if (opp.getInternshipID() != null && opp.getInternshipID().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "internshipTitle":
                            for (String v : vals) if (opp.getInternshipTitle() != null && opp.getInternshipTitle().equalsIgnoreCase(v)) matches = true;
                            break;
                        default:
                            matches = true;
                    }
                    if (!matches) return false;
                }
                return true;
            }).collect(Collectors.toList());
        }

        // Sorting
        if (filterType != null && !filterType.isEmpty()) {
            Comparator<InternshipOpportunity> cmp = null;
            switch (filterType) {
                case "title":
                    cmp = Comparator.comparing(InternshipOpportunity::getInternshipTitle, Comparator.nullsLast(String::compareToIgnoreCase));
                    break;
                case "companyName":
                    cmp = Comparator.comparing(InternshipOpportunity::getCompanyName, Comparator.nullsLast(String::compareToIgnoreCase));
                    break;
                case "openDate":
                    cmp = Comparator.comparing(InternshipOpportunity::getOpeningDate, Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                case "numberOfSlots":
                    cmp = Comparator.comparingInt(InternshipOpportunity::getNumOfSlots);
                    break;
                default:
            }
            if (cmp != null) {
                if (!ascending) cmp = cmp.reversed();
                Opplist.sort(cmp);
            }
        }

        List<String> out = new ArrayList<>();
        for (InternshipOpportunity opp : Opplist) out.add(opp.toString());
        return out;
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
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        approveInternshipCreation(opp);
    }
    public void rejectInternshipCreationByID(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        rejectInternshipCreation(opp);
    }
    public void reject(InternshipOpportunity opp) {
        if (opp == null) return;
        opp.setStatusToRejected();
        updateInternshipInDB();
    }
    public boolean changeVisibility(InternshipOpportunity opp) {
        if (opp == null) return false;
        // toggle
        boolean cur = opp.getVisibility();
        opp.setVisibility(!cur);
        updateInternshipInDB();
        return !cur; // return new visibility state
    }

    /** Toggle visibility by internship ID (public wrapper for CLIs) */
    public boolean changeVisibilityByID(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        return changeVisibility(opp);
    }

    /**
     * Set visibility by internship ID using a string value (safe wrapper for external UIs).
     * Accepts boolean-like strings (y/n, yes/no, approve/reject, a/r).
     */
    public void changeVisibilityByID(String internshipID, String visibleStr) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        Boolean desired = ControlUtils.parseBooleanLike(visibleStr);
        if (desired == null) throw new IllegalArgumentException("Invalid visibility value: '" + visibleStr + "'. Use y/n or approve/reject.");
        opp.setVisibility(desired.booleanValue());
        updateInternshipInDB();
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
        // Write the updated internshipOpportunities list back to the CSV file (same file the loader reads)
        String CSV_FILE = "Code/Backend/Lib/internship_opportunity_list.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            // Always write header first followed by newline
            writer.write("internshipID,title,description,level,preferredMajors,openingDate,closeDate,status,CompanyName,companyRepInCharge,numOfSlots,pendingApplicationNumberList,acceptedApplicationNumberList,visibility");
            writer.newLine();
            for (InternshipOpportunity opp : internshipOpportunities) {
                // Escape all string fields that might contain commas
                String line = String.join(",",
                    ControlUtils.escapeCsvField(opp.getInternshipID()),
                    ControlUtils.escapeCsvField(opp.getInternshipTitle()),
                    ControlUtils.escapeCsvField(opp.getDescription()),
                    ControlUtils.escapeCsvField(opp.getInternshipLevel()),
                    ControlUtils.escapeCsvField(opp.getPreferredMajors() != null ? String.join(";", opp.getPreferredMajors()) : ""),
                    ControlUtils.escapeCsvField(new SimpleDateFormat("yyyy-MM-dd").format(opp.getOpeningDate())),
                    ControlUtils.escapeCsvField(new SimpleDateFormat("yyyy-MM-dd").format(opp.getCloseDate())),
                    ControlUtils.escapeCsvField(opp.getStatus()),
                    ControlUtils.escapeCsvField(opp.getCompanyName()),
                    ControlUtils.escapeCsvField(opp.getCompanyRepInChargeID()),
                    ControlUtils.escapeCsvField(String.valueOf(opp.getNumOfSlots())),
                    ControlUtils.escapeCsvField(opp.getApplicationNumberList().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" "))),
                    ControlUtils.escapeCsvField(opp.getAcceptedApplicationNumbers().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" "))),
                    ControlUtils.escapeCsvField(String.valueOf(opp.getVisibility()))
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Auto-generates a sequential internship ID in the format #INT0001, #INT0002, etc.
     * Uses proper CSV parsing to handle fields with special characters, ensuring accurate
     * ID extraction even when CSV contains commas, quotes, or other special characters in
     * title, description, or other fields.
     * 
     * @return the next available internship ID (e.g., "#INT0004" if highest existing is #INT0003)
     */
    private String autoAssignInternshipID(){
        // the internships will have IDs like #INT0001, #INT0002, ...
        String prefix = "#INT";
        String filePath = "Code/Backend/Lib/internship_opportunity_list.csv";
        int maxID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Use proper CSV parsing that respects quoted fields
                String[] values = ControlUtils.splitCsvLine(line);
                if (values.length > 0) {
                    String internshipID = ControlUtils.unescapeCsvField(values[0]);
                    if (internshipID.startsWith(prefix)) {
                        try {
                            int idNum = Integer.parseInt(internshipID.substring(prefix.length()));
                            if (idNum > maxID) {
                                maxID = idNum;
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid ID format
                            continue;
                        }
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
    // =========================================================
    // Helper methods for frontend filtering UI
    /** Return unique company names among visible internship opportunities. */
    public List<String> getVisibleCompanyNames() {
        return getAllVisibleInternshipOpportunities().stream()
                .map(InternshipOpportunity::getCompanyName)
                .filter(n -> n != null && !n.isEmpty())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    private boolean isCompanyRepLoggedIn() {
        if (authCtrl == null || !authCtrl.isLoggedIn()) return false;
        String identity = authCtrl.getUserIdentity();
        if (identity == null) return false;
        if ("CompanyRepresentative".equals(identity)) return true;
        return "Company Representative".equals(identity);
    }

    /** Parse a raw preferred majors field from CSV into a cleaned List&lt;String&gt;.
     *  Accepts formats like: "a;b;c" or "a, b, c" or "[a, b]" or single value. Does not split on spaces.
     */
    private List<String> parsePreferredMajorsRaw(String pmRaw) {
        List<String> out = new ArrayList<>();
        if (pmRaw == null) return out;
        String s = pmRaw.trim();
        if (s.isEmpty()) return out;
        // strip surrounding brackets
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length()-1).trim();
        }
        // Prefer semicolon separator
        if (s.contains(";")) {
            for (String part : s.split(";")) {
                String t = part.trim(); if (!t.isEmpty()) out.add(t);
            }
            return out;
        }
        // Fall back to comma separator
        if (s.contains(",")) {
            for (String part : s.split(",")) {
                String t = part.trim(); if (!t.isEmpty()) out.add(t);
            }
            return out;
        }
        // Single entry (may contain spaces)
        out.add(s);
        return out;
    }

    /** Format preferredMajors list into a bracketed, comma-separated string for display. */
    public String formatPreferredMajorsForDisplay(List<String> majors) {
        if (majors == null || majors.isEmpty()) return "[]";
        List<String> clean = new ArrayList<>();
        for (String m : majors) {
            if (m == null) continue; String t = m.trim(); if (!t.isEmpty()) clean.add(t);
        }
        return "[" + String.join(", ", clean) + "]";
    }

    /**
     * Return formatted lines for approved, visible internships applying the provided filter criteria.
     * This avoids exposing entities to the UI and centralizes approved-only logic.
     * For students, only shows internships matching their major(s).
     */
    public List<String> getApprovedVisibleInternshipOpportunitiesForDisplay(String filterType, boolean ascending, Map<String, List<String>> filterIn) {
        List<InternshipOpportunity> Opplist = getAllVisibleInternshipOpportunities().stream()
                .filter(opp -> "approved".equalsIgnoreCase(opp.getStatus()))
                .collect(Collectors.toList());
        if (Opplist == null) return new ArrayList<>();

        // If logged-in user is a student, filter by their major(s)
        if (authCtrl != null && authCtrl.isLoggedIn() && "Student".equals(authCtrl.getUserIdentity())) {
            Backend.Entity.Users.User user = authCtrl.getUser();
            if (user instanceof Student) {
                Student student = (Student) user;
                List<String> studentMajors = student.getMajors();
                if (studentMajors != null && !studentMajors.isEmpty()) {
                    Opplist = Opplist.stream().filter(opp -> {
                        List<String> preferredMajors = opp.getPreferredMajors();
                        if (preferredMajors == null || preferredMajors.isEmpty()) {
                            return true; // No preferred majors means open to all
                        }
                        // Check if at least one student major matches any preferred major
                        for (String studentMajor : studentMajors) {
                            for (String prefMajor : preferredMajors) {
                                if (studentMajor.equalsIgnoreCase(prefMajor)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                }
            }
        }

        if (filterIn != null && !filterIn.isEmpty()) {
            Map<String, List<String>> criteria = filterIn;
            Opplist = Opplist.stream().filter(opp -> {
                for (Map.Entry<String, List<String>> e : criteria.entrySet()) {
                    String key = e.getKey();
                    List<String> vals = e.getValue();
                    if (vals == null || vals.isEmpty()) continue;
                    boolean matches = false;
                    switch (key) {
                        case "companyName":
                            for (String v : vals) if (opp.getCompanyName() != null && opp.getCompanyName().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "internshipLevel":
                            for (String v : vals) if (opp.getInternshipLevel() != null && opp.getInternshipLevel().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "preferredMajors":
                            if (opp.getPreferredMajors() != null) {
                                for (String v : vals) if (opp.getPreferredMajors().contains(v)) { matches = true; break; }
                            }
                            break;
                        case "internshipID":
                            for (String v : vals) if (opp.getInternshipID() != null && opp.getInternshipID().equalsIgnoreCase(v)) matches = true;
                            break;
                        case "internshipTitle":
                            for (String v : vals) if (opp.getInternshipTitle() != null && opp.getInternshipTitle().equalsIgnoreCase(v)) matches = true;
                            break;
                        default:
                            matches = true;
                    }
                    if (!matches) return false;
                }
                return true;
            }).collect(Collectors.toList());
        }

        // Sorting
        if (filterType != null && !filterType.isEmpty()) {
            Comparator<InternshipOpportunity> cmp = null;
            switch (filterType) {
                case "title":
                    cmp = Comparator.comparing(InternshipOpportunity::getInternshipTitle, Comparator.nullsLast(String::compareToIgnoreCase));
                    break;
                case "companyName":
                    cmp = Comparator.comparing(InternshipOpportunity::getCompanyName, Comparator.nullsLast(String::compareToIgnoreCase));
                    break;
                case "openDate":
                    cmp = Comparator.comparing(InternshipOpportunity::getOpeningDate, Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                case "numberOfSlots":
                    cmp = Comparator.comparingInt(InternshipOpportunity::getNumOfSlots);
                    break;
                default:
            }
            if (cmp != null) {
                if (!ascending) cmp = cmp.reversed();
                Opplist.sort(cmp);
            }
        }

        List<String> out = new ArrayList<>();
        // Use a delimiter unlikely to appear in normal text to preserve bracketed majors: ' | '
        String DELIM = " | ";
        for (InternshipOpportunity opp : Opplist) {
            StringBuilder sb = new StringBuilder();
            sb.append("internshipID=").append(opp.getInternshipID() != null ? opp.getInternshipID() : "");
            sb.append(DELIM).append("internshipTitle=").append(opp.getInternshipTitle() != null ? opp.getInternshipTitle() : "");
            sb.append(DELIM).append("internshipLevel=").append(opp.getInternshipLevel() != null ? opp.getInternshipLevel() : "");
            sb.append(DELIM).append("companyName=").append(opp.getCompanyName() != null ? opp.getCompanyName() : "");
            sb.append(DELIM).append("preferredMajors=").append(formatPreferredMajorsForDisplay(opp.getPreferredMajors()));
            sb.append(DELIM).append("status=").append(opp.getStatus() != null ? opp.getStatus() : "");
            out.add(sb.toString());
        }
            return out;
        }
    /**
     * Return whether the currently logged-in user (if a student) can apply to the given internship ID.
     * Encapsulates auth checks and requirement checks so the UI doesn't need to access entities.
     */
    public boolean canCurrentLoggedInStudentApply(String internshipID) {
        try {
            if (authCtrl == null || !authCtrl.isLoggedIn() || !"Student".equals(authCtrl.getUserIdentity())) {
                return false;
            }
            String sid = authCtrl.getUserID();
            // Check: visibility, slots, status, student requirements, AND date range
            return isVisibleAndNotFullAndNotRejected(internshipID) 
                && studentFitsRequirements(sid, internshipID)
                && isInternshipOpen(internshipID);
        } catch (Exception e) {
            // On any error, be conservative and return false
            return false;
        }
    }
    
    /**
     * Check if the internship is currently open based on opening and closing dates.
     * Returns true if today's date is between opening date (inclusive) and closing date (inclusive).
     */
    private boolean isInternshipOpen(String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) return false;
        
        Date now = new Date();
        Date openDate = opp.getOpeningDate();
        Date closeDate = opp.getCloseDate();
        
        if (openDate == null || closeDate == null) return true; // If dates not set, allow application
        
        // Strip time component for date-only comparison
        // Set all times to start of day (00:00:00) for fair comparison
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String nowStr = sdf.format(now);
            String openStr = sdf.format(openDate);
            String closeStr = sdf.format(closeDate);
            
            Date nowDateOnly = sdf.parse(nowStr);
            Date openDateOnly = sdf.parse(openStr);
            Date closeDateOnly = sdf.parse(closeStr);
            
            // Check if now is within [openDate, closeDate] inclusive
            return !nowDateOnly.before(openDateOnly) && !nowDateOnly.after(closeDateOnly);
        } catch (ParseException e) {
            // If parsing fails, fall back to original comparison
            return !now.before(openDate) && !now.after(closeDate);
        }
    }
    
    /**
     * Returns the logged-in student's year of study (used for eligibility filtering).
     * Year 3/4 students are eligible for Intermediate and Advanced internships,
     * while Year 1/2 students can only apply to Basic level internships.
     * Uses proper CSV parsing to handle fields with special characters.
     * 
     * @return the student's year (1-4) or null if user is not a student or not found
     */
    public Integer getLoggedInStudentYear() {
        if (authCtrl == null || !authCtrl.isLoggedIn() || !"Student".equals(authCtrl.getUserIdentity())) {
            return null;
        }
        String studentID = authCtrl.getUserID();
        try (BufferedReader br = new BufferedReader(new FileReader("Code/Backend/Lib/student.csv"))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Use proper CSV parsing that respects quoted fields
                String[] vals = ControlUtils.splitCsvLine(line);
                if (vals.length > 4) {
                    String id = ControlUtils.unescapeCsvField(vals[0]);
                    if (id.equals(studentID)) {
                        try { 
                            return Integer.parseInt(ControlUtils.unescapeCsvField(vals[4])); 
                        } catch (NumberFormatException ex) { 
                            return null; 
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            // swallow and return null to avoid UI crash; UI will assume early year
        }
        return null;
    }
}
