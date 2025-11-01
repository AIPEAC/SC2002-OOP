package Entity;
import java.util.List;

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
                //
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

