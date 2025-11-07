package Boundary;

import java.util.Scanner;

import Control.LoginControl;
import Control.AuthenticationControl;

public class LoginCLI {
    private final Scanner sc;
    private final LoginControl loginCtrl;
    private final AuthenticationControl authCtrl;

    public LoginCLI(Scanner sc, LoginControl loginCtrl, AuthenticationControl authCtrl) {
        this.sc = sc;
        this.loginCtrl = loginCtrl;
        this.authCtrl = authCtrl;
    }

    public void run() {
        System.currentTimeMillis();
        System.out.println("Login: Enter any key (except 1);");
        System.out.println("Register As a Company Representative: Enter 1;");
        int choice = sc.nextInt();
        sc.nextLine();  // Consume newline
        if (choice == 1) {
            System.out.print("Enter your name: ");
            String name = sc.nextLine();
            System.out.print("Enter company name: ");
            String companyName = sc.nextLine();
            System.out.print("Enter department: ");
            String department = sc.nextLine();
            System.out.print("Enter position: ");
            String position = sc.nextLine();
            System.out.print("Enter email: ");
            String email = sc.nextLine();
            registerCompanyRep(name, companyName, department, position, email);
            return;
        }
        System.out.print("Enter user ID: ");
        String userID = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        // Delegate to control layer
        // LoginControl already prints the outcome and sets AuthenticationControl's current user
        loginCtrl.handleLogin(userID, password);

        // Optionally, show who is logged in if login succeeded
        if (authCtrl.isLoggedIn()) {
            System.out.println("Logged in as: " + authCtrl.getUserID() + " (" + authCtrl.getUserIdentity() + ")");
        }
    }

    public void registerCompanyRep(String name, String companyName, String department, String position, String email) {
        loginCtrl.handleRegister(name, companyName, department, position, email);
    }
}