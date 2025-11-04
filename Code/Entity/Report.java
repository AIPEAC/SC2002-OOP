package Entity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Report {
    private int reportIndex;
    private List<InternshipOpportunity> internshipOpportunities;
    private boolean filtered;

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
                int numOfInternships=internshipOpportunities.size();
                int numOfVisibleInternships=0;
                int numOfFullInternships=0;
                int numOfAdvancedInternships=0;
                int numOfIntermediateInternships=0;
                int numOfBasicInternships=0;
                Map<String,Integer> majorsAndInternship=new HashMap<String,Integer>();
                Map<String,Integer> companyAndTheirNumOfInternships=new HashMap<String,Integer>();


                for (int i=0;i<internshipOpportunities.size();i++){
                    List<Object> details=internshipOpportunities.get(i).getDetails();
                    if ((details.get(1)).equals("Advanced")){
                        numOfAdvancedInternships++;
                    }else if((details.get(1)).equals("Intermediate")){
                        numOfIntermediateInternships++;
                    }else if((details.get(1)).equals("Basic")){
                        numOfBasicInternships++;
                    }else{
                        System.out.println("Error in internship level, check the entity code for InternshipOpportunity.");
                    }
                    
                    if ((boolean)details.get(7)){
                        numOfVisibleInternships++;
                    }
                    if ((boolean)details.get(8)){
                        numOfFullInternships++;
                    }

                        
                        
                }
            }
        }else{
            if (internshipOpportunities.size()==0){
                System.out.println("No internship opportunities found after filtering, try another filter.");
            }else{
                //
            }
        }
    }

    public void saveToLocal(){
        //
    }
    
}

