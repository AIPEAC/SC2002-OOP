package Boundary;

import Control.*;
import Entity.Application;
import Entity.Report;
import Interface.*;


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
        // ...existing code...
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

    public void approveInternshipCreated(Entity.InternshipOpportunity opp) {
        // implementation
    }

    public void rejectInternshipCreated(Entity.InternshipOpportunity opp) {
        // implementation
    }

    public void viewPendingWithdrawal() {
        // implementation
    }

    public void approveWithdrawal(Entity.Application app) {
        // implementation
    }

    public void rejectWithdrawal(Entity.Application app) {
        // implementation
    }
}