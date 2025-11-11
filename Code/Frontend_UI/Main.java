package Frontend_UI;

import Backend.Control.*;
import Frontend_UI.Boundary.*;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Internship Application Management System.
 * Uses centralized initialization to ensure proper setup of all backend controllers
 * and maintains separation between frontend and backend layers.
 * 
 * @author Allen
 * @version 2.0
 */
public class Main {
    public static void main(String[] args) {
        // Initialize all backend controllers through centralized initialization
        // This prevents the frontend from directly instantiating or manipulating backend components
        InitializeControl initCtrl = new InitializeControl();
        
        // Get controller instances from the initializer
        AuthenticationControl authCtrl = initCtrl.getAuthenticationControl();
        LoginControl loginCtrl = initCtrl.getLoginControl();
        InternshipControl intCtrl = initCtrl.getInternshipControl();
        ApplicationControl appCtrl = initCtrl.getApplicationControl();
        ReportControl reportCtrl = initCtrl.getReportControl();
        UserControl userCtrl = initCtrl.getUserControl();

        // Launch UI login and then dispatch to role-specific UIs
        SwingUtilities.invokeLater(() -> {
            LoginCLI loginUI = new LoginCLI(loginCtrl, authCtrl);
            loginUI.run();

            // Wait briefly to allow user to interact with login dialog and backend to set auth state
            try { Thread.sleep(300); } catch (InterruptedException e) { }

            if (!authCtrl.isLoggedIn()) {
                System.out.println("Exiting application (login failed or cancelled).");
                return;
            }

            String identity = authCtrl.getUserIdentity();
            switch (identity) {
                case "CareerStaff":
                case "Career Staff":
                    CareerStaffCLI staffUI = new CareerStaffCLI(appCtrl, intCtrl, reportCtrl, userCtrl, loginCtrl);
                    staffUI.show();
                    break;
                case "Student":
                    StudentCLI studentUI = new StudentCLI(appCtrl, intCtrl, loginCtrl);
                    studentUI.show();
                    break;
                case "CompanyRepresentative":
                case "Company Representative":
                    CompanyRepresentativeCLI compUI = new CompanyRepresentativeCLI(intCtrl, loginCtrl);
                    compUI.show();
                    break;
                default:
                    System.out.println("Unknown identity: " + identity + ". Exiting.");
            }
        });
    }
}
