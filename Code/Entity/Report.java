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
                Map<String,Integer> companyAnd=new HashMap<String,Integer>();


                for (int i=0;i<internshipOpportunities.size();i++){
                    List<Object> details=internshipOpportunities.get(i).getDetails();
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

