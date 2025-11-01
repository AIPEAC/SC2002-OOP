package Boundary;

import Control.*;
import Entity.Report;
import Interface.*;


public class CareerStaffCLI implements InterfaceCLI {
    
    private LoginControl loginCtrl;
    private AuthenticationControl authCtrl;
    private InternshipControl intCtrl;
    private ReportControl reportCtrl;


    public CareerStaffCLI(LoginControl loginCtrl, AuthenticationControl authCtrl, InternshipControl intCtrl, ReportControl reportCtrl) {
        this.loginCtrl = loginCtrl;
        this.authCtrl = authCtrl;
        this.intCtrl = intCtrl;
        this.reportCtrl = reportCtrl;
    }

    public CareerStaffCLI(LoginControl loginCtrl, AuthenticationControl authCtrl, InternshipControl intCtrl){
        
    }
    @Override
    public void login(String userID, String password) {
        //
    }
    @Override
    public void changePassowrd(String originalPassword, String newPassword){
        //
    }
    
    public void generateReportOverview(boolean optToSaveReport){
        reportCtrl.generateReportOverview(optToSaveReport);
    }
    public void genereteReportSpecific(boolean optToSaveReport,String filterOrder,boolean ascending,String[] filterOut){
        reportCtrl.generateReportSpecific(optToSaveReport,filterOrder,ascending,filterOut);
    }
}