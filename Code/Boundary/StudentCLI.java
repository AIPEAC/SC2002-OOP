package Boundary;

import javax.swing.*;

import Boundary.Helper.UIHelper;
import Control.*;

import java.awt.*;
import java.util.List;

/**
 * Boundary class for student user interface.
 * Provides functionality for students to view internships (filtered by their major),
 * apply for opportunities, check application status, accept/reject offers,
 * and request withdrawals. Students can apply to maximum 3 internships at once
 * and can accept only 1 internship placement.
 * 
 * @author Allen
 * @version 1.0
 */
public class StudentCLI extends AbstractCLI {
    /** Application controller for managing student applications */
    private ApplicationControl appCtrl;
    
    /** Main frame for student interface */
    private JFrame frame;

    /**
     * Constructs a StudentCLI and loads the student's applications.
     * 
     * @param appCtrl the application controller
     * @param intCtrl the internship controller
     * @param loginCtrl the login controller for password changes
     */
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

            JButton check = new JButton("Check My Application Status");
            check.addActionListener(e -> checkMyApplicationStatus());
            p.add(check);

            JButton viewApplied = new JButton("View Internships I Applied To");
            viewApplied.addActionListener(e -> viewInternshipIAppliedTo());
            p.add(viewApplied);

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> {
                UIHelper.closeLoggedInPopup();
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

    private void checkMyApplicationStatus() {
        try {
            appCtrl.loadStudentApplicationFromDB();
            List<String> lines = appCtrl.getApplicationsWithInternshipDetails();
            if (lines == null || lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No applications found.");
                return;
            }
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            // Check if there are approved applications and show warning
            boolean hasApproved = lines.stream().anyMatch(l -> {
                String st = parseFieldValue(l, "status");
                String acc = parseFieldValue(l, "acceptance");
                return "approved".equalsIgnoreCase(st) && !"yes".equalsIgnoreCase(acc) && !"no".equalsIgnoreCase(acc);
            });
            if (hasApproved) {
                JPanel warningPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                warningPanel.setBackground(new Color(255, 250, 205));
                JLabel warningLabel = new JLabel("⚠ Warning: Accepting an internship offer will automatically withdraw all your other applications.");
                warningLabel.setForeground(new Color(184, 134, 11));
                warningLabel.setFont(warningLabel.getFont().deriveFont(Font.BOLD));
                warningPanel.add(warningLabel);
                warningPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
                listPanel.add(warningPanel);
            }

            // Header row
            JPanel header = new JPanel(new GridBagLayout());
            header.setBackground(new Color(240,240,240));
            GridBagConstraints hgb = new GridBagConstraints();
            hgb.insets = new Insets(4,6,4,6);
            hgb.gridy = 0;
            hgb.gridx = 0; hgb.weightx = 0; hgb.fill = GridBagConstraints.NONE; header.add(new JLabel("Actions", SwingConstants.CENTER), hgb);
            hgb.gridx = 1; hgb.weightx = 0.08; hgb.fill = GridBagConstraints.HORIZONTAL; header.add(new JLabel("App#", SwingConstants.CENTER), hgb);
            hgb.gridx = 2; hgb.weightx = 0.1; header.add(new JLabel("ID", SwingConstants.CENTER), hgb);
            hgb.gridx = 3; hgb.weightx = 0.2; header.add(new JLabel("Title", SwingConstants.CENTER), hgb);
            hgb.gridx = 4; hgb.weightx = 0.08; header.add(new JLabel("Level", SwingConstants.CENTER), hgb);
            hgb.gridx = 5; hgb.weightx = 0.12; header.add(new JLabel("Company", SwingConstants.CENTER), hgb);
            hgb.gridx = 6; hgb.weightx = 0.15; header.add(new JLabel("Preferred Majors", SwingConstants.CENTER), hgb);
            hgb.gridx = 7; hgb.weightx = 0.1; header.add(new JLabel("Status", SwingConstants.CENTER), hgb);
            hgb.gridx = 8; hgb.weightx = 0.1; header.add(new JLabel("Acceptance", SwingConstants.CENTER), hgb);
            hgb.gridx = 9; hgb.weightx = 0.07; header.add(new JLabel("Withdraw", SwingConstants.CENTER), hgb);
            header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.GRAY));
            listPanel.add(header);

            for (String line : lines) {
                String appNum = parseFieldValue(line, "applicationNumber");
                String id = parseFieldValue(line, "internshipID");
                String title = parseFieldValue(line, "internshipTitle");
                String level = parseFieldValue(line, "internshipLevel");
                String company = parseFieldValue(line, "companyName");
                String majorsRaw = parseFieldValue(line, "preferredMajors");
                String majors = formatMajorsSimple(majorsRaw);
                String status = parseFieldValue(line, "status");
                String acceptance = parseFieldValue(line, "acceptance");
                String withdrawStatus = parseFieldValue(line, "withdrawStatus");

                JPanel row = new JPanel(new GridBagLayout());
                GridBagConstraints r = new GridBagConstraints();
                r.insets = new Insets(6,6,6,6);
                r.gridy = 0;

                // Actions column
                r.gridx = 0; r.weightx = 0; r.fill = GridBagConstraints.NONE;
                JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                
                String finalAppNum = appNum;
                boolean isWithdrawn = "approved".equalsIgnoreCase(withdrawStatus);
                
                // Accept/Reject buttons for approved applications (only if not already accepted/rejected)
                if ("approved".equalsIgnoreCase(status) && !"yes".equalsIgnoreCase(acceptance) && !"no".equalsIgnoreCase(acceptance)) {
                    JButton acceptBtn = new JButton("Accept");
                    JButton rejectBtn = new JButton("Reject");
                    
                    acceptBtn.addActionListener(e -> {
                        // Confirm acceptance with warning
                        int confirm = JOptionPane.showConfirmDialog(frame, 
                            "Are you sure you want to accept this internship offer?\n" +
                            "All your other applications will be automatically withdrawn.",
                            "Confirm Acceptance", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.WARNING_MESSAGE);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                acceptInternshipOpportunity(Integer.parseInt(finalAppNum));
                                // Disable buttons immediately to prevent double-clicking
                                acceptBtn.setEnabled(false);
                                rejectBtn.setEnabled(false);
                                JOptionPane.showMessageDialog(frame, "Internship accepted! Close this window and reopen to see updated status.");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    rejectBtn.addActionListener(e -> {
                        try {
                            rejectInternshipOpportunity(Integer.parseInt(finalAppNum));
                            // Disable buttons immediately to prevent double-clicking
                            acceptBtn.setEnabled(false);
                            rejectBtn.setEnabled(false);
                            JOptionPane.showMessageDialog(frame, "Offer rejected. Close this window and reopen to see updated status.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    actionsPanel.add(acceptBtn);
                    actionsPanel.add(rejectBtn);
                }
                
                // Withdraw button - available for any application that hasn't been withdrawn yet
                // Can withdraw: pending applications, approved applications, even accepted ones
                // Cannot withdraw: already withdrawn or withdrawal pending
                boolean canWithdraw = !isWithdrawn && (withdrawStatus == null || "N/A".equals(withdrawStatus) || "".equals(withdrawStatus.trim()));
                
                if (canWithdraw) {
                    JButton withdrawBtn = new JButton("Withdraw");
                    withdrawBtn.addActionListener(e -> {
                        try {
                            appCtrl.requestWithdrawApplication(Integer.parseInt(finalAppNum));
                            JOptionPane.showMessageDialog(frame, "Withdrawal request submitted. Awaiting staff approval.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    actionsPanel.add(withdrawBtn);
                }
                
                actionsPanel.setPreferredSize(new Dimension(220, 28));
                row.add(actionsPanel, r);

                r.gridx = 1; r.weightx = 0.08; r.fill = GridBagConstraints.HORIZONTAL; row.add(new JLabel(appNum != null ? appNum : ""), r);
                r.gridx = 2; r.weightx = 0.1; row.add(new JLabel(id != null ? id : ""), r);
                r.gridx = 3; r.weightx = 0.2; row.add(new JLabel(title != null ? title : ""), r);
                r.gridx = 4; r.weightx = 0.08; row.add(new JLabel(level != null ? level : ""), r);
                r.gridx = 5; r.weightx = 0.12; row.add(new JLabel(company != null ? company : ""), r);
                r.gridx = 6; r.weightx = 0.15; row.add(new JLabel(majors), r);
                r.gridx = 7; r.weightx = 0.1; row.add(new JLabel(status != null ? status : ""), r);
                r.gridx = 8; r.weightx = 0.1; row.add(new JLabel(acceptance != null ? acceptance : "N/A"), r);
                r.gridx = 9; r.weightx = 0.07; row.add(new JLabel(withdrawStatus != null ? withdrawStatus : "N/A"), r);
                row.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
                listPanel.add(row);
            }

            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(1200, 400));
            JOptionPane.showMessageDialog(frame, sp, "My Applications", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewInternshipIAppliedTo() {
        try {
            appCtrl.loadStudentApplicationFromDB();
            List<String> lines = appCtrl.getApplicationsWithInternshipDetails();
            if (lines == null || lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No internships found for your applications.");
                return;
            }
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            // Header row
            JPanel header = new JPanel(new GridBagLayout());
            header.setBackground(new Color(240,240,240));
            GridBagConstraints hgb = new GridBagConstraints();
            hgb.insets = new Insets(4,6,4,6);
            hgb.gridy = 0;
            hgb.gridx = 0; hgb.weightx = 0.1; hgb.fill = GridBagConstraints.HORIZONTAL; header.add(new JLabel("App#", SwingConstants.CENTER), hgb);
            hgb.gridx = 1; hgb.weightx = 0.12; header.add(new JLabel("ID", SwingConstants.CENTER), hgb);
            hgb.gridx = 2; hgb.weightx = 0.25; header.add(new JLabel("Title", SwingConstants.CENTER), hgb);
            hgb.gridx = 3; hgb.weightx = 0.1; header.add(new JLabel("Level", SwingConstants.CENTER), hgb);
            hgb.gridx = 4; hgb.weightx = 0.15; header.add(new JLabel("Company", SwingConstants.CENTER), hgb);
            hgb.gridx = 5; hgb.weightx = 0.18; header.add(new JLabel("Preferred Majors", SwingConstants.CENTER), hgb);
            hgb.gridx = 6; hgb.weightx = 0.1; header.add(new JLabel("Status", SwingConstants.CENTER), hgb);
            header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.GRAY));
            listPanel.add(header);

            for (String line : lines) {
                String appNum = parseFieldValue(line, "applicationNumber");
                String id = parseFieldValue(line, "internshipID");
                String title = parseFieldValue(line, "internshipTitle");
                String level = parseFieldValue(line, "internshipLevel");
                String company = parseFieldValue(line, "companyName");
                String majorsRaw = parseFieldValue(line, "preferredMajors");
                String majors = formatMajorsSimple(majorsRaw);
                String status = parseFieldValue(line, "status");

                JPanel row = new JPanel(new GridBagLayout());
                GridBagConstraints r = new GridBagConstraints();
                r.insets = new Insets(6,6,6,6);
                r.gridy = 0;

                r.gridx = 0; r.weightx = 0.1; r.fill = GridBagConstraints.HORIZONTAL; row.add(new JLabel(appNum != null ? appNum : ""), r);
                r.gridx = 1; r.weightx = 0.12; row.add(new JLabel(id != null ? id : ""), r);
                r.gridx = 2; r.weightx = 0.25; row.add(new JLabel(title != null ? title : ""), r);
                r.gridx = 3; r.weightx = 0.1; row.add(new JLabel(level != null ? level : ""), r);
                r.gridx = 4; r.weightx = 0.15; row.add(new JLabel(company != null ? company : ""), r);
                r.gridx = 5; r.weightx = 0.18; row.add(new JLabel(majors), r);
                r.gridx = 6; r.weightx = 0.1; row.add(new JLabel(status != null ? status : ""), r);
                row.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
                listPanel.add(row);
            }

            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(1000, 400));
            JOptionPane.showMessageDialog(frame, sp, "Internships I Applied To", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acceptInternshipOpportunity(int appNum) {
        appCtrl.acceptOffer(appNum);
    }

    private void rejectInternshipOpportunity(int appNum) {
        appCtrl.rejectOffer(appNum);
        JOptionPane.showMessageDialog(frame, "You have rejected this internship offer.");
    }

    private String parseFieldValue(String line, String key) {
        String marker = key + "=";
        int idx = line.indexOf(marker);
        if (idx < 0) return null;
        int start = idx + marker.length();
        String DELIM = " | ";
        int end = line.indexOf(DELIM, start);
        if (end < 0) end = line.length();
        return line.substring(start, end).trim();
    }

    private String formatMajorsSimple(String raw) {
        if (raw == null) return "[]";
        String r = raw.trim();
        if (r.isEmpty()) return "[]";
        while (r.startsWith("[[") && r.endsWith("]]")) {
            r = r.substring(1, r.length()-1).trim();
        }
        if (r.startsWith("[") && r.endsWith("]")) {
            return r;
        }
        return "[" + r + "]";
    }
}
