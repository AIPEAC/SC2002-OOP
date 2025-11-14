package Boundary;

import javax.swing.*;

import Boundary.Helper.UIHelper;
import Control.*;

/**
 * Boundary class for handling login and registration user interface.
 * Provides dialogs for user authentication and company representative registration.
 * Routes authenticated users to their role-specific interfaces.
 * 
 * @author Allen
 * @version 1.0
 */
public class LoginCLI {
    /** Login controller for handling authentication */
    private final LoginControl loginCtrl;
    
    /** Authentication controller for session management */
    private final AuthenticationControl authCtrl;

    /**
     * Constructs a LoginCLI with required controllers.
     * 
     * @param loginCtrl the login controller
     * @param authCtrl the authentication controller
     */
    public LoginCLI(LoginControl loginCtrl, AuthenticationControl authCtrl) {
        this.loginCtrl = loginCtrl;
        this.authCtrl = authCtrl;
    }

    /**
     * Runs the login CLI dialog, handling user authentication and registration.
     */
    public void run() {
        String[] options = {"Login", "Register as Company Rep", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "Welcome - choose an action", "Login",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == 1) {
            registerCompanyRep();
            return;
        } else if (choice != 0) {
            return;
        }

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] loginFields = {"User ID:", userField, "Password:", passField};
        int res = JOptionPane.showConfirmDialog(null, loginFields, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String userID = userField.getText();
            String password = new String(passField.getPassword());
            try {
                loginCtrl.handleLogin(userID, password);
                JOptionPane.showMessageDialog(null, "Login successful.");
                boolean loggedIn=authCtrl.isLoggedIn();
                if (loggedIn) {
                    UIHelper.showLoggedInPopup(authCtrl.getUserID(), authCtrl.getUserIdentity());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Registers a new company representative.
     */
    private void registerCompanyRep() {
        JTextField name = new JTextField();
        JTextField company = new JTextField();
        JTextField dept = new JTextField();
        JTextField position = new JTextField();
        JTextField email = new JTextField();
        Object[] fields = {"Name:", name, "Company:", company, "Department:", dept, "Position:", position, "Email:", email};
        int res = JOptionPane.showConfirmDialog(null, fields, "Register Company Representative", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String userID = loginCtrl.handleRegister(name.getText(), company.getText(), dept.getText(), position.getText(), email.getText());
                JOptionPane.showMessageDialog(null, "Successfully created. Please wait for account approval from staff. Your UserID is: " + userID + "\nDefault password: 'password'", "Registered", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Register Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
