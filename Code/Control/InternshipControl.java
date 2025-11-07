package Control;
import Entity.InternshipOpportunity;
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

public class InternshipControl {
    private List<InternshipOpportunity> internshipOpportunities = new ArrayList<InternshipOpportunity>();
    private List<String> pendingInternshipOppID = new ArrayList<String>();
    private AuthenticationControl authCtrl;

    //when initialize the internships. read from csv.
    //the last column will be several int seperated by spaces.
    //read those int and initialize the internships with List<application>, using the int get from the csv.

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
    public List<Object> getInternshiDetails(InternshipOpportunity opp) {
        if (authCtrl.isLoggedIn()){
            //implementation
            return opp.getDetailsForViewing();
        }
        System.out.println("Please login to view internship details.");
        return null;
    }

    
    
    //=========================================================
    // Career Staff methods


    public void requestCreateInternshipOpportunity(int internshipID, String internshipTitle, String description, String internshipLevel, List<String> preferredMajors, Date openDate, Date closeDate, String companyName, String companyRepInChargeID, int numberOfSlots) {
        //implementation
    }

    public List<InternshipOpportunity> getInternshipOpportunities(String oppID) {
        //implementation
        return null;
    }

    public List<InternshipOpportunity> getPendingInternshipOpportunities() {
        //implementation
        return null;
    }

    

    public List<InternshipOpportunity> getAllInternshipOpportunities(){
        //implementation
        return null;
    }
    
    

    public List<Application> getInternshipStatus(InternshipOpportunity opp) {
        //implementation
        return null;
    }

    public void approveInternshipCreation(InternshipOpportunity opp) {
        //implementation
    }

    public void rejectInternshipCreation(InternshipOpportunity rejectInternshipCreation) {
        //implementation
    }

    public void approve(Application app) {
        //implementation
    }

    public void withdrawEveryOtherApplication(String studentID) {
        //
    }

    public void reject(Application app) {
        //
    }

    public void reject(InternshipOpportunity opp) {
        //
    }

    public void changeVisibility(InternshipOpportunity opp) {
        //
    }

    public void addOpportunityToPendingList(InternshipOpportunity opp) {
        //
    }

    public void removeOpportunityToPendingList(InternshipOpportunity opp) {
        //
    }

    
}
