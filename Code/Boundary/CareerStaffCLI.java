package Boundary;

import Control.*;
import Entity.Application;
import Interface.*;
import Entity.InternshipOpportunity;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

public class CareerStaffCLI extends InterfaceCLI {
    
    private AuthenticationControl authCtrl;
    private ApplicationControl appCtrl;
    private InternshipControl intCtrl;
    private ReportControl reportCtrl;


    public CareerStaffCLI(Scanner sc, ApplicationControl appCtrl, InternshipControl intCtrl, ReportControl reportCtrl) {
        super(sc, intCtrl);
        this.appCtrl = appCtrl;
        this.intCtrl = intCtrl;
        this.reportCtrl = reportCtrl;
    }    
    
    public void viewCompanyRepRegisterList() {
        // implementation
    }

    public void approveRegister(String id) {
        // implementation
    }

    public void rejectRegister(String id) {
        // implementation
    }

    public void viewPendingInternshipOpp() {
        // implementation
    }

    public void approveInternshipCreated(InternshipOpportunity opp) {
        // implementation
    }

    public void rejectInternshipCreated(InternshipOpportunity opp) {
        // implementation
    }

    public void viewPendingWithdrawal() {
        // implementation
    }

    public void approveWithdrawal(Application app) {
        
    }

    public void rejectWithdrawal(Application app) {
        // implementation
    }
    public void generateReportOverview(boolean optToSaveReport){
        reportCtrl.generateReportOverview(optToSaveReport);
    }
    public void generateReportSpecific(boolean optToSaveReport,Map<String,List<String>> filterIn){
        reportCtrl.generateReportSpecific(optToSaveReport, filterIn);
    }

}