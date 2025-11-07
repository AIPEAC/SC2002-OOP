
import java.util.Scanner;
import Boundary.*;
import Control.*;

public class Main {

    public static void main(String[] args) {
        // Initialize shared controls and services (single instances for consistent state)
        AuthenticationControl authCtrl = new AuthenticationControl();
        UserLoginDirectoryControl userLoginDirCtrl = new UserLoginDirectoryControl();
        LoginControl loginCtrl = new LoginControl(authCtrl, userLoginDirCtrl);
        ApplicationControl appCtrl = new ApplicationControl(authCtrl);
        InternshipControl intCtrl = new InternshipControl(authCtrl);
        ReportControl reportCtrl = new ReportControl(authCtrl, intCtrl);

        Scanner sc = new Scanner(System.in);
        try {
            String domain = null;
            while (true) {
                System.out.println("Please choose domain to login: 1. CareerStaff 2. Student 3. CompanyRepresentative");
                domain = sc.nextLine().trim();
                if (domain.equals("1") || domain.equals("2") || domain.equals("3")) break;
                System.out.println("Invalid selection. Please enter 1, 2, or 3.");
            }

            // Collect credentials (UI) and delegate authentication to the selected CLI
            System.out.println("Enter userID:");
            String userID = sc.nextLine().trim();
            System.out.println("Enter password:");
            String password = sc.nextLine();

            switch (domain) {
                case "1": {
                    System.out.println("Welcome, Career Staff. Launching staff console (login via CLI)...");
                    CareerStaffCLI staffCLI = new CareerStaffCLI(loginCtrl, appCtrl, intCtrl, reportCtrl);
                    staffCLI.login(userID, password);
                    break;
                }
                case "2": {
                    System.out.println("Welcome, Student. Launching student console (login via CLI)...");
                    StudentCLI studentCLI = new StudentCLI(loginCtrl, appCtrl);
                    studentCLI.login(userID, password);
                    break;
                }
                case "3": {
                    System.out.println("Welcome, Company Representative. Launching company console (login via CLI)...");
                    CompanyRepresentativeCLI compRepCLI = new CompanyRepresentativeCLI(loginCtrl, intCtrl);
                    compRepCLI.login(userID, password);
                    break;
                }
                default:
                    // Should never happen due to earlier validation
                    break;
            }
        } finally {
            sc.close();
        }
    }
}