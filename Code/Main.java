

import javax.swing.SwingUtilities;

import Boundary.*;
import Control.*;

/**
 * Main entry point for the Internship Application Management System.
 * Uses centralized initialization to ensure proper setup of all controllers
 * and maintains separation between layers.
 * 
 * @author Allen
 * @version 2.0
 */
public class Main {
    /**
     * Private constructor to prevent instantiation of this main class.
     */
    private Main() {
    }
    
    /**
     * The main entry point for the application.
     * Initializes all controllers and starts the login interface.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize all controllers through centralized initialization
        // This prevents the frontend from directly instantiating or manipulating control components
        ControlInitializer initCtrl = new ControlInitializer();
        
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

            // Wait briefly to allow user to interact with login dialog and control to set auth state
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
