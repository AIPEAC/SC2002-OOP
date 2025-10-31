package Entity;
import java.util.List;

public class Report {
    private int reportIndex;
    private List<InternshipOpportunity> internshipOpportunities;

    public Report(int reportIndex,List<InternshipOpportunity> internshipOpportunities) {
        this.reportIndex = reportIndex;
        this.internshipOpportunities = internshipOpportunities;
    }
    public void formatOutput(){
        List<String> comprehensiveDataList=comprehensiveDataList(internshipOpportunities);

    }
    private List<String> comprehensiveDataList(List<InternshipOpportunity> internshipOpportunities){
        return null;
    }
}
