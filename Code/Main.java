
import java.util.Scanner;
import Boundary.*;
import Control.*;
import Interface.*;
import Entity.*;
import Entity.User.*;


public class Main {
    private static void initialization(){
        LoginControl loginCtrl = new LoginControl();
        AuthenticationControl authCtrl = new AuthenticationControl();
        ApplicationControl appCtrl = new ApplicationControl();
        IntershipControl intCtrl = new IntershipOpportunityControl();
    }

    public static void main(String[] args) {
        initialization();
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choose domain to login: 1. CareerStaff 2. Student 3. CompanyRepresentative");
        String domain=sc.next();
        
        domainChoose: while (true) {
            System.out.println("enter userID");
            String userID=sc.next();
            System.out.println("enter password");
            String password=sc.next();
            switch (domain) {
                case "1":
                    UserControl userCtrl = new UserControl();
                    ReportControl reportCtrl = new ReportControl();
                    CareerStaffCLI careerStaffCLI = new CareerStaffCLI();
                    careerStaffCLI.login(userID, password);
                    break domainChoose;
                case "2":
                    StudentCLI studentCLI = new StudentCLI();
                    studentCLI.login(userID, password);
                    break domainChoose;
                case "3":
                    CompanyRepresentativeCLI companyRepCLI = new CompanyRepresentativeCLI();
                    companyRepCLI.login(userID, password);
                    break domainChoose;
                default:
                    System.out.println("Invalid domain. Please choose again: 1. CareerStaff 2. Student 3. CompanyRepresentative");
                    domain = sc.next();
            }
            sc.close();
        }
        
    }
}