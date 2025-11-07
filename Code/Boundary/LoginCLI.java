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
}