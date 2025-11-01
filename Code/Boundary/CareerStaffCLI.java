package Boundary;

import Control.*;
import Entity.Application;
import Entity.Report;
import Interface.*;
import Entity.InternshipOpportunity;

public class CareerStaffCLI implements InterfaceCLI {
    
    private LoginControl loginCtrl;
    private ApplicationControl appCtrl;
    private InternshipControl intCtrl;
    private ReportControl reportCtrl;


    public CareerStaffCLI(LoginControl loginCtrl, ApplicationControl appCtrl, InternshipControl intCtrl, ReportControl reportCtrl) {
        this.loginCtrl = loginCtrl;
        this.appCtrl = appCtrl;
        this.intCtrl = intCtrl;
        this.reportCtrl = reportCtrl;
    }



    
    public void generateReportOverview(boolean optToSaveReport){
        reportCtrl.generateReportOverview(optToSaveReport);
    }
    public void genereteReportSpecific(boolean optToSaveReport,String filterOrder,boolean ascending,String[] filterOut){
        reportCtrl.generateReportSpecific(optToSaveReport, filterOrder, ascending, filterOut);
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
}