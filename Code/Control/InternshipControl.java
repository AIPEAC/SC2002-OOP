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
    private List<String> pendingInternshipOppID = new ArrayList<String>();
    private AuthenticationControl authCtrl;
    private Student student=null;

    public InternshipControl(AuthenticationControl authCtrl) {
        this.authCtrl = authCtrl;
        loadInternshipOpportunityFromDB();
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
    public List<InternshipOpportunity> getInternshipOpportunities(String oppID) {
        //implementation
        return null;
    }

    // =========================================================
    // Company Rep methods

    public void requestCreateInternshipOpportunity(int internshipID, 
        String internshipTitle, String description, 
        String internshipLevel, List<String> preferredMajors, 
        Date openDate, Date closeDate, String companyName, 
        String companyRepInChargeID, int numberOfSlots) {
        //implementation
    }
    public List<Application> getInternshipStatus(String internshipID) {
        //implementation
        return null;
    }
    public void viewApplications(String internshipID) {
        //implementation
    }
    public void approve(Application app) {
        //implementation
    }

    public void reject(Application app) {
        //
    }

    // =========================================================
    // Student and Career Staff methods
    
    // =========================================================
    // Student methods
    private void loadStudentFromDB(String studentID) {
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
            if (!preferredMajors.contains(student.getMajor())) {
                System.out.println("Note: You do not meet the major preferences.");
            }
            return true;
        }
        return false; 
    }
    public void addApplicationNumberToInternshipOpportunity(InternshipOpportunity opp) {
        //implementation
    }
    public void withdrawEveryOtherApplication(String studentID) {
        //
    }

    // =========================================================
    // Career Staff methods

    public List<InternshipOpportunity> getPendingInternshipOpportunities() {
        //implementation
        return null;
    }
    public List<InternshipOpportunity> getAllInternshipOpportunities(){ //for report
        //implementation
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
    // Private Helpers
    private InternshipOpportunity getInternshipByID(String internshipID) {
        for (InternshipOpportunity opp : internshipOpportunities) {
            if (opp.getInternshipID().equals(internshipID)) {
                return opp;
            }
        }
        return null;
    }
    
    private void addOpportunityToPendingList(InternshipOpportunity opp) {
        //
    }
    private void removeOpportunityFromPendingList(InternshipOpportunity opp) {
        //
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
    }
}
