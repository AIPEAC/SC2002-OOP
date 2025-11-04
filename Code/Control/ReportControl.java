package Control;

import Entity.Report;
import Entity.InternshipOpportunity;
import java.util.List;


public class ReportControl {
    private AuthenticationControl authCtrl;
    private InternshipControl intCtrl;
    private List<InternshipOpportunity> allOpplist=intCtrl.getAllInternshipOpportunities();

    public ReportControl(AuthenticationControl authCtrl, InternshipControl intCtrl){
        this.authCtrl=authCtrl;
        this.intCtrl=intCtrl;
    }
    public void generateReportOverview(boolean optToSaveReport){
        if (authCtrl.isLoggedIn()){
            if (authCtrl.getUserIdentity().equals("Staff")){
                int reportIndex = optToSaveReport ? getNumberOfReports()+1 : 0;
                boolean filtered=false;

                Report report=new Report(reportIndex,allOpplist,filtered);
                report.formatOutput();
                if (optToSaveReport){
                    report.saveToLocal();
                    System.out.printf("Report saved in Lib_example/report %4d.txt\n",reportIndex);
                }
            }else{
                System.out.println("You do not have the permission to generate reports.");
            }
        }else{
            System.out.println("You are not logged in.");
        }
    }
    public void generateReportSpecific(boolean optToSaveReport,String[] filterOut){
        if (authCtrl.isLoggedIn()){
            if (authCtrl.getUserIdentity().equals("Staff")){
                int reportIndex = optToSaveReport ? getNumberOfReports()+1 : 0;
                List<InternshipOpportunity> filteredList=comprehensive(allOpplist,filterOut);
                boolean filtered=true;

                Report report=new Report(reportIndex,filteredList,filtered);
                report.formatOutput();

                if (optToSaveReport){
                    report.saveToLocal();
                    //System.out.printf("Report saved in Lib_example/report %4d.txt\n",reportIndex);
                    //print is done in report.saveToLocal()
                }
            }else{
                System.out.println("You do not have the permission to generate reports.");
            }
        }else{
            System.out.println("You are not logged in.");
        }
    }
    
    private List<InternshipOpportunity> comprehensive(List<InternshipOpportunity> internshipOpportunities,
                String[] filterOut){
        // Apply filtering logic here
        return null;
    }
    


    private int getNumberOfReports(){
        // Implement logic to count the number of reports
        return 0;
    }
    
}
