package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import Backend.Control.*;
import Frontend_UI.Helper.UIHelper;

public class StudentCLI extends AbstractCLI {
    private ApplicationControl appCtrl;
    private JFrame frame;

    public StudentCLI(ApplicationControl appCtrl, InternshipControl intCtrl, LoginControl loginCtrl) {
        super(intCtrl);
        this.appCtrl = appCtrl;
        setLoginControl(loginCtrl);
        appCtrl.loadStudentApplicationFromDB();
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Student");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(480,360);
            frame.setLocationRelativeTo(null);
            JPanel p = new JPanel(new GridLayout(0,1,5,5));

            JButton chPwd = new JButton("Change Password");
            chPwd.addActionListener(e -> changePassword());
            p.add(chPwd);

            JButton viewOpp = new JButton("View Internship Opportunities");
            viewOpp.addActionListener(e -> viewFilteredInternshipOpportunities());
            p.add(viewOpp);

            JButton submit = new JButton("Submit Internship Application");
            submit.addActionListener(e -> submitApplication());
            p.add(submit);

            JButton check = new JButton("Check My Application Status");
            check.addActionListener(e -> checkMyApplicationStatus());
            p.add(check);

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> {
                Frontend_UI.Helper.UIHelper.closeLoggedInPopup();
                frame.dispose();
            });
            p.add(logout);

            frame.setContentPane(p);
            frame.setVisible(true);
        });
    }

    // changePassword and viewFilteredInternshipOpportunities inherited from AbstractCLI

    @Override
    protected void onApply(String internshipID) {
        if (internshipID == null || internshipID.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid Internship ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int appNum = appCtrl.makeApplication(internshipID);
            JOptionPane.showMessageDialog(frame, "Application submitted successfully. Application Number: " + appNum);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitApplication() {
        String id = JOptionPane.showInputDialog(frame, "Enter Internship ID:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            int appNum = appCtrl.makeApplication(id);
            JOptionPane.showMessageDialog(frame, "Application submitted successfully. Application Number: " + appNum);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkMyApplicationStatus() {
        appCtrl.loadStudentApplicationFromDB();
        List<String> lines = appCtrl.getApplicationsForDisplay();
        if (lines == null || lines.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No applications found.");
            return;
        }
        JTextArea ta = new JTextArea(String.join("\n", lines));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(600,300));
        JOptionPane.showMessageDialog(frame, sp, "My Applications", JOptionPane.INFORMATION_MESSAGE);

        if (appCtrl.hasAcceptedOffer()) {
            if (UIHelper.showYesNo("You have accepted an internship offer. Do you want to view the internships you applied to?")) {
                viewInternshipIAppliedTo();
            }
            if (UIHelper.showYesNo("Do you want to withdraw this application?")) {
                withdrawApplication();
            }
        } else if (appCtrl.hasApprovedApplication()) {
            List<String> approved = appCtrl.getApprovedApplicationInternshipCompaniesAndIDs();
            JOptionPane.showMessageDialog(frame, String.join("\n", approved), "Approved applications", JOptionPane.INFORMATION_MESSAGE);
            String appNumStr = JOptionPane.showInputDialog(frame, "Enter Application Number to respond to offer, or press 0 to check the internships you applied to:");
            if (appNumStr == null) return;
            int appNum = Integer.parseInt(appNumStr);
            if (appNum == 0) { viewInternshipIAppliedTo(); return; }
            String[] options = {"Accept","Reject","Cancel"};
            int rsp = JOptionPane.showOptionDialog(frame, "Accept or reject the offer?", "Respond", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (rsp == 0) acceptInternshipOpportunity(appNum);
            else if (rsp == 1) rejectInternshipOpportunity(appNum);
        }
    }

    private void viewInternshipIAppliedTo() {
        appCtrl.loadStudentApplicationFromDB();
        List<String> lines = appCtrl.viewInternshipsAppliedTo();
        if (lines == null || lines.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No internships found for your applications.");
            return;
        }
        JTextArea ta = new JTextArea(String.join("\n", lines));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(600,300));
        JOptionPane.showMessageDialog(frame, sp, "Internships Applied To", JOptionPane.INFORMATION_MESSAGE);
    }

    private void withdrawApplication() {
        String input = JOptionPane.showInputDialog(frame, "Enter Application Number to withdraw, or leave blank to cancel:");
        if (input == null || input.trim().isEmpty()) { JOptionPane.showMessageDialog(frame, "Withdrawal cancelled."); return; }
        int appNumber = Integer.parseInt(input);
        appCtrl.requestWithdrawApplication(appNumber);
        JOptionPane.showMessageDialog(frame, "Withdrawal requested.");
    }

    private void acceptInternshipOpportunity(int appNum) {
        appCtrl.acceptOffer(appNum);
        JOptionPane.showMessageDialog(frame, "Internship accepted successfully! Other pending applications will be withdrawn.");
    }

    private void rejectInternshipOpportunity(int appNum) {
        appCtrl.rejectOffer(appNum);
        JOptionPane.showMessageDialog(frame, "You have rejected this internship offer.");
    }
}
