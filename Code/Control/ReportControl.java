package Control;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import Entity.InternshipOpportunity;
import Entity.Report;

/**
 * Controls report generation and management for career center staff.
 * Generates comprehensive reports on internship opportunities with various filters
 * including status, major, level, company, and date ranges.
 * Reports can be saved to CSV files for documentation and analysis.
 * 
 * @author Allen
 * @version 1.0
 */
public class ReportControl {
    /** Authentication controller for verifying staff permissions */
    private AuthenticationControl authCtrl;
    
    /** Internship controller for accessing internship data */
    private InternshipControl intCtrl;
    
    /** Cached list of all internship opportunities */
    private List<InternshipOpportunity> allOpplist;

    /**
     * Constructs a ReportControl with required dependencies.
     * 
     * @param authCtrl the authentication controller
     * @param intCtrl the internship controller for accessing opportunity data
     */
    ReportControl(AuthenticationControl authCtrl, InternshipControl intCtrl){
        this.authCtrl=authCtrl;
        this.intCtrl=intCtrl;
        // Initialize after intCtrl is assigned to avoid NPE
        this.allOpplist = (this.intCtrl == null) ? new ArrayList<>() : this.intCtrl.getAllInternshipOpportunities();
    }
    public List<String> generateReportOverview(boolean optToSaveReport){
        if (!authCtrl.isLoggedIn()) throw new IllegalStateException("You are not logged in.");
        if (!authCtrl.getUserIdentity().equals("CareerStaff")) throw new IllegalStateException("You do not have the permission to generate reports.");
        int reportIndex = optToSaveReport ? getNumberOfReports()+1 : 0;
        boolean filtered=false;
        Report report=new Report(reportIndex,allOpplist,filtered);
        List<String> lines = report.formatOutput();
        if (optToSaveReport) {
            try {
                String path = report.saveToLocal();
                lines.add("Saved report to: " + path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save report: " + e.getMessage());
            }
        }
        return lines;
    }
    public List<String> generateReportSpecific(boolean optToSaveReport,Map<String,List<String>> filterIn){
        if (!authCtrl.isLoggedIn()) throw new IllegalStateException("You are not logged in.");
        if (!authCtrl.getUserIdentity().equals("CareerStaff")) throw new IllegalStateException("You do not have the permission to generate reports.");
        int reportIndex = optToSaveReport ? getNumberOfReports()+1 : 0;
        List<InternshipOpportunity> filteredList=comprehensive(allOpplist,filterIn);
        boolean filtered=true;
        Report report=new Report(reportIndex,filteredList,filtered,filterIn);
        List<String> lines = report.formatOutput();
        if (optToSaveReport) {
            try {
                String path = report.saveToLocal();
                lines.add("Saved report to: " + path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save report: " + e.getMessage());
            }
        }
        return lines;
    }
    
    private List<InternshipOpportunity> comprehensive(List<InternshipOpportunity> internshipOpportunities,
                Map<String,List<String>> filterIn){
        /*
         * Filters in internship opportunities based on the provided criteria.
         * 
         * the maps first key is a String, the sequence is fixed as follows:
         * "Major", "Level", "CompanyName", "StartDate"
         * 
         * eg: filterIn = {
         * "Major": ["Computer Science,Business"],  //OR condition within the same key
         * "Level": ["Basic,Intermediate"],
         * "CompanyName": ["Google,Facebook"],
         * "StartDate": ["2023-01-01"]; denoting internships opening from 1st Jan 2023
         * }
         */
        // If no filters provided, return the original list
        if (filterIn == null || filterIn.isEmpty()) {
            return internshipOpportunities;
        }

        List<InternshipOpportunity> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (InternshipOpportunity opp : internshipOpportunities) {
            List<Object> details = opp.getDetailsForReport();

            // details indices as documented in InternshipOpportunity.getDetails():
            // 0 - internshipID, 1 - internshipLevel, 2 - preferredMajors (List<String>),
            // 3 - openingDate (Date), 4 - closeDate, 5 - companyName, ...
            String level = (String) details.get(1);
            @SuppressWarnings("unchecked")
            List<String> majors = (List<String>) details.get(2);
            Date openingDate = (Date) details.get(3);
            String companyName = (String) details.get(5);

            boolean matches = true;

            // Helper to build a set of accepted tokens (case-insensitive)
            Function<List<String>, Set<String>> buildSet = (lst) -> {
                Set<String> s = new HashSet<>();
                if (lst == null) return s;
                for (String item : lst) {
                    if (item == null) continue;
                    String[] parts = item.split(",");
                    for (String p : parts) {
                        String t = p.trim();
                        if (!t.isEmpty()) s.add(t.toLowerCase());
                    }
                }
                return s;
            };

            // Major filter
            if (filterIn.containsKey("Major")) {
                Set<String> acceptedMajors = buildSet.apply(filterIn.get("Major"));
                boolean anyMajorMatch = false;
                if (majors != null && !majors.isEmpty()) {
                    for (String m : majors) {
                        if (m != null && acceptedMajors.contains(m.toLowerCase())) {
                            anyMajorMatch = true;
                            break;
                        }
                    }
                }
                if (!anyMajorMatch) matches = false;
            }

            // Level filter
            if (matches && filterIn.containsKey("Level")) {
                Set<String> acceptedLevels = buildSet.apply(filterIn.get("Level"));
                String lvl = level == null ? "" : level.toLowerCase();
                if (!acceptedLevels.contains(lvl)) matches = false;
            }

            // CompanyName filter
            if (matches && filterIn.containsKey("CompanyName")) {
                Set<String> acceptedCompanies = buildSet.apply(filterIn.get("CompanyName"));
                String comp = companyName == null ? "" : companyName.toLowerCase();
                if (!acceptedCompanies.contains(comp)) matches = false;
            }

            // StartDate filter: keep internships opening on or after given date(s)
            if (matches && filterIn.containsKey("StartDate")) {
                boolean anyDateOk = false;
                List<String> dateFilters = filterIn.get("StartDate");
                if (dateFilters != null && !dateFilters.isEmpty()) {
                    for (String raw : dateFilters) {
                        if (raw == null) continue;
                        String[] parts = raw.split(",");
                        for (String p : parts) {
                            String ds = p.trim();
                            if (ds.isEmpty()) continue;
                            try {
                                Date filterDate = sdf.parse(ds);
                                if (openingDate != null && !openingDate.before(filterDate)) {
                                    anyDateOk = true;
                                    break;
                                }
                            } catch (ParseException e) {
                                // ignore invalid filter date token
                            }
                        }
                        if (anyDateOk) break;
                    }
                }
                if (!anyDateOk) matches = false;
            }

            if (matches) result.add(opp);
        }

        return result;
    }

    private int getNumberOfReports(){
        // Count files in the Output_report directory matching Report####.md
        try {
            File dir = new File("Output_report");
            if (!dir.exists() || !dir.isDirectory()) return 0;
            String[] files = dir.list((d, name) -> name.matches("Report\\d{4}\\.md"));
            return files == null ? 0 : files.length;
        } catch (SecurityException e) {
            // If we cannot access the directory, treat as zero
            return 0;
        }
    }
    
    /** Get list of unique company names from all internship opportunities */
    public List<String> getAllCompanyNames() {
        Set<String> companies = new HashSet<>();
        for (InternshipOpportunity opp : allOpplist) {
            List<Object> details = opp.getDetailsForReport();
            if (details.size() > 5) {
                String companyName = (String) details.get(5);
                if (companyName != null && !companyName.trim().isEmpty()) {
                    companies.add(companyName);
                }
            }
        }
        List<String> sortedCompanies = new ArrayList<>(companies);
        sortedCompanies.sort(String::compareTo);
        return sortedCompanies;
    }
    
    /** Get list of unique majors from all internship opportunities */
    public List<String> getAllMajors() {
        Set<String> majors = new HashSet<>();
        for (InternshipOpportunity opp : allOpplist) {
            List<Object> details = opp.getDetailsForReport();
            if (details.size() > 2) {
                @SuppressWarnings("unchecked")
                List<String> oppMajors = (List<String>) details.get(2);
                if (oppMajors != null) {
                    for (String major : oppMajors) {
                        if (major != null && !major.trim().isEmpty()) {
                            majors.add(major);
                        }
                    }
                }
            }
        }
        List<String> sortedMajors = new ArrayList<>(majors);
        sortedMajors.sort(String::compareTo);
        return sortedMajors;
    }
    
}
