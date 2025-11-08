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
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileWriter;

import Entity.InternshipOpportunity;
import Entity.Users.Student;


public class InternshipControl{
    private List<InternshipOpportunity> internshipOpportunities = new ArrayList<InternshipOpportunity>();
    private List<String> pendingInternshipOppID = null;
    private List<InternshipOpportunity> companyRepsInternshipOpps = null;
    private List<Application> applicationForCurrentInternship = null;
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
        //implementation
        return null;
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
        Date openDate, Date closeDate, int numberOfSlots) {
        if (!authCtrl.isLoggedIn()) {
            System.out.println("User not logged in.");
            return;
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
        List<InternshipOpportunity> companyRepsInternshipOpps = getInternshipsByCompanyRepID(companyRepID);
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
        String companyRepID = authCtrl.getUserID();
        InternshipOpportunity opp = getInternshipByID(internshipID);
        if (opp == null || !opp.getCompanyRepInChargeID().equals(companyRepID)) {
            System.out.println("Invalid internship ID or you do not have permission to view this internship.");
            return;
        }
        // implementation
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
                String major = values[3];
                boolean hasAcceptedInternshipOpportunity = Boolean.parseBoolean(values[5]);
                int year = Integer.parseInt(values[4]);
                student = new Student(studentID, name, email, major, year, hasAcceptedInternshipOpportunity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isVisibleAndNotFull(String oppID) {
        InternshipOpportunity opp = getInternshipByID(oppID);
        if (opp != null) {
            if (opp.getVisibility() && opp.getNumOfSlots() > 0) {
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
                if (!preferredMajors.contains(student.getMajor())) {
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
    // =========================================================
    // Career Staff methods

    public List<InternshipOpportunity> getPendingInternshipOpportunities() {
        //implementation
        return null;
    }
    public List<InternshipOpportunity> getAllInternshipOpportunities(){ //for report
        //implementation, excluding rejected internships.
        return null;
    } 
    public void approveInternshipCreation(InternshipOpportunity opp) {
        //implementation
    }
    public void rejectInternshipCreation(InternshipOpportunity rejectInternshipCreation) {
        //implementation
    }
    public void reject(InternshipOpportunity opp) {
        //
    }
    public void changeVisibility(InternshipOpportunity opp) {
        //
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
    // for Company Rep
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
    
    //TODO: To use: for Staff
    private void initializePendingInternshipOppList() {
        pendingInternshipOppID = new ArrayList<>();
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getStatus().equals("pending")) {
                pendingInternshipOppID.add(opp.getInternshipID());
            }
        }
    }
    private void removeOpportunityFromPendingList(String oppID) {
        if (oppID == null) {
            System.out.println("Pending internship ID is empty.");
            return;
        }
        pendingInternshipOppID.remove(oppID);
    }  
}
