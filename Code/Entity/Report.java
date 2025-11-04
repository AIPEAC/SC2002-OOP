package Entity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
            }
        }else{
            if (internshipOpportunities.size()==0){
                System.out.println("No internship opportunities found after filtering, try another filter.");
            }else{
                
            }
        }
    }

    public void saveToLocal(){
        // Save the report to a local file
    }
    
    private void statistifyTheNumbers(List<InternshipOpportunity> internshipOpportunities){
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
        System.out.println("Total number of internships (within the filter contraint): "+numOfInternships);
        System.out.println("Number of Advanced internships: "+numOfAdvancedInternships);
        System.out.println("Number of Intermediate internships: "+numOfIntermediateInternships);
        System.out.println("Number of Basic internships: "+numOfBasicInternships);
        System.out.println("Number of Visible internships: "+numOfVisibleInternships);
        System.out.println("Number of Full internships: "+numOfFullInternships);
        System.out.println("Total number of slots across all internships: "+numOfTotalSlots);
        System.out.println("Majors and their number of internships: ");
        for (String major:majorsAndInternship.keySet()){
            System.out.println(major+": "+majorsAndInternship.get(major));
        }
        System.out.println("Companies and their number of internships: ");
        for (String company:companyAndTheirNumOfInternships.keySet()){
            System.out.println(company+": "+companyAndTheirNumOfInternships.get(company));
        }
    }
}

