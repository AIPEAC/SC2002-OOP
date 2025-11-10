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
                String[] values = line.split(",");
                String internshipID = values.length > 0 ? values[0] : "";
                String title = values.length > 1 ? values[1] : "";
                String description = values.length > 2 ? values[2] : "";
                String level = values.length > 3 ? values[3] : "";
                String pmRaw = values.length > 4 ? values[4] : "";
                List<String> preferredMajors = parsePreferredMajorsRaw(pmRaw);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String openStr = values.length > 5 ? values[5] : "";
                String closeStr = values.length > 6 ? values[6] : "";
                Date openingDate = (openStr != null && !openStr.trim().isEmpty()) ? sdf.parse(openStr.trim()) : new Date();
                Date closeDate = (closeStr != null && !closeStr.trim().isEmpty()) ? sdf.parse(closeStr.trim()) : new Date();
                String status = values.length > 7 ? values[7] : "pending";
                String companyName = values.length > 8 ? values[8] : "";
                String companyRepInChargeID = values.length > 9 ? values[9] : "";
                int numberOfSlots = 0;
                try {
                    numberOfSlots = values.length > 10 && values[10] != null && !values[10].trim().isEmpty()
                            ? Integer.parseInt(values[10].trim()) : 0;
                } catch (NumberFormatException nfe) {
                    numberOfSlots = 0;
                }
                // applicationNumberList is space-separated integers
                List<Integer> applicationNumberList = new ArrayList<>();
                if (values.length > 11 && values[11] != null && !values[11].trim().isEmpty()) {
                    applicationNumberList = Arrays.stream(values[11].trim().split("\\s+"))
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                }
                // acceptedApplicantNumbers is space-separated integers
                List<Integer> acceptedApplicantNumbers = new ArrayList<>();
                if (values.length > 12 && values[12] != null && !values[12].trim().isEmpty()) {
                    acceptedApplicantNumbers = Arrays.stream(values[12].trim().split("\\s+"))
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                }
                boolean visibility = false;
                if (values.length > 13 && values[13] != null && !values[13].trim().isEmpty()) {
                    visibility = Boolean.parseBoolean(values[13].trim());
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
        // Check if internship is already full before approving
        if (opp.isFull()) {
            throw new IllegalStateException("Cannot approve application - this internship is already full (" + 
                opp.getAcceptedApplicationNumbers().size() + "/" + opp.getNumOfSlots() + " slots filled).");
        }
        if (appCtrl != null) {
            appCtrl.approveApplicationByNumber(applicationNumber);
            
            // Check if internship is now full after this approval
            String fullMessage = checkAndRejectIfFull(opp);
            if (fullMessage != null) {
                return "Approved application: " + applicationNumber + "\n\n" + fullMessage;
            }
            return "Approved application: " + applicationNumber;
        } else {
            throw new IllegalStateException("ApplicationControl not set; cannot approve application.");
        }
    }
    
    /**
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
                    return "⚠ This internship is now FULL (" + numAccepted + "/" + numSlots + " slots filled).\n" + rejectedCount + " other pending application(s) have been automatically rejected.";
                }
            }
            // If no pending apps to reject, just notify that it's full
            return "⚠ This internship is now FULL (" + numAccepted + "/" + numSlots + " slots filled).";
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
    
    /** Approve an application: mark application approved and add to internship accepted list. */
    void approveApplicationNumberForInternship(int applicationNumber, String internshipID) {
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null) throw new IllegalArgumentException("Internship not found: " + internshipID);
        // Note: Don't check isFull() here because this is called after approval decision is made
        // The full check happens in approveApplicationForInternship before calling this
        // move application number from pending to accepted list
        opp.approveApplicationNumber(applicationNumber);
        updateInternshipInDB();
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
                if (student.getMajors() != null) {
                    for (String m : student.getMajors()) {
                        if (preferredMajors.contains(m)) {
                            anyMatch = true;
                            break;
                        }
                    }
                }
                if (!anyMatch) {
                    // Student does not meet preferred majors. Backend does not emit UI messages.
                }
                return true;
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
    void withdrawEveryOtherApplication(String studentID) {
        appCtrl.withdrawOtherApplicationsOfApprovedStudent(studentID);
        updateInternshipInDB();
    }
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
                String line = String.join(",",
                    opp.getInternshipID(),
                    opp.getInternshipTitle(),
                    opp.getDescription(),
                    opp.getInternshipLevel(),
                    (opp.getPreferredMajors() != null ? String.join(";", opp.getPreferredMajors()) : ""),
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
        String filePath = "Code/Backend/Lib/internship_opportunity_list.csv";
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

    /** Parse a raw preferred majors field from CSV into a cleaned List<String>.
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
     */
    public List<String> getApprovedVisibleInternshipOpportunitiesForDisplay(String filterType, boolean ascending, Map<String, List<String>> filterIn) {
        List<InternshipOpportunity> Opplist = getAllVisibleInternshipOpportunities().stream()
                .filter(opp -> "approved".equalsIgnoreCase(opp.getStatus()))
                .collect(Collectors.toList());
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
    /** Return logged-in student year (3/4 gating logic for level filter) or null if not a student / not found. */
    public Integer getLoggedInStudentYear() {
        if (authCtrl == null || !authCtrl.isLoggedIn() || !"Student".equals(authCtrl.getUserIdentity())) {
            return null;
        }
        String studentID = authCtrl.getUserID();
        try (BufferedReader br = new BufferedReader(new FileReader("Code/Backend/Lib/student.csv"))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(",");
                if (vals.length > 4 && vals[0].equals(studentID)) {
                    try { return Integer.parseInt(vals[4]); } catch (NumberFormatException ex) { return null; }
                }
            }
        } catch (IOException ioe) {
            // swallow and return null to avoid UI crash; UI will assume early year
        }
        return null;
    }
}
