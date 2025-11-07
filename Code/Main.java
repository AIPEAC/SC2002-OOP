
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
            // Generic login first
            LoginCLI loginCLI = new LoginCLI(sc, loginCtrl, authCtrl);
            loginCLI.run();

            if (!authCtrl.isLoggedIn()) {
                System.out.println("Exiting application (login failed).");
                return;
            }

            // Dispatch based on identity
            String identity = authCtrl.getUserIdentity();
            switch (identity) {
                case "CareerStaff": {
                    System.out.println("Launching Career Staff console...");
                    // Placeholder: instantiate and immediately enter (future) staff menu loop
                    new CareerStaffCLI(sc, authCtrl, appCtrl, intCtrl, reportCtrl); // instance retained only if needed later
                    break;
                }
                case "Student": {
                    System.out.println("Launching Student console...");
                    new StudentCLI(sc, loginCtrl, appCtrl, authCtrl, intCtrl);
                    
                    break;
                }
                case "CompanyRepresentative": {
                    System.out.println("Launching Company Representative console...");
                    new CompanyRepresentativeCLI(sc, authCtrl, intCtrl);
                    break;
                }
                default:
                    System.out.println("Unknown identity: " + identity + ". Exiting.");
            }
        } finally {
            sc.close();
        }
    }
}