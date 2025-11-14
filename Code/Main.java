

import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;

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
     * Runs in an infinite loop - when the window closes, all objects are destroyed
     * and the application restarts from the beginning.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        while (true) {
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

            // Use a latch to wait for UI to finish
            CountDownLatch uiFinished = new CountDownLatch(1);
            boolean[] shouldRestart = {false}; // Flag to determine if we should restart
            AbstractCLI[] cliToWaitFor = {null}; // Store reference to CLI to wait for

            // Launch UI login and then dispatch to role-specific UIs
            SwingUtilities.invokeLater(() -> {
                try {
                    LoginCLI loginUI = new LoginCLI(loginCtrl, authCtrl);
                    loginUI.run();

                    // Wait briefly to allow user to interact with login dialog and control to set auth state
                    try { Thread.sleep(300); } catch (InterruptedException e) { }

                    if (!authCtrl.isLoggedIn()) {
                        System.out.println("No user logged in. Exiting application.");
                        shouldRestart[0] = false; // Don't restart - user cancelled or closed window
                        return;
                    }

                    String identity = authCtrl.getUserIdentity();
                    switch (identity) {
                        case "CareerStaff":
                        case "Career Staff":
                            CareerStaffCLI staffUI = new CareerStaffCLI(appCtrl, intCtrl, reportCtrl, userCtrl, loginCtrl);
                            staffUI.show();
                            cliToWaitFor[0] = staffUI; // Store reference to wait for later
                            shouldRestart[0] = true; // Mark for restart when CLI closes
                            break;
                        case "Student":
                            StudentCLI studentUI = new StudentCLI(appCtrl, intCtrl, loginCtrl);
                            studentUI.show();
                            cliToWaitFor[0] = studentUI; // Store reference to wait for later
                            shouldRestart[0] = true; // Mark for restart when CLI closes
                            break;
                        case "CompanyRepresentative":
                        case "Company Representative":
                            CompanyRepresentativeCLI compUI = new CompanyRepresentativeCLI(intCtrl, loginCtrl);
                            compUI.show();
                            cliToWaitFor[0] = compUI; // Store reference to wait for later
                            shouldRestart[0] = true; // Mark for restart when CLI closes
                            break;
                        default:
                            System.out.println("Unknown identity: " + identity + ". Exiting.");
                            shouldRestart[0] = false;
                    }
                } catch (Exception e) {
                    // Login failed with exception (wrong credentials, etc.)
                    System.out.println("Login failed. Restarting with fresh login screen...");
                    shouldRestart[0] = true;
                } finally {
                    // Signal that UI has been dispatched
                    uiFinished.countDown();
                }
            });
            
            // Wait for the UI to be dispatched
            try {
                uiFinished.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Now wait for the CLI window to close (if one was shown)
            if (cliToWaitFor[0] != null) {
                try {
                    cliToWaitFor[0].waitForWindowClose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // If shouldRestart is false, exit the loop and terminate
            if (!shouldRestart[0]) {
                break;
            }
        }
    }
}
