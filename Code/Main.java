
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

            // Login loop
            while (true) {
                System.out.println("Enter userID:");
                String userID = sc.nextLine().trim();
                System.out.println("Enter password:");
                String password = sc.nextLine();

                // Perform login
                loginCtrl.handleLogin(userID, password);

                if (!authCtrl.isLoggedIn()) {
                    System.out.println("Login failed. Try again? (y/n)");
                    String retry = sc.nextLine().trim().toLowerCase();
                    if (!retry.equals("y")) {
                        System.out.println("Exiting.");
                        return;
                    }
                    continue;
                }

                String identity = authCtrl.getUserIdentity();
                // Validate role matches selected domain
                boolean roleMatch =
                    (domain.equals("1") && "CareerStaff".equals(identity)) ||
                    (domain.equals("2") && "Student".equals(identity)) ||
                    (domain.equals("3") && "CompanyRepresentative".equals(identity));

                if (!roleMatch) {
                    System.out.printf("You logged in as %s but selected a different domain. Please select again.\n", identity);
                    authCtrl.setLoggedin(null); // logout
                    continue;
                }

                // Hand off to the appropriate CLI (placeholder for actual menu handling)
                switch (domain) {
                    case "1":
                        System.out.println("Welcome, Career Staff. Launching staff console...");
                        // Initialize CLI for this role (menu handling to be implemented inside CLI)
                        new CareerStaffCLI(loginCtrl, appCtrl, intCtrl, reportCtrl);
                        // TODO: call staffCLI.run(authCtrl.getUser());
                        break;
                    case "2":
                        System.out.println("Welcome, Student. Launching student console...");
                        new StudentCLI(loginCtrl, appCtrl);
                        // TODO: call studentCLI.run(authCtrl.getUser());
                        break;
                    case "3":
                        System.out.println("Welcome, Company Representative. Launching company console...");
                        new CompanyRepresentativeCLI(loginCtrl, intCtrl);
                        // TODO: call compRepCLI.run(authCtrl.getUser());
                        break;
                    default:
                        // Should never happen due to earlier validation
                        break;
                }

                // Exit after successful handoff for now (menus not implemented yet)
                break;
            }
        } finally {
            sc.close();
        }
    }
}