package Boundary;

import javax.swing.*;

import Boundary.Helper.UIHelper;
import Control.*;

import java.awt.*;

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
     * @throws Exception if login fails due to invalid credentials
     */
    public void run() throws Exception {
        String[] options = {"Login", "Register as Company Rep", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "Welcome - choose an action", "Login",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == 1) {
            registerCompanyRep();
            return;
        } else if (choice != 0) {
            return;
        }

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Arial", Font.PLAIN, 20));
        userField.setPreferredSize(new Dimension(300, 50));
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Arial", Font.PLAIN, 20));
        passField.setPreferredSize(new Dimension(300, 50));
        JLabel userLabel = new JLabel("User ID:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        int res = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
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
                throw e; // Re-throw the exception to signal login failure
            }
        }
    }

    /**
     * Registers a new company representative.
     */
    private void registerCompanyRep() {
        JTextField name = new JTextField(30);
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setPreferredSize(new Dimension(350, 40));
        JTextField company = new JTextField(30);
        company.setFont(new Font("Arial", Font.PLAIN, 18));
        company.setPreferredSize(new Dimension(350, 40));
        JTextField dept = new JTextField(30);
        dept.setFont(new Font("Arial", Font.PLAIN, 18));
        dept.setPreferredSize(new Dimension(350, 40));
        JTextField position = new JTextField(30);
        position.setFont(new Font("Arial", Font.PLAIN, 18));
        position.setPreferredSize(new Dimension(350, 40));
        JTextField email = new JTextField(30);
        email.setFont(new Font("Arial", Font.PLAIN, 18));
        email.setPreferredSize(new Dimension(350, 40));
        
        JLabel nameLabel = new JLabel("*Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel companyLabel = new JLabel("*Company:");
        companyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel posLabel = new JLabel("Position:");
        posLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel emailLabel = new JLabel("*Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(nameLabel);
        panel.add(name);
        panel.add(companyLabel);
        panel.add(company);
        panel.add(deptLabel);
        panel.add(dept);
        panel.add(posLabel);
        panel.add(position);
        panel.add(emailLabel);
        panel.add(email);
        
        int res = JOptionPane.showConfirmDialog(null, panel, "Register Company Representative", JOptionPane.OK_CANCEL_OPTION);
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
