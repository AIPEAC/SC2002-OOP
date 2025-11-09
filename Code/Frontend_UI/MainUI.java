package Frontend_UI;

import Backend.Control.*;
import Frontend_UI.Boundary.*;
import javax.swing.SwingUtilities;

public class MainUI {
    public static void main(String[] args) {
        // Build backend controls the same way as the console main
        AuthenticationControl authCtrl = new AuthenticationControl();
        UserLoginDirectoryControl userLoginDirCtrl = new UserLoginDirectoryControl(authCtrl);
        LoginControl loginCtrl = new LoginControl(authCtrl, userLoginDirCtrl);
        InternshipControl intCtrl = new InternshipControl(authCtrl);
        ApplicationControl appCtrl = new ApplicationControl(authCtrl, intCtrl);
        intCtrl.setApplicationControl(appCtrl);
        ReportControl reportCtrl = new ReportControl(authCtrl, intCtrl);
        UserControl userCtrl = new UserControl(userLoginDirCtrl, authCtrl);

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
                    CompanyRepresentativeCLI compUI = new CompanyRepresentativeCLI(intCtrl, loginCtrl, authCtrl);
                    compUI.show();
                    break;
                default:
                    System.out.println("Unknown identity: " + identity + ". Exiting.");
            }
        });
    }
}
