package Entity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Report {
    private int reportIndex;
    private List<InternshipOpportunity> internshipOpportunities;
    private boolean filtered;
    private int numOfInternships=0;

    private int numOfAdvancedInternships=0;
    private int numOfIntermediateInternships=0;
    private int numOfBasicInternships=0;

    private int numOfVisibleInternships=0;
    private int numOfFullInternships=0;
    private int numOfTotalSlots=0;
    
    private Map<String,Integer> majorsAndInternship=new HashMap<String,Integer>();
    private Map<String,Integer> companyAndTheirNumOfInternships=new HashMap<String,Integer>();

    public Report(int reportIndex,List<InternshipOpportunity> internshipOpportunities,boolean filtered) {
        this.reportIndex = reportIndex;
        this.internshipOpportunities = internshipOpportunities;
        this.filtered=filtered;
    }
    
    public void formatOutput(){
        if (!filtered){
            if (internshipOpportunities.size()==0){
                System.out.println("No internship opportunities found.");
            }else{
                statistifyTheNumbers(internshipOpportunities);
                printStatistics();
            }
        }else{
            if (internshipOpportunities.size()==0){
                System.out.println("No internship opportunities found after filtering, try another filter.");
            }else{
                statistifyTheNumbers(internshipOpportunities);
                printStatistics();
            }
        }
    }

    public void saveToLocal(){
        // Ensure statistics are up-to-date (statistify resets counts)
        if (internshipOpportunities==null || internshipOpportunities.size()==0){
            System.out.println("No internships to save in report.");
            return;
        }
        statistifyTheNumbers(internshipOpportunities);

        String md = buildMarkdownReport();

        // Try to save into Output_report directory (relative to working directory)
        File dir = new File("Output_report");
        if (!dir.exists()){
            dir.mkdirs();
        }
    // Use zero-padded 4-digit filename like Report0001.md
    String filename = String.format("Report%04d.md", reportIndex);
        File out = new File(dir, filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))){
            bw.write(md);
            System.out.println("Saved report to: "+out.getPath());
        } catch (IOException e){
            System.out.println("Failed to save report: "+e.getMessage());
        }
    }
    
    private void statistifyTheNumbers(List<InternshipOpportunity> internshipOpportunities){
        // Reset and calculate statistics (safe to call multiple times)
        numOfInternships=0;

        numOfAdvancedInternships=0;
        numOfIntermediateInternships=0;
        numOfBasicInternships=0;

        numOfVisibleInternships=0;
        numOfFullInternships=0;
        numOfTotalSlots=0;

        majorsAndInternship.clear();
        companyAndTheirNumOfInternships.clear();

        //calculate statistics
        numOfInternships=internshipOpportunities.size();

        //calculate number of internships by level and visibility/fullness
        for (int i=0;i<internshipOpportunities.size();i++){
            List<Object> details=internshipOpportunities.get(i).getDetails();

            //numbers for each level
            if ((details.get(1)).equals("Advanced")){
                numOfAdvancedInternships++;
            }else if((details.get(1)).equals("Intermediate")){
                numOfIntermediateInternships++;
            }else if((details.get(1)).equals("Basic")){
                numOfBasicInternships++;
            }else{
                System.out.println("Error in internship level, check the entity code for InternshipOpportunity.");
            }
            
            //numbers for visibable internships
            if ((boolean)details.get(7)){
                numOfVisibleInternships++;
            }

            //numbers for full internships
            if ((boolean)details.get(8)){
                numOfFullInternships++;
            }

            //number of total slots
            numOfTotalSlots+= (int) details.get(6);

            //majors and their number of internships
            @SuppressWarnings("unchecked")
            List<String> preferredMajors=(List<String>) details.get(2);
            for (int j=0;j<preferredMajors.size();j++){
                String major=preferredMajors.get(j);
                if (majorsAndInternship.containsKey(major)){
                    majorsAndInternship.put(major,majorsAndInternship.get(major)+1);
                }else{
                    majorsAndInternship.put(major,1);
                }
            }
            //company and their number of internships
            String companyName=(String) details.get(5);
            if (companyAndTheirNumOfInternships.containsKey(companyName)){
                companyAndTheirNumOfInternships.put(companyName,companyAndTheirNumOfInternships.get(companyName)+1);
            }else{
                companyAndTheirNumOfInternships.put(companyName,1);
            }
        }
    }

    private void printStatistics(){
        // Punchy header
        System.out.println("========================================");
        System.out.println("🔥 PREVALENT INTERNSHIP REPORT - #"+reportIndex);
        System.out.println("(Showing "+numOfInternships+" internships"+(filtered?" after filters":"")+")");
        System.out.println("========================================");

        // Overall counts with simple distribution bars
        int maxForBars = Math.max(1, numOfInternships);
        System.out.println("Levels:");
        printLabeledCount("Advanced", numOfAdvancedInternships, maxForBars);
        printLabeledCount("Intermediate", numOfIntermediateInternships, maxForBars);
        printLabeledCount("Basic", numOfBasicInternships, maxForBars);
        System.out.println();

        System.out.println("Visibility & Fullness:");
        printLabeledCount("Visible", numOfVisibleInternships, maxForBars);
        printLabeledCount("Full", numOfFullInternships, maxForBars);
        System.out.println();

        System.out.println("Total slots across all internships: "+numOfTotalSlots);
        System.out.println();

        // Majors: sorted by count desc
        System.out.println("Top majors by number of internships:");
        List<Entry<String,Integer>> majorsSorted = sortMapByValueDesc(majorsAndInternship);
        if (majorsSorted.isEmpty()){
            System.out.println(" (none)");
        } else {
            int topN = Math.min(10, majorsSorted.size());
            int maxMajorCount = majorsSorted.get(0).getValue();
            for (int i=0;i<topN;i++){
                Entry<String,Integer> e = majorsSorted.get(i);
                printLabeledCount(e.getKey(), e.getValue(), Math.max(1, maxMajorCount));
            }
            if (majorsSorted.size()>topN){
                System.out.println("...and "+(majorsSorted.size()-topN)+" more majors.");
            }
        }
        System.out.println();

        // Companies: sorted by count desc and show top 5 prominently
        System.out.println("Top companies by number of internships:");
        List<Entry<String,Integer>> companiesSorted = sortMapByValueDesc(companyAndTheirNumOfInternships);
        if (companiesSorted.isEmpty()){
            System.out.println(" (none)");
        } else {
            int topCompanies = Math.min(5, companiesSorted.size());
            int maxCompanyCount = companiesSorted.get(0).getValue();
            for (int i=0;i<topCompanies;i++){
                Entry<String,Integer> e = companiesSorted.get(i);
                System.out.print((i+1)+". ");
                printLabeledCount(e.getKey(), e.getValue(), Math.max(1, maxCompanyCount));
            }
            if (companiesSorted.size()>topCompanies){
                System.out.println("...and "+(companiesSorted.size()-topCompanies)+" more companies.");
            }
        }

        System.out.println("========================================");
    }

    // Helper: print a label, raw count, percentage and a small ASCII bar
    private void printLabeledCount(String label, int count, int maxForBars){
        double pct = (numOfInternships>0)?(count*100.0/numOfInternships):0.0;
        String pctStr = String.format("%.1f", pct);
        String bar = makeBar(count, maxForBars, 30);
        System.out.println(String.format("%-20s %4d  (%5s%%) %s", label, count, pctStr, bar));
    }
    private String makeBar(int value, int max, int width){
        if (max<=0) max=1;
        int filled = (int) Math.round((double)value / max * width);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<filled;i++) sb.append('█');
        for (int i=filled;i<width;i++) sb.append(' ');
        return "|"+sb.toString()+"|";
    }
    private List<Entry<String,Integer>> sortMapByValueDesc(Map<String,Integer> map){
        List<Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Entry<String,Integer>>(){
            public int compare(Entry<String,Integer> a, Entry<String,Integer> b){
                return b.getValue().compareTo(a.getValue());
            }
        });
        return list;
    }


    // Build a Markdown-formatted report string
    private String buildMarkdownReport(){
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        sb.append("# 🔥 PREVALENT INTERNSHIP REPORT - #"+reportIndex+"\n\n");
        sb.append("_Generated: "+LocalDateTime.now().format(dtf)+"_\n\n");
        sb.append("**Total internships:** "+numOfInternships+(filtered?" (filtered)":"")+"\n\n");

        int maxForBars = Math.max(1, numOfInternships);

        sb.append("## Levels\n\n");
        sb.append("| Level | Count | % | Bar |\n");
        sb.append("|---|---:|---:|---|\n");
        sb.append(tableRowMd("Advanced", numOfAdvancedInternships, maxForBars, 20));
        sb.append(tableRowMd("Intermediate", numOfIntermediateInternships, maxForBars, 20));
        sb.append(tableRowMd("Basic", numOfBasicInternships, maxForBars, 20));
        sb.append("\n");

        sb.append("## Visibility & Fullness\n\n");
        sb.append("| Metric | Count | % | Bar |\n");
        sb.append("|---|---:|---:|---|\n");
        sb.append(tableRowMd("Visible", numOfVisibleInternships, maxForBars, 20));
        sb.append(tableRowMd("Full", numOfFullInternships, maxForBars, 20));
        sb.append("\n");

        sb.append("**Total slots across all internships:** "+numOfTotalSlots+"\n\n");

        sb.append("## Top Majors\n\n");
        List<Entry<String,Integer>> majorsSorted = sortMapByValueDesc(majorsAndInternship);
        if (majorsSorted.isEmpty()){
            sb.append("(none)\n\n");
        } else {
            sb.append("| Major | Count | % | Bar |\n");
            sb.append("|---|---:|---:|---|\n");
            int topN = Math.min(10, majorsSorted.size());
            int maxMajor = majorsSorted.get(0).getValue();
            for (int i=0;i<topN;i++){
                Entry<String,Integer> e = majorsSorted.get(i);
                sb.append(tableRowMd(e.getKey(), e.getValue(), Math.max(1, maxMajor), 20));
            }
            if (majorsSorted.size()>topN) sb.append("...and "+(majorsSorted.size()-topN)+" more majors.\n\n");
            else sb.append("\n");
        }

        sb.append("## Top Companies\n\n");
        List<Entry<String,Integer>> companiesSorted = sortMapByValueDesc(companyAndTheirNumOfInternships);
        if (companiesSorted.isEmpty()){
            sb.append("(none)\n\n");
        } else {
            sb.append("| Company | Count | % | Bar |\n");
            sb.append("|---|---:|---:|---|\n");
            int topC = Math.min(5, companiesSorted.size());
            int maxComp = companiesSorted.get(0).getValue();
            for (int i=0;i<topC;i++){
                Entry<String,Integer> e = companiesSorted.get(i);
                sb.append(tableRowMd((i+1)+". "+e.getKey(), e.getValue(), Math.max(1, maxComp), 20));
            }
            if (companiesSorted.size()>topC) sb.append("...and "+(companiesSorted.size()-topC)+" more companies.\n\n");
            else sb.append("\n");
        }

        sb.append("---\n\n");
        sb.append("*Generated by the internship reporting system.*\n");
        return sb.toString();
    }
    private String tableRowMd(String label, int count, int maxForBars, int width){
        double pct = (numOfInternships>0)?(count*100.0/numOfInternships):0.0;
        String pctStr = String.format("%.1f", pct);
        String bar = makeBar(count, maxForBars, width);
        // Wrap bar in backticks to preserve monospace in Markdown
        return String.format("| %s | %d | %s%% | `%s` |\n", escapeMd(label), count, pctStr, bar);
    }
    private String escapeMd(String s){
        if (s==null) return "";
        return s.replace("|","\\|");
    }
}

